package com.proxy.config;

import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.security.util.SignatureRestTemplateFactory;
import com.proxy.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/proxy/**").authenticated()
            .anyRequest().permitAll()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public SecureProperties secureProperties() {
        SecureProperties properties = new SecureProperties();
        properties.setDefaultClientName("proxy-service");
        
        Map<String, ServiceCredential> credentials = new HashMap<>();
        
        ServiceCredential titanCreds = new ServiceCredential();
        titanCreds.setSecret(serviceConfig.getKeys().get("titan"));
        credentials.put("titan", titanCreds);
        
        ServiceCredential bookCreds = new ServiceCredential();
        bookCreds.setSecret(serviceConfig.getKeys().get("book"));
        credentials.put("book", bookCreds);
        
        properties.setKeys(credentials);
        return properties;
    }

    @Bean
    public RestTemplate titanRestTemplate(SignatureRestTemplateFactory restTemplateFactory) {
        return restTemplateFactory.create(
            serviceConfig.getHosts().get("titan"),
            "titan"
        );
    }

    @Bean
    public RestTemplate bookRestTemplate(SignatureRestTemplateFactory restTemplateFactory) {
        return restTemplateFactory.create(
            serviceConfig.getHosts().get("book"),
            "book"
        );
    }
} 