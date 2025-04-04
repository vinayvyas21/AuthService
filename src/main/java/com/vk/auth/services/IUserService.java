package com.vk.auth.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vk.auth.converters.UserConverter;
import com.vk.auth.dtos.LoginRequest;
import com.vk.auth.dtos.UserSignUpRequest;
import com.vk.auth.models.Session;
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
	public void createUser(UserSignUpRequest request) {
		User user = UserConverter.convertUserSignUpRequestToUser(request);
		user.setPasswordSalt(hashPassword(request.getPassword()));
		userRepository.save(user);
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
	public boolean login(LoginRequest request) {
		Optional<User> user = userRepository.findByEmail(request.getEmail());
		if (user.isPresent()) {
			if (verifyPassword(request.getPassword(), user.get().getPasswordSalt())) {
				String token = jwtUtil.generateToken(user.get().getEmail());

				Session session = new Session();
				session.setUser(user.get());
				session.setToken(token);
				sessionRepository.save(session);

				Optional<UserRole> userRoleOptional = roleRepository.findByName("Reader");

				if (userRoleOptional.isPresent()) {
					user.get().setRoles(List.of(userRoleOptional.get()));
				}

				userRepository.save(user.get());

				return true;
			}
		}
		return false;
	}

	public String hashPassword(String plainPassword) {
		return encoder.encode(plainPassword);
	}

	public boolean verifyPassword(String plainPassword, String hashedPassword) {
		return encoder.matches(plainPassword, hashedPassword);
	}

}
