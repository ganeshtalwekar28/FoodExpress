package com.ofds.dto;

/*
 * DTO used to send the generated JWT authentication token back to the client after a successful login.
 */
public class AuthResponse {

	String token;
	
	AuthResponse(){}
	
	public AuthResponse(String token){
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}