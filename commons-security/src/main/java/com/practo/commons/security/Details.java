package com.practo.commons.security;

import java.io.Serializable;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.practo.commons.security.util.SecurityConstants.AuthVersion;

/**
 * An interface for objects storing request details in the authentication object.
 *
 * @author mayankdharwa
 *
 */
public interface Details extends Serializable {

  /**
   * Returns the name of the calling service.
   *
   * @return String
   */
  String getService();

  /**
   * Returns nonce of the request.
   *
   * @return String
   */
  String getNonce();

  /**
   * Returns the signature of the request as provided by the calling service.
   *
   * @return String
   */
  String getSignature();

  /**
   * Returns the URL Scheme (http, https, etc).
   *
   * @return
   */
  String getScheme();

  /**
   * Server Name.
   *
   * @return
   */
  String getServerName();

  /**
   * Server Port.
   *
   * @return
   */
  Integer getServerPort();

  /**
   * Returns the Http Method of the request.
   *
   * @return HttpMethod
   */
  HttpMethod getMethod();

  /**
   * Returns the Url Path of the request API.
   *
   * @return String.
   */
  String getRequestURI();

  /**
   * Returns the User Identifier associated with the request.
   *
   * @return
   */
  String getUserIdentifier();

  /**
   * Returns the Hmac Algorithm used by the calling service to sign the request.
   *
   * @return String
   */
  String getHmacAlgorithm();

  /**
   * Returns the Auth Version that the calling service expects the consuming service to use while
   * authenticating.
   *
   * @return AuthVersion
   */
  AuthVersion getAuthVersion();

  /**
   * Get all headers associated with the request.
   *
   * @return Map
   */
  Map<String, String> getHeaders();

  /**
   * Get value of a header by name.
   *
   * @param name
   * @return String
   */
  default String getHeaderValue(String name) {
    return getHeaders().get(name);
  }
}
