package com.practo.proxy.controller;

import com.practo.proxy.config.ServiceConfig;
import com.practo.proxy.service.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private ExternalService externalService;

    @Autowired
    private ServiceConfig serviceConfig;

    /**
     * Proxies requests to the Titan service.
     *
     * @param endpoint the endpoint to call
     * @param queryParams the query parameters
     * @param body the request body
     * @param method the HTTP method
     * @return the response from the Titan service
     */
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

    /**
     * Proxies requests to the Book service.
     *
     * @param endpoint the endpoint to call
     * @param queryParams the query parameters
     * @param body the request body
     * @param method the HTTP method
     * @return the response from the Book service
     */
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

    /**
     * Builds a query string from the given parameters.
     *
     * @param params the parameters to build the query string from
     * @return the built query string
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return queryString.toString();
    }
}