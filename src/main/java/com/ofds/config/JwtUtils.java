package com.ofds.config;

import io.jsonwebtoken.Jwts; // Main utility class for building and parsing JWTs
import io.jsonwebtoken.SignatureAlgorithm; // Enum for specifying the signing algorithm
import io.jsonwebtoken.security.Keys; // Utility for generating secure keys
import org.springframework.stereotype.Component; // Marks the class as a Spring-managed bean
import org.springframework.security.core.userdetails.UserDetails; // Interface for user details

import java.nio.charset.StandardCharsets;
import java.util.Date; // For setting issued and expiration times

/*
 * 
 * This JwtUtils class is a Spring component that provides utility methods for 
 * handling JSON Web Tokens (JWT) in a secure authentication system. It uses a 
 * 36-character secret key to sign and verify tokens with the HMAC SHA-256 algorithm.
 * The generateToken method creates a JWT for a given username, setting its subject,
 * issue time, and expiration time (10 hours from issuance). 
 * The extractUsername method parses a token to retrieve the embedded username, 
 * ensuring the token was signed with the correct key. Finally, the validateToken
 * method checks whether the token's username matches the expected user, 
 * confirming its authenticity and integrity. This class plays a critical role 
 * in managing stateless authentication in a Spring-based application.
 * 
 * 
 */
@Component
public class JwtUtils { 
	 String secret = "123456789012345678901234567890123456"; // 36 characters
	
	 
	 // Method to generate a JWT token for the given username. 
	public String generateToken (String username) {
		return Jwts.builder()  // Starts building the JWT token.
				   // Sets the subject of the token to the provided username.
				   .setSubject(username)  
		           
		           // Sets the token's issue time to the current date and time.
		           .setIssuedAt(new Date())  
		          
		           // 10 hours, Sets the token's expiration time to 10 hours from now.
		           .setExpiration(new Date(System.currentTimeMillis() + 36000000)) 
		          
		           // Signs the token using the secret key and HMAC SHA-256 algorithm.
		           .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
		           
		           // Finalizes and returns the compact JWT string.
		           .compact();
	}
	
	
	
	
	// Method to extract the username (subject) from a JWT token.
	 public   String extractUsername(String token) {
		 
		 // Begins building the JWT parser.
		return Jwts.parserBuilder()
				
				 // Sets the signing key used to validate the token's signature.
				.setSigningKey(secret.getBytes())
				
				// Builds the parser instance.
				.build()
				
				// Parses the token and verifies its signature.
				.parseClaimsJws(token)
				
				 // Retrieves the token's body (claims).
				.getBody()
				
				// Extracts and returns the subject (username) from the claims.
				.getSubject();
	}
	
	 // Method to validate the token by comparing its username with the expected user.
	 public boolean validateToken (String token, UserDetails userDetails) {
		 
		 
		 // Returns true if the token's username matches the user's username.
		 return extractUsername(token).equals(userDetails.getUsername());
	 }

}
