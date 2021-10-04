package com.example.WebIssueTracker;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-issues-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-issues-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MainControllerTest {

	@Autowired
	private MockMvc mockMvc;


	@WithUserDetails("artupka")
	@Test
	void mainPageTestWithAuthenticationLikeAdmin() throws Exception {
		this.mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(authenticated())
				.andExpect(content().string(containsString("Write issue")))
				.andExpect(content().string(containsString("Logout")))
				.andExpect(content().string(containsString("Home")))
				.andExpect(xpath("/html/body/div/main/div/div/div/div").string(containsString("Delete")))
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(6));
	}

	@WithUserDetails("artupka")
	@Test
	void onlyAdminCanDeleteIssue() throws Exception {
		this.mockMvc.perform(get("/delete/1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(get("/delete/2"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(get("/"))
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(4));
	}

	@WithUserDetails("artupka22")
	@Test
	void mainPageTestWithAuthenticationLikeUser1() throws Exception {
		this.mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(authenticated())
				.andExpect(content().string(containsString("Write issue")))
				.andExpect(content().string(containsString("Logout")))
				.andExpect(content().string(containsString("Home")))
				.andExpect(xpath("/html/body/div/main/div[2]/div[6]").string(containsString("Edit")))
				.andExpect(xpath("/html/body/div/main/div[2]/div[6]").string(containsString("Created")))
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(6));
	}

	@WithUserDetails("artupka33")
	@Test
	void mainPageTestWithAuthenticationLikeUser2() throws Exception {
		this.mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(authenticated())
				.andExpect(content().string(containsString("Write issue")))
				.andExpect(content().string(containsString("Logout")))
				.andExpect(content().string(containsString("Home")))
				.andExpect(xpath("/html/body/div/main/div[2]/div[3]").string(containsString("Edit")))
				.andExpect(xpath("/html/body/div/main/div[2]/div[3]").string(containsString("Created")))
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(6));
	}

	@WithUserDetails("artupka22")
	@Test
	void ifTryToDeleteOrEditNotMyOwnIssueThenBeRedirect() throws Exception {
		this.mockMvc.perform(get("/delete/1"))
				.andDo(print())
				//only admin can delete issue
				.andExpect(status().isForbidden());

		this.mockMvc.perform(get("/edit/6"))
				.andDo(print())
				//you can only edit your own messages with status "created"
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(get("/edit/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Edit your issue")));

		this.mockMvc.perform(get("/edit/2"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		this.mockMvc.perform(get("/edit/3"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void mainPageTestWithoutAuthentication() throws Exception {
		this.mockMvc.perform(get("/"))
				.andDo(print())
				//unauthorized user sees other items of UI
				.andExpect(content().string(containsString("Login")))
				.andExpect(content().string(containsString("Register")))
				.andExpect(content().string(containsString("Home")))
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(6));
	}

}
