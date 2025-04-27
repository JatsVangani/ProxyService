package com.practo.commons.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practo.commons.security.ServiceCredentialUserDetailsService;
import com.practo.commons.security.authentication.DefaultAuthenticationEntryPoint;
import com.practo.commons.security.authentication.SignatureAuthenticationProvider;
import com.practo.commons.security.nonce.NonceManager;

@Configuration
public class SecurityAutoConfiguration {

  @Autowired
  private SecureProperties secureProperties;

  @Autowired
  private NonceManager nonceManager;

  @Bean
  @ConditionalOnMissingBean
  public ServiceCredentialUserDetailsService serviceCredentialUserDetailsService() {
    return new ServiceCredentialUserDetailsService(secureProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public SignatureAuthenticationProvider
      signatureAuthenticationProvider(ServiceCredentialUserDetailsService uds) {
    return new SignatureAuthenticationProvider(uds, nonceManager);
  }

  @Bean
  @ConditionalOnMissingBean
  public DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint() {
    return new DefaultAuthenticationEntryPoint();
  }
}
