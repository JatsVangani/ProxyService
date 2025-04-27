package com.practo.commons.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.practo.commons.security.Details;
import com.practo.commons.security.ServiceCredentialUserDetailsService;
import com.practo.commons.security.SignatureGenerator;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.security.config.SecurityAutoConfiguration;
import com.practo.commons.security.nonce.NonceManager;
import com.practo.commons.security.util.SecurityConstants;
import com.practo.commons.security.util.SecurityConstants.AuthVersion;
import com.practo.commons.security.util.SecurityConstants.ErrorMessage;

/**
 * Authenticates the request via an HMAC Signature. This class is part of
 * {@link SecurityAutoConfiguration} and therefore a bean of this class is available for injection.
 * </p>
 * You can override this class to provide an alternate implementation for
 * {@link SignatureAuthenticationProvider#supports(Class)} or
 * {@link SignatureAuthenticationProvider#validateSignature(Details, ServiceCredential)} method. All
 * you need to do is create a @Bean of your implementation.
 * </p>
 * A user details service is required for creating an authentication token with a proper Principal
 * and GrantedAuthorities. This class uses {@link ServiceCredentialUserDetailsService} as the
 * default {@link UserDetailsService}. You can change that behaviour by creating your own
 * UserDetailsService and adding the following Qualifier to that bean:
 * <code>@Qualifier(SecurityConstants.QUALIFIER_SAP_UDS)</code>.
 * </p>
 *
 * @see Qualifier {@link Qualifier}
 * @see SecurityConstants {@link SecurityConstants#QUALIFIER_SAP_UDS}.
 */
public class SignatureAuthenticationProvider implements AuthenticationProvider {

  private NonceManager nonceManager;

  private UserDetailsService userDetailsService;

  @Autowired
  public SignatureAuthenticationProvider(
      @Qualifier(SecurityConstants.QUALIFIER_SAP_UDS) UserDetailsService userDetailsService,
      NonceManager nonceManager) {
    this.userDetailsService = userDetailsService;
    this.nonceManager = nonceManager;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return SignatureAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public final Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
    Details details = (Details) authentication.getCredentials();

    // The service which has made the request is being treated as the "user" here.
    UserDetails user = userDetailsService.loadUserByUsername(details.getService());

    // Validate the nonce only after user is loaded correctly.
    nonceManager.validate(authentication);

    validateSignature(details, user);

    SignatureAuthenticationToken token = new SignatureAuthenticationToken(user, details);
    token.setAuthenticated(true);

    return token;
  }

  /**
   * Validates the request signature. The default implementation complies with the
   * {@link AuthVersion#V4} style of signature. If a different validation is expected (to support
   * {@link AuthVersion#V2} etc), then the developer is expected to extend this class and override
   * this method. Validation failure should result in a {@link BadCredentialsException}.
   *
   * @param requestDetails
   * @param userDetails
   *          - {@link UserDetails} as provided by the {@link ServiceCredentialUserDetailsService}.
   * @throws BadCredentialsException
   */
  protected void validateSignature(Details requestDetails, UserDetails userDetails)
      throws BadCredentialsException {
    String serviceSecret = userDetails.getPassword();
    SignatureGenerator generator = SignatureGenerator.fromDetails(requestDetails, serviceSecret);
    if (!generator.generate().equals(requestDetails.getSignature())) {
      throw new BadCredentialsException(ErrorMessage.AUTH_SIGNATURE_MISMATCH);
    }
  }
}
