package com.practo.proxy.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Service interface for JWT token operations.
 */
public interface JwtService {

  /**
   * Generates a JWT token for the given username and authorized services.
   *
   * @param username the username to include in the token
   * @param authorizedServices the list of services the user is authorized to access
   * @return the generated JWT token
   */
  String generateToken(String username, List<String> authorizedServices);

  /**
   * Extracts the username from a JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  String extractUsername(String token);

  /**
   * Extracts the list of authorized services from a JWT token.
   *
   * @param token the JWT token
   * @return the list of authorized services
   */
  List<String> extractAuthorizedServices(String token);

  /**
   * Validates a JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  Boolean validateToken(String token);

  /**
   * Checks if a JWT token is valid for the given user details.
   *
   * @param token the JWT token to validate
   * @param userDetails the user details to validate against
   * @return true if the token is valid, false otherwise
   */
  Boolean isTokenValid(String token, UserDetails userDetails);
} 