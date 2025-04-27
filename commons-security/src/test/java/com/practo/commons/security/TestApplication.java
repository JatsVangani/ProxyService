package com.practo.commons.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.practo.commons.security.nonce.NoOpNonceStorage;
import com.practo.commons.security.nonce.NonceStorage;

@SpringBootApplication(scanBasePackages = "com.practo.commons.security")
public class TestApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(TestApplication.class, args);
  }

  @Bean
  public NonceStorage nonceStorage() {
    return new NoOpNonceStorage();
  }
}
