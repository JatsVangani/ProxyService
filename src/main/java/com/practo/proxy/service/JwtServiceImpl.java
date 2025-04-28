package com.practo.proxy.service;

import com.practo.proxy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

  @Autowired
  private JwtUtil jwtUtil;

  /**
   * Generates a JWT token for the given username and authorized services.
   *
   * @param username the username to include in the token
   * @param authorizedServices the list of services the user is authorized to access
   * @return the generated JWT token
   */
  @Override
  public String generateToken(String username, List<String> authorizedServices) {
    return jwtUtil.generateToken(username, authorizedServices);
  }

  /**
   * Extracts the username from a JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  @Override
  public String extractUsername(String token) {
    return jwtUtil.extractUsername(token);
  }

  /**
   * Extracts the list of authorized services from a JWT token.
   *
   * @param token the JWT token
   * @return the list of authorized services
   */
  @Override
  public List<String> extractAuthorizedServices(String token) {
    return jwtUtil.extractAuthorizedServices(token);
  }

  /**
   * Validates a JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  @Override
  public Boolean validateToken(String token) {
    return jwtUtil.validateToken(token);
  }

  /**
   * Checks if a JWT token is valid for the given user details.
   *
   * @param token the JWT token to validate
   * @param userDetails the user details to validate against
   * @return true if the token is valid, false otherwise
   */
  @Override
  public Boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && validateToken(token));
  }
} 