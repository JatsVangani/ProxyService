package com.practo.commons.security.util;

public interface SecurityConstants {

  /**
   * Signature Authentication Provider User Details Service Qualifier.
   */
  String QUALIFIER_SAP_UDS = "signatureAuthenticationProviderUserDetailsService";

  public enum AuthVersion {
    @Deprecated
    V2,

    @Deprecated
    V3,

    V4
  }

  public interface ErrorMessage {
    String NONCE_MISSING = "A nonce is required to complete the request authentication";

    String NONCE_BAD_FORMAT = "The nonce of the request is of an invalid format."
        + " Use the format '{unix_timestamp}|{salt}'";

    String NONCE_EXPIRED = "The nonce of the request is expired.";

    String NONCE_USED = "The nonce of the request has already been used.";

    String SERVICE_MISSING = "The service name is missing from the request details";

    String AUTH_SIGNATURE_MISMATCH =
        "HMAC signature does not match with the one provided in the request";

    String AUTH_INVALID_SERVICE =
        "Either the service is missing or it does not have any credentials registered.";
  }

  public interface AuthHeader {
    String SERVICE = "X-AUTH-SERVICE";

    String NONCE = "X-AUTH-NONCE";

    String SIGNATURE = "X-AUTH-SIGNATURE";

    String USER = "X-AUTH-USER";

    String ALGO = "X-AUTH-ALGO";

    String VERSION = "X-AUTH-VERSION";
  }

  public interface Algorithms {
    String HMAC_SHA1 = "HmacSHA1";

    String HMAC_SHA256 = "HmacSHA256";
  }

  /**
   * Nonce Compliance was added to help apps transition to enforce nonce usage. Nonce is crucial for
   * Auth V4 and should not be optional. The next major version release will make it mandatory to
   * always have nonce in the request.
   */
  @Deprecated
  public enum NonceCompliance {
    REQUIRED,

    OPTIONAL
  }
}
