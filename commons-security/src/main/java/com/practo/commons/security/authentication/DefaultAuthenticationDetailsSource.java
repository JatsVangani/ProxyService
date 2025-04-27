package com.practo.commons.security.authentication;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.practo.commons.security.Details;
import com.practo.commons.security.RequestDetails;
import com.practo.commons.security.nonce.NonceManager;
import com.practo.commons.security.util.SecurityConstants.Algorithms;
import com.practo.commons.security.util.SecurityConstants.AuthHeader;
import com.practo.commons.security.util.SecurityConstants.AuthVersion;

/**
 * The commons-security implementation of the {@link AuthenticationDetailsSource} interface. This
 * class is used by the {@link SignatureAuthenticationFilter} to create the credentials and details
 * for the HMAC Signature authentication flow which are then used by
 * {@link SignatureAuthenticationProvider} and {@link NonceManager}.
 */
public class DefaultAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HttpServletRequest, Details> {

  @Override
  public Details buildDetails(HttpServletRequest request) {
    AuthVersion authVersion;
    try {
      authVersion = AuthVersion.valueOf(request.getHeader(AuthHeader.VERSION));
    } catch (Exception ex) {
      authVersion = getDefaultAuthVersion();
    }

    RequestDetails details = RequestDetails.builder()
        .authVersion(authVersion)
        .service(request.getHeader(AuthHeader.SERVICE))
        .signature(request.getHeader(AuthHeader.SIGNATURE))
        .nonce(request.getHeader(AuthHeader.NONCE))
        .userIdentifier(request.getHeader(AuthHeader.USER))
        .hmacAlgorithm(Optional.ofNullable(request.getHeader(AuthHeader.ALGO))
            .filter(StringUtils::isNotBlank)
            .orElse(Algorithms.HMAC_SHA1))
        .serverName(request.getServerName())
        .serverPort(request.getServerPort())
        .method(HttpMethod.resolve(request.getMethod()))
        .scheme(request.getScheme())
        .requestURI(request.getRequestURI())
        .headers(getHeadersMap(request))
        .build();

    return details;
  }

  /**
   * While creating the {@link RequestDetails} object, if the request does not specify an
   * {@link AuthVersion} via the {@link AuthHeader#VERSION} header, this default {@link AuthVersion}
   * will be used.
   *
   * @return
   */
  protected AuthVersion getDefaultAuthVersion() {
    return AuthVersion.V4;
  }

  /**
   * Converts the Header Enumeration to a String->String map with header name -> value mapping. One
   * header can have multiple values and this method will override and keep the last value.
   *
   * @param request
   * @return
   */
  protected Map<String, String> getHeadersMap(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    Map<String, String> headers = new LinkedCaseInsensitiveMap<>();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        headers.put(headerName, request.getHeader(headerName));
      }
    }

    return Collections.unmodifiableMap(headers);
  }
}
