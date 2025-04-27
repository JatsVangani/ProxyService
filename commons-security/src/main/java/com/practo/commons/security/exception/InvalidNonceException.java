package com.practo.commons.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when the string representation of the nonce cannot be parsed into its components or
 * contains invalid values.
 *
 * @author mayankdharwa
 *
 */
public class InvalidNonceException extends AuthenticationException {
  private static final long serialVersionUID = 2161909498072045603L;

  public InvalidNonceException(String msg) {
    super(msg);
  }

  public InvalidNonceException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
