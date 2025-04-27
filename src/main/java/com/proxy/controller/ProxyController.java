package com.proxy.controller;

import com.proxy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProxyController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${internal-services}")
    private List<Map<String, String>> internalServices;

    @GetMapping("/{serviceName}/**")
    public ResponseEntity<?> proxyRequest(
            @PathVariable String serviceName,
            @RequestHeader("Authorization") String token,
            @RequestParam Map<String, String> params) {

        // Validate JWT token
        if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get list of authorized services from token
        List<String> authorizedServices = jwtUtil.extractServices(token.replace("Bearer ", ""));
        
        // Check if service is authorized
        if (!authorizedServices.contains(serviceName)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Find service URL
        String serviceUrl = internalServices.stream()
                .filter(service -> service.get("name").equals(serviceName))
                .findFirst()
                .map(service -> service.get("url"))
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Forward request to internal service
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    serviceUrl + "/" + serviceName,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class,
                    params
            );
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 