package com.example.lab3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class Lab3ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void postContentIsEscaped() throws Exception {
        mockMvc.perform(post("/topic/1/post")
                        .param("author", "Tester")
                        .param("content", "<script>alert(1)</script>"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/topic/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")))
                .andExpect(content().string(not(containsString("<script>alert"))));
    }

    @Test
    void missingTopicReturns404() throws Exception {
        mockMvc.perform(get("/topic/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cspHeaderIsSet() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(header().string("Content-Security-Policy", "default-src 'self'"));
    }
}