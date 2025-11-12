package com.ofds.dto;

/*
* DTO used to carry user credential and registration data (like email, password, name, phone)
* from the client to the server during login and registration processes.
*/
public class AuthRequest {
	
	private String name;
    private String password;
    private String email;
	private String phone;
	private boolean termsAccepted;

    public AuthRequest() {}
	    
    public AuthRequest(String email, String password) {
    	this.email = email;
    	this.password = password;
    }

    public AuthRequest(String email, String password, String name, String phone) {
    	this.email = email;
    	this.password = password;
    	this.name = name;
    	this.phone = phone;
	}

    // Getters
    public String getName() {
    	return name;
	}

    public String getPassword() {
    	return password;
	}

    public String getEmail() {
    	return email;
	}

    public String getPhone() {
    	return phone;
    }

    public boolean isTermsAccepted() {
    	return termsAccepted;
	}

    // Setters
    public void setName(String name) {
    	this.name = name;
	}

    public void setPassword(String password) {
    	this.password = password;
    }

    public void setEmail(String email) {
    	this.email = email;
	}

    public void setPhone(String phone) {
    	this.phone = phone;
	}

    public void setTermsAccepted(boolean termsAccepted) {
    	this.termsAccepted = termsAccepted;
	}
}