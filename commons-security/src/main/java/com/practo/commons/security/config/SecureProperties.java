package com.practo.commons.security.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import com.practo.commons.security.util.SecurityConstants.AuthHeader;
import com.practo.commons.security.util.SecurityConstants.NonceCompliance;
import com.practo.commons.security.util.SignatureHeadersGenerator;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "practo.security")
public class SecureProperties implements InitializingBean {

  /** HMAC service to key credential Map. */
  private Map<String, ServiceCredential> keys = new HashMap<>();

  /**
   * Time to Live duration for the request nonce. This value should be strictly less than TTL of the
   * Nonce Store (Cache). Nonce should expire before the Nonce Store entry expires.
   */
  @DurationUnit(ChronoUnit.SECONDS)
  private Duration nonceTtl = Duration.ofSeconds(10);

  /**
   * Your service's name as understood by the external service you are trying to call. This property
   * is used by {@link SignatureHeadersGenerator} to send as the {@link AuthHeader#SERVICE} header.
   * </p>
   * If the {@link ServiceCredential#clientName} property is set, it will override this value. This
   * is done to support the case where different services identify your service by different names
   * (which you ideally shouldn't do, to keep things simple. But it is helpful for secret rotation
   * etc.).
   */
  private String defaultClientName;

  @Override
  public void afterPropertiesSet() throws Exception {
    for (String service : keys.keySet()) {
      ServiceCredential credential = getServiceCredential(service);
      if (credential == null) {
        throw new RuntimeException(String.format("Missing Credentials for Service %s", service));
      }

      if (StringUtils.isBlank(credential.getSecret())) {
        throw new RuntimeException(String.format("Service %s does not have a secret", service));
      }
    }
  }

  /**
   * Returns ServiceCredential for the given service. Null if service not found.
   *
   * @param service
   * @return {@link ServiceCredential}
   */
  public ServiceCredential getServiceCredential(String service) {
    return keys.get(service);
  }

  /**
   * Returns the "client name" for the given service.
   * </p>
   * It is the name with which the given service (in the argument) identifies your service. To be
   * used when making API calls to other services.
   * </p>
   * The method checks for service specific clientName and if not found, returns the
   * defaultClientName.
   * </p>
   *
   * @param service
   * @return String
   */
  public String getClientName(String service) {
    String clientName = getServiceCredential(service).getClientName();
    if (clientName == null) {
      clientName = defaultClientName;
    }

    return clientName;
  }

  @Data
  public static class ServiceCredential {

    /**
     * Your service's name as understood by the external service you are trying to call. This
     * property is used by {@link SignatureHeadersGenerator} to send as the
     * {@link AuthHeader#SERVICE} header.
     * </p>
     * This property is for service level customisation and you most likely will not be required to
     * set it. Instead, you should prefer to use {@link SecureProperties#defaultClientName} to
     * ensure consistency across all the external services you call.
     */
    private String clientName;

    /** Service's HMAC signing secret. */
    private String secret;

    /**
     * Whether nonce validation is required or optional.
     *
     * @deprecated This release changes the default value of nonce compliance from OPTIONAL to
     *             REQUIRED. This property is also being deprecated because Nonce should be
     *             mandatory by default. This property existed to help apps transition from an
     *             "optional" nonce approach to making it mandatory with every request. This
     *             property will be removed in the next major version release.
     */
    @Deprecated
    private NonceCompliance nonceCompliance = NonceCompliance.REQUIRED;
  }
}
