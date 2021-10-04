package com.example.WebIssueTracker;

import com.example.WebIssueTracker.repo.IssueRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-issues-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-comments-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-issues-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-comments-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddAndDeleteComments {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private IssueRepository issueRepository;

	@WithUserDetails("artupka")
	@Test
	void onlyAdminCanDeleteComments() throws Exception {
		this.mockMvc.perform(get("/detail/1"))
				.andDo(print())
				.andExpect(status().isOk())
				//comment box and comment form on the same lvl then actual 4 nodes
				.andExpect(xpath("/html/body/div/main/div/div[2]/div").nodeCount(4));

		this.mockMvc.perform(get("/detail/1/delete/1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/detail/1"));

		this.mockMvc.perform(get("/detail/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(xpath("/html/body/div/main/div/div[2]/div").nodeCount(3));
	}

	@WithUserDetails("artupka22")
	@Test
	void simpleUserCantDeleteComment() throws Exception {
		this.mockMvc.perform(get("/detail/1/delete/1"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@WithUserDetails("artupka22")
	@Test
	void youCanCreateCommentsOnlyForIssuesWithStatusCreated() throws Exception {
		this.mockMvc.perform(post("/add-comment/1").param("context","<i>new comment...</i>"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/detail/1"));

		this.mockMvc.perform(get("/detail/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(xpath("//html/body/div/main/div/div[2]/div[1]/p").string("new comment..."));

		this.mockMvc.perform(post("/add-comment/2").param("context","<i>new comment...</i>"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/detail/2"));

		this.mockMvc.perform(get("/detail/2"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(xpath("/html/body/div/main/div/div[2]/div").nodeCount(0));
	}

	@WithUserDetails("artupka22")
	@Test
	void addCommentErrorsTest() throws Exception {
		this.mockMvc.perform(post("/add-comment/1").param("context"," "))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Comment should not be empty")));

		this.mockMvc.perform(post("/add-comment/1").param("context","<script> alert('hello') </script>"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("The message must not contain potentially dangerous html")));
	}
}
