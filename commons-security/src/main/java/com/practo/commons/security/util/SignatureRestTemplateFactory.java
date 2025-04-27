package com.practo.commons.security.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.practo.commons.security.config.SecureProperties;

/**
 * This factory class creates new {@link RestTemplate} objects which are capable of calling external
 * Practo services which support Auth V4 HMAC Authentication. The proper authentication headers are
 * automatically added to the request (headers are generated using a
 * {@link SignatureHeadersGenerator} object).
 * </p>
 * You can use this class and create many RestTemplate beans - one for each external service that
 * you need to call. For example:
 * 
 * <pre>
 * 
 * &#64;Configuration
 * public class RestTemplateBeanConfiguration {
 * 
 *   &#64;Autowired
 *   private SignatureRestTemplateFactory restTemplateFactory;
 * 
 *   &#64;Bean
 *   public RestTemplate fabricRestTemplate() {
 *     return restTemplateFactory.create("fabric");
 *   }
 * 
 *   &#64;Bean
 *   public RestTemplate titanRestTemplate() {
 *     return restTemplateFactory.create("titan");
 *   }
 * }
 * 
 * public class MyExternalServiceCaller {
 * 
 *   &#64;Autowired
 *   &#64;Qualifier("fabricRestTemplate")
 *   private RestTemplate fabricRestTemplate;
 * 
 *   // Sample method for example purpose only.
 *   public ResponseEntity callFabric(RequestEntity request) {
 *     return fabricRestTemplate.exchange(request);
 *   }
 * }
 * </pre>
 * </p>
 * Using pre-configured rest templates removes the burden of correctly creating and adding the auth
 * headers.
 * </p>
 * Note: To use HMAC-SHA256 algorithm instead of HMAC-SHA1 for creating signatures, use the
 * {@link SignatureRestTemplateFactory#create(SignatureHeadersGenerator)} method and pass a custom
 * {@link SignatureHeadersGenerator} object which uses the SHA256 algo.
 */
@Component
public class SignatureRestTemplateFactory {

  @Autowired
  private SecureProperties secureProperties;

  /**
   * Creates a new {@link RestTemplate} capable of adding Auth V4 HMAC headers automatically for the
   * given service.
   *
   * @param serviceName
   * @return
   */
  public RestTemplate create(String serviceName) {
    return create(createSignatureHeadersGenerator(serviceName));
  }

  /**
   * Creates a new {@link RestTemplate} capable of adding Auth V4 HMAC headers automatically for the
   * given service. Uses the given root uri to customize the rest template.
   *
   * @param rootUri
   * @param serviceName
   * @return
   */
  public RestTemplate create(String rootUri, String serviceName) {
    return create(rootUri, createSignatureHeadersGenerator(serviceName));
  }

  /**
   * Creates a new {@link RestTemplate} which uses the given Auth Header generator to add HMAC
   * headers to the request.
   *
   * @param headersGenerator
   * @return
   */
  public RestTemplate create(SignatureHeadersGenerator headersGenerator) {
    return new RestTemplateBuilder()
        .interceptors(new SignatureClientHttpRequestInterceptor(headersGenerator))
        .build();
  }

  /**
   * Creates a new {@link RestTemplate} capable of adding Auth V4 HMAC headers automatically for the
   * given service. Uses the given root uri to customize the rest template.
   *
   * @param rootUri
   * @param headersGenerator
   * @return
   */
  public RestTemplate create(String rootUri, SignatureHeadersGenerator headersGenerator) {
    return new RestTemplateBuilder().rootUri(rootUri)
        .interceptors(new SignatureClientHttpRequestInterceptor(headersGenerator))
        .build();
  }

  /**
   * Returns a new {@link SignatureHeadersGenerator}. This method is useful when a customized
   * {@link RestTemplate} is created.
   *
   * @param serviceName
   * @return
   */
  public SignatureHeadersGenerator createSignatureHeadersGenerator(String serviceName) {
    return new SignatureHeadersGenerator(serviceName, secureProperties);
  }
}
