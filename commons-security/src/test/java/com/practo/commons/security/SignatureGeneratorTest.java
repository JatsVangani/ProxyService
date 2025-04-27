package com.practo.commons.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import com.practo.commons.security.util.SecurityConstants.Algorithms;

public class SignatureGeneratorTest extends AbstractTest {

  private static final String NONCE = "1640187374|61c345ee90c5061c345ee90c51";

  private static final String SECRET = "samplesecret00";

  private static final String URI = "/api/v1/test/url";

  private static final String EXPECTED_SIGNATURE_WITH_NONCE = "XAENNpuzK2Nmj+D8f2aFN0stjHI=";

  private static final String EXPECTED_SIGNATURE_WITHOUT_NONCE = "L5InVC1UEVL9/qwt6rUJi6JeMOg=";

  private static final String EXPECTED_SHA256_SIGNATURE_WITH_NONCE =
      "LxtPsEFqgyUaQrTbzRPyxTdMBn5gGyyrKl259pQReJw=";

  @Test
  public void withNonceTest() {
    assertThat(SignatureGenerator.builder()
        .method(HttpMethod.GET)
        .nonce(NONCE)
        .secret(SECRET)
        .urlPath(URI)
        .build()
        .generate()).isEqualTo(EXPECTED_SIGNATURE_WITH_NONCE);
  }

  @Test
  public void sha256WithNonceTest() {
    assertThat(SignatureGenerator.builder()
        .method(HttpMethod.GET)
        .algorithm(Algorithms.HMAC_SHA256)
        .nonce(NONCE)
        .secret(SECRET)
        .urlPath(URI)
        .build()
        .generate()).isEqualTo(EXPECTED_SHA256_SIGNATURE_WITH_NONCE);
  }

  @Test
  public void withoutNonceTest() {
    assertThat(SignatureGenerator.builder()
        .method(HttpMethod.GET)
        .secret(SECRET)
        .urlPath(URI)
        .build()
        .generate()).isEqualTo(EXPECTED_SIGNATURE_WITHOUT_NONCE);
  }

  @Test
  public void withoutUriTest() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> SignatureGenerator.builder()
            .method(HttpMethod.GET)
            .nonce(NONCE)
            .secret(SECRET)
            .build());
  }

  @Test
  public void withoutSecretTest() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> SignatureGenerator.builder()
            .method(HttpMethod.GET)
            .urlPath(URI)
            .nonce(NONCE)
            .build());
  }

  @Test
  public void withoutMethodTest() {
    assertThatExceptionOfType(NullPointerException.class).isThrownBy(
        () -> SignatureGenerator.builder().urlPath(URI).nonce(NONCE).secret(SECRET).build());
  }
}
