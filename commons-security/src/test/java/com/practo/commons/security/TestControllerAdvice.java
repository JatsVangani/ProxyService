package com.practo.commons.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TestControllerAdvice {

  @ExceptionHandler
  public ResponseEntity<String> handleException(Exception ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Request Failed: " + ex.getMessage());
  }
}
