package com.example.simpleonlinestore.security.filters;

import java.io.IOException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (BadJwtException e) {
      setErrorResponse(HttpStatusCode.valueOf(401), e.getMessage(), response);
    } 
    catch (BadCredentialsException e) {
      setErrorResponse(HttpStatusCode.valueOf(401), e.getMessage(), response);
    }
  }
  
  public void setErrorResponse(HttpStatusCode status, String errorMessage, HttpServletResponse response){
    ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);
    try {
      response.getWriter().write(errorDetail.toString());
      response.setStatus(status.value());
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
}
