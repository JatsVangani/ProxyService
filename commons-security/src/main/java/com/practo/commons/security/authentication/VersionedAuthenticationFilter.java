package com.practo.commons.security.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.practo.commons.security.util.SecurityConstants.AuthHeader;
import com.practo.commons.security.util.SecurityConstants.AuthVersion;

import lombok.Setter;

/**
 * This class helps in migrating/updating/changing an application's API security in case of backward
 * breaking changes. This filter allows the application to have multiple authentication flows based
 * on the value of <tt>X-AUTH-VERSION</tt> header. The consumer is expected to add the Filters to
 * use for a given value of version. If no such mapping is created, the default filter is used to
 * attempt authentication.
 * </p>
 * This class should only be used if using multiple {@link AuthenticationProvider}s with
 * an {@link AuthenticationManager} is not working out.
 *
 * @author mayankdharwa
 *
 */
public class VersionedAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  @Setter
  private AbstractAuthenticationProcessingFilter defaultFilter;

  @Setter
  private AuthVersion defaultVersion = AuthVersion.V4;

  private Map<AuthVersion, AbstractAuthenticationProcessingFilter> versionFilters = new HashMap<>();

  /**
   * Constructor.
   *
   * @param defaultFilterProcessesUrl
   */
  public VersionedAuthenticationFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
    setDefaults();
  }

  /**
   * Constructor.
   *
   * @param requestMatcher
   */
  public VersionedAuthenticationFilter(RequestMatcher requestMatcher) {
    super(requestMatcher);
    setDefaults();
  }

  private void setDefaults() {
    setAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
  }

  /**
   * For a particular version, which authentication filter to use.
   *
   * @param version
   * @param additionalFilter
   * @return
   */
  public VersionedAuthenticationFilter addAdditionalFilter(AuthVersion version,
      AbstractAuthenticationProcessingFilter additionalFilter) {
    versionFilters.put(version, additionalFilter);

    return this;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    AuthVersion version = defaultVersion;
    String versionStr = request.getHeader(AuthHeader.VERSION);
    if (versionStr != null && !versionStr.isEmpty()) {
      version = AuthVersion.valueOf(versionStr);
    }

    AbstractAuthenticationProcessingFilter applicableFilter =
        versionFilters.getOrDefault(version, defaultFilter);

    return applicableFilter.attemptAuthentication(request, response);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }

}
