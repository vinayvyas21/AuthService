package com.vk.auth.utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//	SecretKey secretKey = Keys.hmacShaKeyFor("VinnySecretKey21".getBytes(StandardCharsets.UTF_8));

	public String generateToken(String username) {
		LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime after30Days = localDateTime.plusDays(30);
	    Date date = Date.from(after30Days.atZone(ZoneId.systemDefault()).toInstant());
	    
		return Jwts.builder().issuer("vinay@gmail.com").setSubject(username).setIssuedAt(new Date())
				.setExpiration(date)
				.signWith(secretKey).compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractAllClaims(token);
			Date expiration = claims.getExpiration();
			
			return !isTokenExpired(expiration);
		} catch(Exception ex) {
			return false;
		}
	}

	private boolean isTokenExpired(Date expiration) {
		return expiration.before(new Date());
	}

	private Claims extractAllClaims(String token) {
		Claims claims = Jwts.parser()
	            .setSigningKey(secretKey)
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
		
		return claims;
	}
}
