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
class RegistrationTest {

	@Autowired
	private MockMvc mockMvc;


	@Test
	void registrationViewContentLoads() throws Exception {
		this.mockMvc.perform(get("/register"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Register")));
	}

	@Test
	void registrationViewErrors() throws Exception {
		this.mockMvc.perform(post("/register").param("firstName"," ")
						.param("lastName"," ")
						.param("nickname"," ")
						.param("password","123")
						.param("password_again","1234"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(("First name should not be empty"))))
				.andExpect(content().string(containsString(("Last name should not be empty"))))
				.andExpect(content().string(containsString("Username must have length between 5 and 20")))
				.andExpect(content().string(containsString("Username should not be empty")))
				.andExpect(content().string(containsString("Value of password fields must be the same")));

		this.mockMvc.perform(post("/register").param("firstName","user")
						.param("lastName","user")
						.param("nickname","artupka")
						.param("password","123")
						.param("password_again","123"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(("User with the same nickname already exist"))));
	}

	@Test
	void successRegistration() throws Exception {
		this.mockMvc.perform(post("/register").param("firstName","user")
						.param("lastName","user")
						.param("nickname","user1")
						.param("password","123")
						.param("password_again","123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		this.mockMvc.perform(post("/login").param("username","user1")
				.param("password","123"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}
}
