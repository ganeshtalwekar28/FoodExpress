package com.ofds.config;

import io.jsonwebtoken.Jwts; 
import io.jsonwebtoken.SignatureAlgorithm; 
import io.jsonwebtoken.security.Keys; 
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails; 

import java.nio.charset.StandardCharsets;
import java.util.Date; 

@Component
public class JwtUtils { 
	 String secret = "xZ91jFcvxc5OADGk6kQVJzUUUS3I4ax9UAv3"; // 36 characters
	
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