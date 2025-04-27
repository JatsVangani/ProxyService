package com.practo.commons.security.authentication;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.practo.commons.security.Details;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Default implementation of the {@link Authentication} interface.
 *
 * @deprecated This class is being deprecated in favour of {@link SignatureAuthenticationToken}. At
 *             this point, the only place this class was being used in the
 *             {@link SignatureAuthenticationProvider}. {@link SignatureAuthenticationToken} is more
 *             meaningful in that context. It is also a fits better into spring-security as it is an
 *             extension of {@link AbstractAuthenticationToken} as intended by the library rather
 *             than a direct implementation of the {@link Authentication} interface.
 *             </p>
 *             There shouldn't be a "general-purpose" authentication token implementation as it goes
 *             against how Spring security has been implemented. Each {@link AuthenticationProvider}
 *             should return a specific token so that if any other provider wants to act on that
 *             particular token, they should be able to. Using a generic implementation takes away
 *             that selection granularity.
 */
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
@Deprecated
public class DefaultAuthentication implements Authentication {
  private static final long serialVersionUID = -2118565246862315443L;

  /**
   * List of {@link GrantedAuthority granted authorities} of the user/service.
   */
  private Collection<? extends GrantedAuthority> authorities;

  /** User credentials. */
  private Object credentials;

  /** Details of the Request. */
  private Details details;

  /** The logged in user. */
  private UserDetails principal;

  /** Was authentication successful?. */
  @Setter
  private boolean authenticated;

  @Override
  public String getName() {
    if (principal != null) {
      return principal.getUsername();
    }

    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.unmodifiableCollection(authorities);
  }
}
