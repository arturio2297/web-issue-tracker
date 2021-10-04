package com.example.WebIssueTracker;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LoginTest {

	@Autowired
	private MockMvc mockMvc;


	@Test
	void notAuthenticationRedirectTest() throws Exception {
		this.mockMvc.perform(get("/add"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));

		this.mockMvc.perform(get("/edit/1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	void loginViewContentLoads() throws Exception {
		this.mockMvc.perform(get("/login"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Log in")));
	}

	@Test
	void loginTest() throws Exception {
		this.mockMvc.perform(formLogin().user("artupka").password("123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(formLogin().user("artupka22").password("123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(formLogin().user("artupka33").password("123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void loginTestWithOnRememberMeOption() throws Exception {
		this.mockMvc.perform(post("/login").param("username","artupka")
				.param("password","123")
				.param("remember-me","on"))
				.andDo(print())
				.andExpect(status().isFound())
				.andExpect(cookie().exists("remember-me"));
	}

	@Test
	void wrongLoginData() throws Exception {
		this.mockMvc.perform(formLogin().user("artupka44").password("123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));

		this.mockMvc.perform(formLogin().user("artupka").password("1234"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));
	}
}
