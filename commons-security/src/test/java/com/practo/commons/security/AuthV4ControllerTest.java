package com.practo.commons.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.nonce.Nonce;
import com.practo.commons.security.util.SecurityConstants.Algorithms;
import com.practo.commons.security.util.SecurityConstants.AuthHeader;
import com.practo.commons.security.util.SignatureHeadersGenerator;
import com.practo.commons.security.util.SignatureRestTemplateFactory;

public class AuthV4ControllerTest extends AbstractTest {

  private static final URI TEST_URI = URI.create("http://localhost:8081" + TestController.TEST_API);

  private static final String TEST_SERVER_NAME = "server";

  @Autowired
  private SecureProperties secureProperties;

  @Autowired
  private SignatureRestTemplateFactory templateFactory;

  private RestTemplate defaultRestTemplate = new RestTemplate();

  /**
   * Tests HMAC Authenticatio with no customizations.
   */
  @Test
  public void testDefaultInterceptor() {
    RestTemplate template = templateFactory.create(TEST_SERVER_NAME);
    runTestWithTemplate(template);
  }

  /**
   * Tests HMAC Authentication by using a custom headers generator and HMAC-SHA256 signing
   * algorithm.
   */
  @Test
  public void testCustomHeaderGeneratorInterceptor() {
    SignatureHeadersGenerator headersGenerator =
        new SignatureHeadersGenerator(TEST_SERVER_NAME, secureProperties, Algorithms.HMAC_SHA256);

    RestTemplate template = templateFactory.create(headersGenerator);
    runTestWithTemplate(template);
  }

  /**
   * Tests that authentication should fail if no auth headers are passed.
   */
  @Test
  public void testAuthenticationFailure() {
    RequestEntity<?> request = RequestEntity.get(TEST_URI).build();

    assertThatExceptionOfType(HttpClientErrorException.class)
        .isThrownBy(() -> defaultRestTemplate.exchange(request, String.class))
        .extracting(ex -> ex.getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  /**
   * Tests HMAC Authentication without using the headers generator and rest template interceptor.
   */
  @Test
  public void testDefaultHeadersFlow() {
    SignatureGenerator generator = SignatureGenerator.builder()
        .secret(secureProperties.getServiceCredential(TEST_SERVER_NAME).getSecret())
        .method(HttpMethod.GET)
        .nonce(Nonce.createString())
        .urlPath(TestController.TEST_API)
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.add(AuthHeader.NONCE, generator.getNonce());
    headers.add(AuthHeader.SIGNATURE, generator.generate());
    headers.add(AuthHeader.SERVICE, TEST_SERVER_NAME);

    RequestEntity<?> request = RequestEntity.get(TEST_URI).headers(headers).build();

    runTestWithTemplate(request, defaultRestTemplate);
  }

  private void runTestWithTemplate(RestTemplate template) {
    RequestEntity<?> request = RequestEntity.get(TEST_URI).build();
    runTestWithTemplate(request, template);
  }

  private void runTestWithTemplate(RequestEntity<?> request, RestTemplate template) {
    ResponseEntity<String> response = template.exchange(request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(TestController.RESPONSE);
  }
}
