package com.practo.commons.security.nonce;

import java.util.Objects;

import org.springframework.cache.Cache;

/**
 * A Nonce Storage that uses Cache as the store.
 */
public abstract class CacheBasedNonceStorage implements NonceStorage {

  private Cache cache;

  public CacheBasedNonceStorage(Cache cache) {
    Objects.requireNonNull(cache, "Cache cannot be null");
    this.cache = cache;
  }

  @Override
  public void store(String nonce) {
    cache.put(nonce, nonce);
  }

  @Override
  public boolean exists(String nonce) {
    return cache.get(nonce, String.class) != null;
  }
}
