package com.vk.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {
	
	private String email;
	
	private String password;
	
	private String gender;

}
