package com.practo.proxy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService {

    @Autowired
    private RestTemplate titanRestTemplate;

    @Autowired
    private RestTemplate bookRestTemplate;

    /**
     * Calls the Titan service with the given endpoint, method, and body.
     *
     * @param endpoint the endpoint to call
     * @param method the HTTP method
     * @param body the request body
     * @return the response from the Titan service
     */
    public ResponseEntity<String> callTitanService(String endpoint, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return titanRestTemplate.exchange(endpoint, method, entity, String.class);
    }

    /**
     * Calls the Book service with the given endpoint, method, and body.
     *
     * @param endpoint the endpoint to call
     * @param method the HTTP method
     * @param body the request body
     * @return the response from the Book service
     */
    public ResponseEntity<String> callBookService(String endpoint, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return bookRestTemplate.exchange(endpoint, method, entity, String.class);
    }

    /**
     * Calls the Titan service with the given endpoint and method.
     *
     * @param endpoint the endpoint to call
     * @param method the HTTP method
     * @return the response from the Titan service
     */
    public ResponseEntity<String> callTitanService(String endpoint, HttpMethod method) {
        return callTitanService(endpoint, method, null);
    }

    /**
     * Calls the Book service with the given endpoint and method.
     *
     * @param endpoint the endpoint to call
     * @param method the HTTP method
     * @return the response from the Book service
     */
    public ResponseEntity<String> callBookService(String endpoint, HttpMethod method) {
        return callBookService(endpoint, method, null);
    }
} 