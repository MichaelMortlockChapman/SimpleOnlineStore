package com.example.simpleonlinestore.security.filters.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  @Value("${backend.secret}")
  private String secret;

  public String generateToken(String login) {
    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
              .setClaims(claims)
              .setSubject(login)
              .setIssuedAt(new Date(System.currentTimeMillis()))
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3))
              .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
  }

  // Creates a signing key from the base64 encoded secret.
  //returns a Key object for signing the JWT.
  private Key getSignKey() {
    byte[] bytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(bytes);
  }

  public String extractUserName(String token) {
      return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
      final Claims claims = extractAllClaims(token);
      return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
      return Jwts.parserBuilder()
              .setSigningKey(getSignKey())
              .build().parseClaimsJws(token).getBody();
  }

  public Boolean isTokenExpired(String token) {
      return extractExpiration(token).before(new Date());
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
              .setSigningKey(getSignKey())
              .build().parseClaimsJws(token).getBody();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
