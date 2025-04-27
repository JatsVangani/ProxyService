package com.practo.commons.security;

import org.springframework.http.HttpMethod;

import com.practo.commons.security.util.SecurityConstants.Algorithms;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * A class to help generate HMAC Signature for a request.
 *
 * @author mayankdharwa
 *
 */
@Getter
@Builder(toBuilder = true)
public class SignatureGenerator {

  @Builder.Default
  private String algorithm = Algorithms.HMAC_SHA1;

  @NonNull
  private HttpMethod method;

  @NonNull
  private String urlPath;

  private String nonce;

  @NonNull
  private String secret;

  /**
   * Build {@link SignatureGenerator} object from a {@link Details} object.
   *
   * @param details
   *          - Request Details
   * @param secret
   *          - Service Secret
   * @return SignatureGenerator
   */
  public static SignatureGenerator fromDetails(Details details, String secret) {
    return SignatureGenerator.builder()
        .algorithm(details.getHmacAlgorithm())
        .method(details.getMethod())
        .nonce(details.getNonce())
        .secret(secret)
        .urlPath(details.getRequestURI())
        .build();
  }

  /**
   * Alias for {@link SignatureGenerator#generate()}.
   *
   * @return String
   */
  public String getSignature() {
    return generate();
  }

  /**
   * Generates an HMAC Signature.
   *
   * @return String
   */
  public String generate() {
    StringBuilder sb = new StringBuilder();
    if (nonce != null && !nonce.isEmpty()) {
      sb.append("nonce=").append(nonce).append("&");
    }
    sb.append("uri=").append(urlPath).append("&method=").append(method);

    String signingData = sb.toString();
    String signature = Hmac.generate(signingData, secret, algorithm);

    return signature;
  }
}
