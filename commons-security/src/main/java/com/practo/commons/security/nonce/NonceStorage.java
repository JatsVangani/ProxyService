package com.practo.commons.security.nonce;

import java.time.Duration;

/**
 * An interface that allows the consuming service to implement their own way to store and block a
 * request nonce. The most common scenario will be to use a central cache server.
 * </p>
 *
 * @author mayankdharwa
 *
 */
public interface NonceStorage {

  /**
   * Store the nonce in the Storage.
   *
   * @param nonce
   */
  public void store(String nonce);

  /**
   * Returns true if the nonce exists in the Store. Nonce existing in the store means that the
   * request is a replay-request and should not be authenticated.
   *
   * @param nonce
   * @return
   */
  public boolean exists(String nonce);

  /**
   * Returns the Duration for which the nonce will be kept in the storage. For a cache backed
   * nonce-storage, this will be the TTL of the cache key.
   *
   * @return Duration
   */
  public Duration getStoreTtl();
}
