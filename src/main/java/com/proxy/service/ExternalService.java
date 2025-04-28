package com.proxy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService {

    @Autowired
    @Qualifier("titanRestTemplate")
    private RestTemplate titanRestTemplate;

    @Autowired
    @Qualifier("bookRestTemplate")
    private RestTemplate bookRestTemplate;

    public ResponseEntity<String> callTitanService(String endpoint, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        
        return titanRestTemplate.exchange(endpoint, method, entity, String.class);
    }

    public ResponseEntity<String> callBookService(String endpoint, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        
        return bookRestTemplate.exchange(endpoint, method, entity, String.class);
    }

    // Convenience methods for GET requests
    public ResponseEntity<String> callTitanService(String endpoint) {
        return callTitanService(endpoint, HttpMethod.GET, null);
    }

    public ResponseEntity<String> callBookService(String endpoint) {
        return callBookService(endpoint, HttpMethod.GET, null);
    }
} 