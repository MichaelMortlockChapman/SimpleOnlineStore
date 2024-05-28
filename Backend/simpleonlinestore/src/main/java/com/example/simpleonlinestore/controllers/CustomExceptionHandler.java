package com.example.simpleonlinestore.controllers;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  /**
   * Catches and handles errors in the controller space. Any exception thrown in filters will not be caught here
   * @param ex Thrown error
   * @return ProblemDetail with status and error msg
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleSecurityExcpetion(Exception ex) {
    ProblemDetail errorDetail = null;
    if (ex instanceof AuthenticationException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
      errorDetail.setProperty("error", "Bad credentials");
    } else if (ex instanceof AccessDeniedException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
      errorDetail.setProperty("error", "Forbidden");
    } else {
      errorDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(500));
      errorDetail.setProperty("error", ex.getMessage());
    }
    return errorDetail;
  };
}
