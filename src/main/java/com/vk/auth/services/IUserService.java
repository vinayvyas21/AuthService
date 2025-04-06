package com.vk.auth.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vk.auth.converters.UserConverter;
import com.vk.auth.dtos.LoginRequestDto;
import com.vk.auth.dtos.RequestStatus;
import com.vk.auth.dtos.UserSignUpRequestDto;
import com.vk.auth.dtos.UserSignUpResponseDto;
import com.vk.auth.exceptions.UserAlreadyExistsException;
import com.vk.auth.models.Session;
import com.vk.auth.models.SessionStatus;
import com.vk.auth.models.User;
import com.vk.auth.models.UserRole;
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

	@Autowired
	private JwtUtil jwtUtil;

	public IUserService(UserRepository userRepository, BCryptPasswordEncoder encoder,
			SessionRepository sessionRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.sessionRepository = sessionRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public UserSignUpResponseDto createUser(UserSignUpRequestDto request) throws UserAlreadyExistsException {
		if(this.userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("User already exists with the email : " + request.getEmail());
		}
		User user = UserConverter.convertUserSignUpRequestToUser(request);
		user.setPasswordSalt(hashPassword(request.getPassword()));
		User savedUser = userRepository.save(user);

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
				token = jwtUtil.generateToken(user.get().getEmail());

				Session session = new Session();
				session.setUser(user.get());
				session.setToken(token);
				session.setSessionStatus(SessionStatus.ACTIVE);
				sessionRepository.save(session);

				Optional<UserRole> userRoleOptional = roleRepository.findByName("Reader");

				if (userRoleOptional.isPresent()) {
					user.get().setRoles(Set.of(userRoleOptional.get()));
				}

				userRepository.save(user.get());

				return token;
			}
		}

		return token;
	}

	public String hashPassword(String plainPassword) {
		return encoder.encode(plainPassword);
	}

	public boolean verifyPassword(String plainPassword, String hashedPassword) {
		return encoder.matches(plainPassword, hashedPassword);
	}

}
