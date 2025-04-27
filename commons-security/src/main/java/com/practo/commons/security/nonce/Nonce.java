package com.practo.commons.security.nonce;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.practo.commons.security.exception.InvalidNonceException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * This class simplifies working with request nonces used to authenticate a request. Nonce has two
 * components - a creation timestamp and a salt. The format that this class understands is -
 * <code>{timestamp}|{salt}</code>. The timestamp is the unix epoch timestamp (seconds) and salt is
 * a random string.
 * </p>
 * Example: <code>1563274782|dd550e6e-a7b8-11e9-a5bc-a5e48a057a56</code>.
 *
 * @author mayankdharwa
 *
 */
@Getter
@EqualsAndHashCode
public class Nonce {

  private Instant creation;

  private String salt;

  private Duration ttl;

  /**
   * Construct a new Nonce object with a Creation Instant and Salt (Random String).
   *
   * @param creation
   * @param salt
   * @param ttl
   *          - Time to Live
   */
  private Nonce(Instant creation, String salt, Duration ttl) {
    this.creation = creation;
    this.salt = salt;
    this.ttl = ttl;
    afterPropertiesSet();
  }

  /**
   * Construct a new Nonce object with a Creation Timestamp String and Salt (Random String).
   *
   * @param timestamp
   * @param salt
   * @param ttl
   *          - Time to Live
   */
  private Nonce(String timestampStr, String salt, Duration ttl) {
    this.salt = salt;
    this.ttl = ttl;

    try {
      this.creation = Instant.ofEpochSecond(Long.valueOf(timestampStr));
    } catch (Exception e) {
      throw new InvalidNonceException("Could not parse nonce creation timestamp to Instant", e);
    }

    afterPropertiesSet();
  }

  /**
   * Creates a new Nonce Object without TTL information.
   *
   * @return Nonce
   */
  public static Nonce create() {
    return new Nonce(Instant.now(), UUID.randomUUID().toString(), Duration.ofSeconds(2));
  }

  /**
   * Creates a new nonce string. This method is a short-cut for
   * <code>Nonce.create().toString()</code>.
   *
   * @return String
   */
  public static String createString() {
    return create().toString();
  }

  /**
   * Parse a Nonce String to create a new Nonce Object.
   *
   * @param nonceString
   * @param ttl
   *          - Time to Live in seconds.
   * @return Nonce
   */
  public static Nonce from(String nonceString, Long ttl) {
    return from(nonceString, Duration.ofSeconds(ttl));
  }

  /**
   * Parse a Nonce String to create a new Nonce Object.
   *
   * @param nonceString
   * @param ttl
   * @return Nonce
   */
  public static Nonce from(String nonceString, Duration ttl) {
    if (nonceString == null || nonceString.isEmpty()) {
      throw new InvalidNonceException("The nonce string cannot be null or empty");
    }

    String[] nonceparts = nonceString.split("\\|");
    if (nonceparts.length != 2) {
      throw new InvalidNonceException(
          "The nonce string is of an invalid format. Use the format '{unix_timestamp}|{salt}'");
    }

    String timestampStr = nonceparts[0];
    String salt = nonceparts[1];

    return new Nonce(timestampStr, salt, ttl);
  }

  /**
   * Based on the given Time-To-Live (ttl) and current time, this method returns true if nonce is
   * expired.
   *
   * @return boolean
   */
  public boolean isExpired() {
    // Time right now. Trusted Value.
    Instant now = Instant.now();

    // Time when nonce will be expired. Derived from creation, hence untrusted value.
    Instant deadline = creation.plus(ttl);

    // From the current time, what is the oldest possible acceptable creation time. Nothing existed
    // before the BigBang, and for nonce, this "BigBang" event was a "TTL" seconds ago. Trusted
    // Value.
    Instant bigbang = now.minus(ttl);

    // From the current time, what is the latest possible acceptable deadline. Nothing will exist
    // after the heat death, and for nonce, this "HeatDeath" event is "TTL" seconds later. Trusted
    // Value.
    Instant heatdeath = now.plus(ttl);

    if (creation.isBefore(bigbang)) {
      return true;
    }

    if (creation.isAfter(heatdeath)) {
      return true;
    }

    if (now.isAfter(deadline)) {
      return true;
    }

    return false;
  }

  /**
   * Converts this Nonce object to the string representation as expected by the
   * {@link NonceManager}.
   *
   * @see See {@link Nonce#create()} - If you want to generate a new nonce string to send along with
   *      X-AUTH-NONCE header.
   */
  public String toString() {
    return String.valueOf(creation.getEpochSecond()) + "|" + salt;
  }

  /**
   * Validate object properties after creation.
   */
  private void afterPropertiesSet() {
    Objects.requireNonNull(creation, "Nonce Creation instant cannot be null");
    Objects.requireNonNull(salt, "Nonce Salt cannot be null");
    Objects.requireNonNull(ttl, "Nonce Time to Live cannot be null");

    int saltLen = salt.length();
    if (saltLen < 10 || saltLen > 40) {
      throw new InvalidNonceException(
          "Length of the nonce salt should be between 10 to 40 characters");
    }

    if (ttl.isNegative() || ttl.isZero() || ttl.compareTo(Duration.ofHours(1L)) > 0) {
      throw new InvalidNonceException(
          "Nonce TTL should be a positive non-zero value with less than 60 minutes duration");
    }
  }

}
