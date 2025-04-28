package com.proxy.controller;

import com.proxy.config.ServiceConfig;
import com.proxy.service.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private ExternalService externalService;

    @Autowired
    private ServiceConfig serviceConfig;

    @RequestMapping(value = "/titan/v1/{endpoint}", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyTitanRequest(
            @PathVariable String endpoint,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestBody(required = false) Object body,
            HttpMethod method) {
        String fullEndpoint = serviceConfig.getHosts().get("titan") + endpoint;
        if (queryParams != null && !queryParams.isEmpty()) {
            fullEndpoint += "?" + buildQueryString(queryParams);
        }
        return externalService.callTitanService(fullEndpoint, method, body);
    }

    @RequestMapping(value = "/book/v1/{endpoint}", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyBookRequest(
            @PathVariable String endpoint,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestBody(required = false) Object body,
            HttpMethod method) {
        String fullEndpoint = serviceConfig.getHosts().get("book") + endpoint;
        if (queryParams != null && !queryParams.isEmpty()) {
            fullEndpoint += "?" + buildQueryString(queryParams);
        }
        return externalService.callBookService(fullEndpoint, method, body);
    }

    private String buildQueryString(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((param1, param2) -> param1 + "&" + param2)
                .orElse("");
    }
}