package com.practo.commons.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableConfigurationProperties
@EnableWebSecurity
@PropertySource("classpath:security.properties")
public class SecurityApplication {

}
