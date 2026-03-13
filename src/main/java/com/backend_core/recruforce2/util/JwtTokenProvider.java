package com.backend_core.recruforce2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for validate and generate JWT token
 * Handle both access and refresh token
 */
@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  /**
   * Generate a JWT access token for a user
   * @param userDetails the authenticate user
   * @return the JWT string
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", userDetails.getAuthorities().stream()
      .findFirst()
      .map(auth -> auth.getAuthority().replace("ROLE_", ""))
      .orElse("USER"));
    return createToken(claims, userDetails.getUsername(), expiration);
  }

  /**
   * Generate a refresh token for a user
   * @param userDetails the authenticate user
   * @return the JWT string
   */
  public String generateRefreshToken(UserDetails userDetails){
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "refresh");
    return createToken(claims, userDetails.getUsername(), refreshExpiration);
  }

  /**
   * Create a token with a custom key and expiration
   * @param claims custom claim to include
   * @param subject username/email
   * @param expiration token duration in milliseconds
   * @return generated JWT token
   */
  private String createToken(Map<String, Object> claims, String subject, Long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
      .claims(claims)
      .subject(subject)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(getSigningKey())
      .compact();
  }

  /**
   * Extrait username (subject) from the token
   * @param token the JWT token
   * @return username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token the JWT token
   * @return the expiration date
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts specific claim from a jwt token
   * @param claimResolver function to extract the claim
   * @param T the claim type
   * @return extracted claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extract All claim from the JWT
   * @param token JWT token
   * @return all claims
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  /**
   * Checks if the token is valid
   * @param token JWT token
   * @return true if expired, false otherwise
   */
  public Boolean isTokenExpired(String token){
    return extractExpiration(token).before(new Date());
  }

  /**
   * Validate a jwt token against user details
   * @param token JWT token
   * @param userDetails user details to validate again
   * @return true if valid, false otherwise
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Returns the secret key used for JWT signing.
   *
   * @return the signing key
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
