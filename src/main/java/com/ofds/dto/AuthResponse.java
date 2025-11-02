package com.ofds.dto;

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
