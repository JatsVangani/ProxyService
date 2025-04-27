package com.practo.commons.security.nonce;

import java.time.Duration;

/**
 * A No-Op implementation of NonceStorage that behaves such that nonce does not exists in the store.
 *
 * @author mayankdharwa
 *
 */
public class NoOpNonceStorage implements NonceStorage {

  @Override
  public void store(String nonce) {
    return;
  }

  @Override
  public boolean exists(String nonce) {
    return false;
  }

  /**
   * For No-op, the TTL is kept a really high value to ensure it passes the condition of being twice
   * the duration as Nonce TTL. (Ideally, Nonce TTL should be a few seconds or a couple of minutes
   * at max.)
   *
   * @see com.practo.commons.security.nonce.NonceStorage#getStoreTtl()
   * @see See {@link NonceManager#afterPropertiesSet()}
   */
  @Override
  public Duration getStoreTtl() {
    return Duration.ofDays(1000);
  }

}
