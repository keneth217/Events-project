package com.example.events.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Secret key for signing JWT tokens
    private static final String SECRET = "DGEF7R3R9404I34R8R23U8FWQLDNQWDWQKNCLEWFNOI4R844RI4R87465TREBFDDJ";

    // Key expiration time (24 hours in milliseconds)
    private static final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Generate token with claims including shopId and shopCode
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, username);
    }


    public String generateSuperToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, username);
    }

    // Create a JWT token
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME)) // Expires in 24 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get signing key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract specific claims from the token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract the username (subject) from the token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }




    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate if the token is valid (correct user and not expired)
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}