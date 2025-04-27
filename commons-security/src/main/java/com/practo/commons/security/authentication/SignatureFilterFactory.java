package com.practo.commons.security.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * A factory class to help quickly configure a HMAC Signature Authentication Filter which uses
 * {@link SignatureAuthenticationProvider} for HMAC authentication. The filter also configures an
 * {@link AuthenticationEntryPoint}. Check {@link DefaultAuthenticationEntryPoint} for more info.
 */
@Component
public class SignatureFilterFactory {

  @Autowired
  private SignatureAuthenticationProvider signatureAuthProvider;

  @Autowired
  private AuthenticationEntryPoint authenticationEntryPoint;

  /**
   * Creates a new {@link SignatureAuthenticationFilter} with
   * {@link SignatureAuthenticationProvider} and any additional providers passed as the arguments.
   * If a {@link SignatureAuthenticationProvider} or its subclass is present in the additional
   * providers, then the order of providers is maintanined as it is. If
   * {@link SignatureAuthenticationProvider} (or a subclass) is not passed, then it is added at the
   * zeroth index.
   *
   * @param urlPattern
   * @param additionalProviders
   * @return
   */
  public SignatureAuthenticationFilter create(String urlPattern,
      AuthenticationProvider... additionalProviders) {
    return create(new AntPathRequestMatcher(urlPattern), additionalProviders);
  }

  /**
   * Creates a new {@link SignatureAuthenticationFilter} which supports HMAC based request
   * authentication via {@link SignatureAuthenticationProvider} for the given Request Matcher. If a
   * {@link SignatureAuthenticationProvider} or its subclass is present in the additional providers,
   * then the order of providers is maintanined as it is. If {@link SignatureAuthenticationProvider}
   * (or a subclass) is not passed, then it is added at the zeroth index.
   *
   * @param requestMatcher
   * @return
   */
  public SignatureAuthenticationFilter create(RequestMatcher requestMatcher,
      AuthenticationProvider... additionalProviders) {
    List<AuthenticationProvider> providers = getProviders(additionalProviders);

    SignatureAuthenticationFilter filter = new SignatureAuthenticationFilter(requestMatcher);
    filter.setAuthenticationManager(new ProviderManager(providers));
    filter.setAuthenticationFailureHandler(
        new AuthenticationEntryPointFailureHandler(authenticationEntryPoint));

    return filter;
  }

  private List<AuthenticationProvider> getProviders(AuthenticationProvider[] additionalProviders) {
    boolean hasSignatureAuthProvider = false;
    List<AuthenticationProvider> providers = new ArrayList<>();
    for (AuthenticationProvider provider : additionalProviders) {
      if (SignatureAuthenticationProvider.class.isAssignableFrom(provider.getClass())) {
        hasSignatureAuthProvider = true;
      }
      providers.add(provider);
    }

    if (!hasSignatureAuthProvider) {
      providers.add(0, signatureAuthProvider);
    }

    return providers;
  }
}
