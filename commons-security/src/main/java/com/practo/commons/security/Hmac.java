package com.practo.commons.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.BadCredentialsException;

import com.practo.commons.security.util.SecurityConstants.Algorithms;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A utility class to generate Message Authentication Code using the java's {@link Mac} class.
 * 
 * @author mayankdharwa
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class Hmac {

  /**
   * Generates a Base64 Encoded signature for the data using the given secret and
   * {@link Algorithms#HMAC_SHA1} algorithm.
   *
   * @param data
   * @param secret
   * @return
   */
  public static String generate(String data, String secret) {
    return generate(data, secret, Algorithms.HMAC_SHA1);
  }

  /**
   * Generates a Base64 Encoded signature for the data using the given secret and algorithm.
   *
   * @param data
   * @param secret
   * @param algorithm
   * @return String
   */
  public static String generate(String data, String secret, String algorithm) {
    SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), algorithm);

    try {
      Mac mac = Mac.getInstance(algorithm);
      mac.init(secretKeySpec);
      byte[] signatureBytes = mac.doFinal(data.getBytes());

      return Base64.getEncoder().encodeToString(signatureBytes);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new BadCredentialsException(e.getMessage(), e);
    }
  }
}
