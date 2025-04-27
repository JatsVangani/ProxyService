package com.practo.commons.security;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  public static final String TEST_API = "/api/v1/test";

  public static final String RESPONSE = UUID.randomUUID().toString();

  @GetMapping(TEST_API)
  public String test() {
    return RESPONSE;
  }
}
