package com.practo.commons.security.authentication;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.practo.commons.security.Details;

/**
 * Implementation of the {@link AbstractAuthenticationProcessingFilter} used to add HMAC Signature
 * authentication to the given URL paths/Request Matchers. To create a new
 * {@link SignatureAuthenticationFilter} inject and use the {@link SignatureFilterFactory}.
 */
public class SignatureAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  protected SignatureAuthenticationFilter(String processingUrl) {
    this(new AntPathRequestMatcher(processingUrl));
  }

  /**
   * Construct a new object with the given request url matcher which decides whether this filter
   * will attempt an authentication or not.
   *
   * @param requestMatcher
   */
  protected SignatureAuthenticationFilter(RequestMatcher requestMatcher) {
    super(requestMatcher);
    setAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
    setAuthenticationDetailsSource(new DefaultAuthenticationDetailsSource());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    Details details = (Details) authenticationDetailsSource.buildDetails(request);
    SignatureAuthenticationToken token = new SignatureAuthenticationToken(details);

    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }
}
