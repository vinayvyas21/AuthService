package com.vk.auth.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.auth.config.KafkaProducerClient;
import com.vk.auth.converters.UserConverter;
import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.RequestStatus;
import com.vk.auth.dtos.SendEmailDto;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.ActiveSessionsLimitationException;
import com.vk.auth.exceptions.InvalidTokenException;
import com.vk.auth.exceptions.UserAlreadyExistsException;
import com.vk.auth.exceptions.UserNotFoundException;
import com.vk.auth.exceptions.WrongPasswordException;
import com.vk.auth.models.Role;
import com.vk.auth.models.Session;
import com.vk.auth.models.SessionStatus;
import com.vk.auth.models.User;
import com.vk.auth.repositories.RoleRepository;
import com.vk.auth.repositories.SessionRepository;
import com.vk.auth.repositories.UserRepository;
import com.vk.auth.utils.JwtUtil;

@Service
public class IUserService implements UserService {

	UserRepository userRepository;

	SessionRepository sessionRepository;

	BCryptPasswordEncoder encoder;

	RoleRepository roleRepository;

	private KafkaProducerClient kafkaProducerClient;
	private ObjectMapper objectMapper;

	@Autowired
	private JwtUtil jwtUtil;

	public IUserService(UserRepository userRepository, BCryptPasswordEncoder encoder,
			SessionRepository sessionRepository, RoleRepository roleRepository, KafkaProducerClient kafkaProducerClient,
			ObjectMapper objectMapper) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.sessionRepository = sessionRepository;
		this.roleRepository = roleRepository;
		this.kafkaProducerClient = kafkaProducerClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public UserSignUpResponseDto createUser(UserSignUpRequestDto request) throws UserAlreadyExistsException {
		if (this.userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("User already exists with the email : " + request.getEmail());
		}
		User user = UserConverter.convertUserSignUpRequestToUser(request);
		user.setPasswordSalt(hashPassword(request.getPassword()));
		User savedUser = userRepository.save(user);

		// Once the signup is complete, send a message to Kafka for sending an email to
		// the User.
		SendEmailDto sendEmailDto = new SendEmailDto();
		sendEmailDto.setTo(user.getEmail());
		sendEmailDto.setFrom("admin@scaler.com");
		sendEmailDto.setSubject("Welcome to Scaler");
		sendEmailDto.setBody("Thanks for joining Scaler");

		try {
			kafkaProducerClient.sendMessage("sendEmail", objectMapper.writeValueAsString(sendEmailDto));
		} catch (JsonProcessingException e) {
			System.out.println("Something went wrong while sending a message to Kafka");
		}

		UserSignUpResponseDto responseDTO = new UserSignUpResponseDto();
		if (savedUser != null) {
			responseDTO.setRequestStatus(RequestStatus.SUCCESS);
		} else {
			responseDTO.setRequestStatus(RequestStatus.FAILURE);
		}

		return responseDTO;
	}

	@Override
	public boolean updateUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUser(Long id) {
		Optional<User> user = this.userRepository.findById(id);
		if (user.isPresent()) {
			userRepository.delete(user.get());
		}
		return true;
	}

	@Override
	public String login(LoginRequestDto request) {
		Optional<User> user = userRepository.findByEmail(request.getEmail());
		String token = null;
		if (user.isPresent()) {
			if (verifyPassword(request.getPassword(), user.get().getPasswordSalt())) {

				List<Session> sessions = sessionRepository.findByUserId(user.get().getId());
				long activeSessions = sessions.stream()
						.filter(session -> session.getSessionStatus() == SessionStatus.ACTIVE).count();

				if (activeSessions == 2) {
					throw new ActiveSessionsLimitationException("Only 2 active sessions are allowed");
				}

				token = jwtUtil.generateToken(user.get().getEmail());

				Session session = new Session();
				session.setUser(user.get());
				session.setToken(token);
				session.setSessionStatus(SessionStatus.ACTIVE);
				sessionRepository.save(session);

				Optional<Role> userRoleOptional = roleRepository.findByName("Reader");

				if (userRoleOptional.isPresent()) {
					user.get().setRoles(Set.of(userRoleOptional.get()));
				}

				userRepository.save(user.get());
			} else {
				throw new WrongPasswordException("Password is wrong");
			}
		} else {
			throw new UserNotFoundException("User is not found with email: " + request.getEmail());
		}

		return token;
	}

	public String hashPassword(String plainPassword) {
		return encoder.encode(plainPassword);
	}

	public boolean verifyPassword(String plainPassword, String hashedPassword) {
		return encoder.matches(plainPassword, hashedPassword);
	}

	@Override
	public boolean validate(String token) {
		return jwtUtil.isTokenValid(token);

	}

	@Override
	public boolean logout(String token) throws InvalidTokenException {
		if (jwtUtil.isTokenValid(token)) {
			Optional<Session> sessionOptional = sessionRepository.findByToken(token);
			if (sessionOptional.isPresent()) {
				Session session = sessionOptional.get();
				session.setSessionStatus(SessionStatus.ENDED);
				sessionRepository.save(session);
				return true;
			} else {
				throw new InvalidTokenException("Invalid token");
			}
		} else {
			throw new InvalidTokenException("Invalid token");
		}
	}

	@Override
	public void logoutAll(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("User is not found for Id: " + userId);
		}

		List<Session> sessionsList = sessionRepository.findByUserId(userId);

		sessionsList.stream().forEach(session -> {
			session.setSessionStatus(SessionStatus.ENDED);
		});
		sessionRepository.saveAll(sessionsList);
	}

}
