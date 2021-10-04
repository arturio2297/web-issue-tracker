package com.example.WebIssueTracker;

import com.example.WebIssueTracker.repo.IssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-manyissues-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-issues-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaginationAndSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void paginationTest() throws Exception {
        this.mockMvc.perform(get("/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12))
                //order by issue id descending (by created date)
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 24"))
                .andExpect(xpath("/html/body/div/main/div[2]/div[12]/div/div/p[1]").string("some name 13"));

        this.mockMvc.perform(get("/page/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 12"))
                .andExpect(xpath("/html/body/div/main/div[2]/div[12]/div/div/p[1]").string("some name 1"));
    }

    @Test
    void searchTestByAllFilter() throws Exception {
        this.mockMvc.perform(get("/search/all/some name/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 24"))
                .andExpect(xpath("/html/body/div/main/div[2]/div[12]/div/div/p[1]").string("some name 13"));

        this.mockMvc.perform(get("/search/all/some name/page/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 12"))
                .andExpect(xpath("/html/body/div/main/div[2]/div[12]/div/div/p[1]").string("some name 1"));

        this.mockMvc.perform(get("/search/all/some name 11/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(1))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 11"));

        this.mockMvc.perform(get("/search/all/some description of issue 11/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(1))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 11"));

        this.mockMvc.perform(get("/search/all/user2/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12));
    }

    @Test
    void searchTestByAuthorFilter() throws Exception {
        this.mockMvc.perform(get("/search/author/user2/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(12));
    }

    @Test
    void searchTestByTitleFilter() throws Exception {
        this.mockMvc.perform(get("/search/name/some name 24/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(1))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 24"));
    }

    @Test
    void searchTestByDescriptionFilter() throws Exception {
        this.mockMvc.perform(get("/search/description/of issue 24/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/main/div/div").nodeCount(1))
                .andExpect(xpath("/html/body/div/main/div[2]/div[1]/div/div/p[1]").string("some name 24"));
    }




}
