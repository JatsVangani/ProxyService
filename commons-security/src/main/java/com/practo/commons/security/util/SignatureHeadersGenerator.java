package com.practo.commons.security.util;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.RequestEntity;

import com.practo.commons.security.SignatureGenerator;
import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.security.nonce.Nonce;
import com.practo.commons.security.util.SecurityConstants.Algorithms;
import com.practo.commons.security.util.SecurityConstants.AuthHeader;
import com.practo.commons.security.util.SecurityConstants.AuthVersion;

import lombok.Getter;

/**
 * A helper class for generating {@link AuthVersion#V4} compatible request headers for a given
 * external service.
 */
public class SignatureHeadersGenerator {

  private SecureProperties secureProperties;

  @Getter
  private String hmacAlgorithm;

  @Getter
  private String service;

  /**
   * Creates a new {@link SignatureHeadersGenerator} object which will generate authentication
   * headers for the given service.
   *
   * @param service
   * @param secureProperties
   */
  public SignatureHeadersGenerator(String service, SecureProperties secureProperties) {
    this(service, secureProperties, Algorithms.HMAC_SHA1);
  }

  /**
   * Creates a new {@link SignatureHeadersGenerator} object which will generate authentication
   * headers for the given service.
   *
   * @param secureProperties
   *          - The secure properties object.
   * @param hmacAlgorithm
   *          - The HMAC SHA algorithm to use while generating the signature. Default value is
   *          {@link Algorithms#HMAC_SHA1}.
   */
  public SignatureHeadersGenerator(String service, SecureProperties secureProperties,
      String hmacAlgorithm) {
    updateCredentials(service, secureProperties);
    this.service = service;
    this.secureProperties = secureProperties;
    this.hmacAlgorithm = hmacAlgorithm;
  }

  private void updateCredentials(String service, SecureProperties secureProperties) {
    ServiceCredential credentials = secureProperties.getServiceCredential(service);
    if (credentials == null) {
      throw new RuntimeException("Could not find credentials for service: " + service);
    }

  }

  /**
   * Returns an {@link HttpHeaders} object containing the necessary {@link AuthVersion#V4}
   * compatible authentication headers with values. This method does not add {@link AuthHeader#USER}
   * header which is used for user identification.
   *
   * @param method
   * @param urlPath
   *          - Path of the URL with a leading forward-slash. Ex: <code>/api/v1/doctors</code>
   * @return {@link HttpHeaders}
   */
  public HttpHeaders generate(HttpMethod method, String urlPath) {
    ServiceCredential credentials = secureProperties.getServiceCredential(service);
    SignatureGenerator sigGen = SignatureGenerator.builder()
        .algorithm(hmacAlgorithm)
        .method(method)
        .nonce(Nonce.createString())
        .secret(credentials.getSecret())
        .urlPath(urlPath)
        .build();

    String clientName = Optional.ofNullable(credentials.getClientName())
        .filter(StringUtils::isEmpty)
        .orElse(secureProperties.getDefaultClientName());

    HttpHeaders headers = new HttpHeaders();
    headers.add(AuthHeader.NONCE, sigGen.getNonce());
    headers.add(AuthHeader.SERVICE, clientName);
    headers.add(AuthHeader.SIGNATURE, sigGen.generate());
    headers.add(AuthHeader.VERSION, AuthVersion.V4.toString());
    headers.add(AuthHeader.ALGO, hmacAlgorithm);

    return headers;
  }

  /**
   * Generate Authentication headers for the request.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param <T>
   * @param request
   * @return
   */
  public <T> HttpHeaders generate(RequestEntity<T> request) {
    HttpMethod method = request.getMethod();
    if (method == null) {
      throw new IllegalArgumentException("Request Method cannot be null");
    }

    return generate(method, request.getUrl());
  }

  /**
   * Generate Authentication headers for the request.
   *
   * @param <T>
   * @param request
   * @return
   */
  public <T> HttpHeaders generate(HttpRequest request) {
    HttpMethod method = request.getMethod();
    if (method == null) {
      throw new IllegalArgumentException("Request Method cannot be null");
    }

    return generate(method, request.getURI().getPath());
  }

  /**
   * Generate authentication headers for the given {@link HttpMethod} and {@link URI}.
   * 
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param method
   * @param uri
   * @return
   */
  public HttpHeaders generate(HttpMethod method, URI uri) {
    return generate(method, uri.getPath());
  }

  // === Shortcut methods ==

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#GET} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param uri
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forGet(URI uri) {
    return generate(HttpMethod.GET, uri);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#GET} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param urlPath
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forGet(String urlPath) {
    return generate(HttpMethod.GET, urlPath);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#POST} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param uri
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPost(URI uri) {
    return generate(HttpMethod.POST, uri);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#POST} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param urlPath
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPost(String urlPath) {
    return generate(HttpMethod.POST, urlPath);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#PATCH} API
   * calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param uri
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPatch(URI uri) {
    return generate(HttpMethod.PATCH, uri);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#PATCH} API
   * calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param urlPath
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPatch(String urlPath) {
    return generate(HttpMethod.PATCH, urlPath);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#PUT} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param uri
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPut(URI uri) {
    return generate(HttpMethod.PUT, uri);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#PUT} API calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param urlPath
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forPut(String urlPath) {
    return generate(HttpMethod.PUT, urlPath);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#DELETE} API
   * calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param uri
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forDelete(URI uri) {
    return generate(HttpMethod.DELETE, uri);
  }

  /**
   * Returns an {@link HttpHeaders} object populated with the {@link AuthVersion#V4} compatible
   * authentication headers for the associated service for making {@link HttpMethod#DELETE} API
   * calls.
   *
   * @see See {@link SignatureHeadersGenerator#generate(HttpMethod, String)}
   * @param urlPath
   * @return {@link HttpHeaders}
   */
  public HttpHeaders forDelete(String urlPath) {
    return generate(HttpMethod.DELETE, urlPath);
  }
}
