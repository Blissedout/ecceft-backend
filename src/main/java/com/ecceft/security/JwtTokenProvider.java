package com.ecceft.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
      
    public String generateToken(String userName) {
       Date now = new Date();
       
       Date expiryDate = new Date(now.getTime() + jwtExpiration);
       
       return Jwts.builder()
    		   .subject(userName)
    		   .issuedAt(now)
    		   .expiration(expiryDate)
    		   .signWith(getSigningKey())
    		   .compact();
    }
    
    public String getUserNameFromToken(String token) {	
    	return Jwts.parser()
    			.verifyWith((javax.crypto.SecretKey) getSigningKey())
    			.build()
    			.parseSignedClaims(token)
    			.getPayload()
    			.getSubject();
    			
    }
    
    public Boolean validateToken(String token) {
    	try {
    		
    		Jwts.parser()
    		.verifyWith((javax.crypto.SecretKey)getSigningKey())
    		.build()
    		.parseSignedClaims(token);
    	return true;
    	
    }catch(Exception e) {
    	return false;
    }
  }
    	
//    
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }

//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//
//
//  
//
//    public String extractAccountId(String token) {
//        return extractClaim(token, claims -> claims.get("accountId", String.class));
//    }
//
//    public String extractAccountNumber(String token) {
//        return extractClaim(token, claims -> claims.get("accountNumber", String.class));
//    }
//
//    public String extractFullName(String token) {
//        return extractClaim(token, claims -> claims.get("fullName", String.class));
//    }
}
