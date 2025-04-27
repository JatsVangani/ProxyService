package com.practo.commons.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.practo.commons.security.Details;

import lombok.Getter;

public class SignatureAuthenticationToken extends AbstractAuthenticationToken {
  private static final long serialVersionUID = -122876001916026702L;

  /**
   * Empty user only serves as a wrapper to pass authorities from one constructor to another. This
   * is done to avoid code duplication.
   */
  private static final UserDetails EMPTY_USER =
      new User("Anonymous", "fakepass", Collections.emptyList());

  @Getter
  private Details credentials;

  @Getter
  private UserDetails principal;

  /**
   * Creates a new object with a principal with zero authorities and the given {@link Details} as
   * credentials and request details.
   *
   * @param details
   */
  public SignatureAuthenticationToken(Details details) {
    this(EMPTY_USER, details);
  }

  /**
   * Constructs a new object with the given {@link UserDetails} as principal. The {@link Details}
   * object will be used as both the request credentials and the request details.
   *
   * @param principal
   * @param details
   */
  public SignatureAuthenticationToken(UserDetails principal, Details details) {
    super(principal.getAuthorities());
    this.principal = principal;
    this.credentials = details;
    setDetails(details);
  }
}
