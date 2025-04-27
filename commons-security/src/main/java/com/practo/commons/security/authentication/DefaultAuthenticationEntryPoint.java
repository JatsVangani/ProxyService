package com.practo.commons.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.practo.commons.security.config.SecurityAutoConfiguration;

/**
 * In a RESTful Web Application, the Spring Security exceptions should be redirected to an
 * {@link ExceptionHandler} or a {@link ControllerAdvice}. However, that is not the default
 * behaviour of the {@link ProviderManager}. This class helps to redirect
 * {@link AuthenticationException} to proper handlers via the
 * {@link AuthenticationEntryPointFailureHandler} class when authentication fails.
 * </p>
 * This class is a part of {@link SecurityAutoConfiguration} and therefore a bean of this class is
 * available for injection. The {@link AuthenticationFilterBuilder} includes this to handle
 * authentication failures if available.
 * </p>
 * A different implementation of {@link AuthenticationEntryPoint} can be provided. Check
 * {@link SecurityAutoConfiguration} on how to disable default bean creation.
 *
 * @author mayankdharwa
 *
 */
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Autowired(required = false)
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver exceptionResolver;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    if (exceptionResolver != null) {
      exceptionResolver.resolveException(request, response, null, authException);
    }
  }
}
