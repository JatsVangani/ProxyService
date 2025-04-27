package com.practo.commons.security.authentication;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.practo.commons.security.Details;
import com.practo.commons.security.RequestDetails;

/**
 * Concrete implementation of AbstractAuthenticationProcessingFilter. This filter creates the
 * {@link RequestDetails} and {@link DefaultAuthentication} objects to be authenticated by the
 * {@link AuthenticationManager}.
 *
 * @deprecated Just like {@link DefaultAuthentication}, this class also does not make sense for the
 *             "generic"/"default" purpose. So now it is being deprecated in favour of
 *             {@link SignatureAuthenticationFilter}. Read the deprecation notice of
 *             {@link DefaultAuthentication} for more details.
 */
@Deprecated
public class DefaultAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  /**
   * Contructor.
   *
   * @param defaultFilterProcessesUrl
   *          - URL pattern for which this filter will attempt authentication.
   */
  public DefaultAuthenticationFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
    setDefaults();
  }

  /**
   * Constructor.
   *
   * @param requestMatcher
   *          - The filter will attempt authentication if this request matcher matches with the URL.
   */
  public DefaultAuthenticationFilter(RequestMatcher requestMatcher) {
    super(requestMatcher);
    setDefaults();
  }

  /**
   * Set Defaults.
   *
   * @param manager
   */
  protected void setDefaults() {
    setAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
    setAuthenticationDetailsSource(new DefaultAuthenticationDetailsSource());
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    DefaultAuthentication authentication = DefaultAuthentication.builder()
        .authenticated(false)
        .authorities(Collections.emptySet())
        .details((Details) authenticationDetailsSource.buildDetails(request))
        .build();

    return getAuthenticationManager().authenticate(authentication);
  }
}
