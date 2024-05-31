package com.example.simpleonlinestore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.thymeleaf.spring6.expression.Mvc;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

	@Autowired
  private MockMvc mockMvc;
                                      
  private String base64Login = "Basic dGhpbmdAdGhpbmcuY29tOnRoaW5nIG5hbWU=";

  @Test 
  void shouldReturnLoginTokenWithValidLogin() throws Exception {
    this.mockMvc
      .perform(
        post("/v1/auth/signin")
          .header("Authorization", base64Login)
      )
      .andExpect(
        status()
          .isOk()
      );
  }

  @Test 
  void shouldnotReturnLoginTokenWithInvalidLogin() throws Exception {
    this.mockMvc
      .perform(
        post("/v1/auth/signin")
          .header("Authorization", "Basic dGhpbmdAdGhpbmcuY29tOnRoaW5nIG5hbWUy")
      )
      .andExpect(
        status()
          .is(401)
      );
  }
  
  @Test
  void singinReturnsValidToken() throws Exception {
    MvcResult res = this.mockMvc
      .perform(post("/v1/auth/signin") .header("Authorization", base64Login))
      .andReturn();

    this.mockMvc
        .perform(get("/v1/hello/user").header("Authorization", "Bearer " + res.getResponse().getContentAsString()))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, user!"));
  }

  @Test
  void loginDosnotWorkForNonSigninRoutes() throws Exception {
    this.mockMvc
      .perform(get("/v1/hello/user") .header("Authorization", base64Login))
      .andExpect(status().is(401));
  }

  @Test
  void singinReturnsTokenWithInvalidAuthorities() throws Exception {
    MvcResult res = this.mockMvc
      .perform(post("/v1/auth/signin") .header("Authorization", base64Login))
      .andReturn();

    this.mockMvc
        .perform(get("/v1/hello/admin").header("Authorization", "Bearer " + res.getResponse().getContentAsString()))
        .andExpect(status().is(403));
  }

  @Test
  void get401ForUnknownRouteWithInvaildToken() throws Exception {
    this.mockMvc
        .perform(get("/fnekjfnsoinf").header("Authorization", "Bearer 1"))
        .andExpect(status().is(401));
  }

  @Test
  void get404ForUnknownRouteWithVaildToken() throws Exception {
    MvcResult res = this.mockMvc
      .perform(post("/v1/auth/signin") .header("Authorization", base64Login))
      .andReturn();

    this.mockMvc
        .perform(get("/fnekjfnsoinf").header("Authorization", "Bearer " + res.getResponse().getContentAsString()))
        .andExpect(status().is(404));
  }
}
