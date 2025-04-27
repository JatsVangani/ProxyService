package com.practo.commons.security.nonce;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import com.practo.commons.security.Details;
import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.security.util.SecurityConstants.ErrorMessage;
import com.practo.commons.security.util.SecurityConstants.NonceCompliance;

@Component
public class NonceManager implements InitializingBean {

  @Autowired
  private NonceStorage nonceStorage;

  @Autowired
  private SecureProperties properties;

  /**
   * Initialize NonceManager. This method also adds the check where the duration for which a key can
   * live in the nonce store (Store TTL) should be at least twice as the time-to-live of the Nonce.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    long nonceTtl = properties.getNonceTtl().getSeconds();
    long storeTtl = nonceStorage.getStoreTtl().getSeconds();

    long acceptableStoreTtl = nonceTtl * 2;

    if (storeTtl < acceptableStoreTtl) {
      throw new IllegalArgumentException("Store TTL should be at least two times of the Nonce TTL."
          + " Bad configuration may allow a nonce to be validated multiple times.");
    }
  }

  /**
   * Validates and Stores nonce in the Nonce Store.
   *
   * @param authentication
   */
  public void validate(Authentication authentication) {
    Details details = (Details) authentication.getDetails();

    ServiceCredential serviceCredential = properties.getServiceCredential(details.getService());

    String nonceStr = details.getNonce();
    boolean hasNonce = (nonceStr != null && !nonceStr.isEmpty());

    if (!hasNonce && NonceCompliance.REQUIRED.equals(serviceCredential.getNonceCompliance())) {
      throw new NonceExpiredException(ErrorMessage.NONCE_MISSING);
    }

    // If nonce is not present and service has an optional compliance, validation can be skipped.
    if (!hasNonce && NonceCompliance.OPTIONAL.equals(serviceCredential.getNonceCompliance())) {
      return;
    }

    // Check if nonce is valid and not expired.
    if (Nonce.from(nonceStr, properties.getNonceTtl()).isExpired()) {
      throw new NonceExpiredException(ErrorMessage.NONCE_EXPIRED);
    }

    // Check if nonce is already used.
    if (nonceStorage.exists(nonceStr)) {
      throw new NonceExpiredException(ErrorMessage.NONCE_USED);
    }

    // Nonce Validated. Store it in nonce store.
    nonceStorage.store(nonceStr);
  }
}
