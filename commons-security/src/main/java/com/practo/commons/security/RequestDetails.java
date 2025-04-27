package com.practo.commons.security;

import java.util.Map;

import org.springframework.http.HttpMethod;

import com.practo.commons.security.util.SecurityConstants.AuthVersion;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RequestDetails implements Details {
  private static final long serialVersionUID = -1557827321990572858L;

  /** The service that has requested at the URI. */
  private String service;

  /** Nonce of the request. */
  private String nonce;

  /** HMAC signature of the request. */
  private String signature;

  /** Request Method. */
  private HttpMethod method;

  /** Request Scheme. */
  private String scheme;

  /** Name of the server to which the request was sent. */
  private String serverName;

  /** Server port to which the request was sent. */
  private Integer serverPort;

  /** The Request URI. */
  private String requestURI;

  /** Identifier of the user who made the request. */
  private String userIdentifier;

  /** Algorithm to use for creating signature hash. */
  private String hmacAlgorithm;

  /**
   * Different Auth versions have different ways of calculating request signature. This attribute
   * becomes the deciding factor in what method is used.
   */
  private AuthVersion authVersion;

  /**
   * All the headers present in the request. Note that this is a singular key to value mapping and
   * will only provide one value if a header is repeated in the request.
   */
  private Map<String, String> headers;
}
