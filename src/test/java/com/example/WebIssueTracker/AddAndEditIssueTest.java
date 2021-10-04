package com.example.WebIssueTracker;

import com.example.WebIssueTracker.models.IssueModel;
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
@Sql(value = {"/create-issues-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddAndEditIssueTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private IssueRepository issueRepository;

	@WithUserDetails("artupka22")
	@Test
	void addIssueErrorsTest() throws Exception {
		this.mockMvc.perform(post("/add").param("name","  ")
						.param("description","  "))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Description of issue must have length more than 20")))
				.andExpect(content().string(containsString("Description of issue should not be empty")))
				.andExpect(content().string(containsString("Title of issue should not be empty")))
				.andExpect(content().string(containsString("Title of issue must have length between 5 and 25")));
		this.mockMvc.perform(post("/add").param("name","new issue")
				//dangerous html will remove
						.param("description","<script> alert('hello') </script>"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("The message must not contain potentially dangerous html")));
	}

	@WithUserDetails("artupka22")
	@Test
	void addAndEditIssueTest() throws Exception {
		this.mockMvc.perform(post("/add").param("name","new issue")
						.param("description","<b>new issue</b><br><i>description</i>"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
		this.mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(xpath("/html/body/div/main/div/div").nodeCount(7));

		IssueModel issueModel = issueRepository.findIssueModelByName("new issue").get();

		this.mockMvc.perform(post("/edit/" + issueModel.getId()).param("name","new new issue")
						.param("description","new description of <b>issue</b>"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
		this.mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(xpath("/html/body/div/main/div[2]/div[1]").string(containsString("Last update: ")));
	}

	@WithUserDetails("artupka")
	@Test
	void onlyAdminCanChangeStatusOfIssue() throws Exception {
		this.mockMvc.perform(get("/detail/6"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Current status: Resolved")));

		this.mockMvc.perform(post("/detail/6/change-status").param("status","Created"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/detail/6"));

		this.mockMvc.perform(get("/detail/6"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Current status: Created")));
	}

	@WithUserDetails("artupka22")
	@Test
	void simpleUserCantChangeStatusOfIssue() throws Exception {
		this.mockMvc.perform(post("/detail/6/change-status").param("status","Closed"))
				.andDo(print())
				.andExpect(status().isForbidden());
	}



}
