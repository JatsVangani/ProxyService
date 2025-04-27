package com.practo.commons.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.practo.commons.security.authentication.SignatureAuthenticationFilter;
import com.practo.commons.security.authentication.SignatureFilterFactory;

@Configuration
public class WebSecurityTestConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private SignatureFilterFactory filterFactory;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    SignatureAuthenticationFilter filter = filterFactory.create("/**");
    http.addFilterAfter(filter, AnonymousAuthenticationFilter.class);
  }
}
