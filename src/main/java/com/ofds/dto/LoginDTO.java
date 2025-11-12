package com.ofds.dto;

import lombok.*;

/**
 * DTO used specifically to carry a user's email and password during the authentication (login) request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

	private String email;
	private String password;
}