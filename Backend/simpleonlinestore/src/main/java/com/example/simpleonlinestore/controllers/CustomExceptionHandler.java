package com.example.simpleonlinestore.controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    if (ex instanceof HttpMessageNotReadableException || ex instanceof IllegalArgumentException || ex instanceof NullPointerException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
      errorDetail.setProperty("error", "Bad request");
    } else if (ex instanceof NoResourceFoundException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
      errorDetail.setProperty("error", "Undefined route");
    } else {
      errorDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(500));
      errorDetail.setProperty("error", ex.getMessage());
    }
    return errorDetail;
  };
}
