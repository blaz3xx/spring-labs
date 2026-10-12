package com.example.lab4;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postLifecycle() throws Exception {
        long topicId = createTopic();

        String response = mockMvc.perform(post("/api/topics/{topicId}/posts", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Аня\", \"content\": \"Перший допис у темі\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.topicId").value(topicId))
                .andExpect(jsonPath("$.content").value("Перший допис у темі"))
                .andReturn().getResponse().getContentAsString();
        long postId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/topics/{topicId}/posts", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(postId));

        mockMvc.perform(get("/api/topics/{topicId}/posts/{postId}", topicId, postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Аня"));

        mockMvc.perform(delete("/api/topics/{topicId}/posts/{postId}", topicId, postId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/topics/{topicId}/posts/{postId}", topicId, postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPostWithInvalidDataReturns400() throws Exception {
        long topicId = createTopic();

        mockMvc.perform(post("/api/topics/{topicId}/posts", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Аня\", \"content\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    void postsOfMissingTopicReturn404() throws Exception {
        mockMvc.perform(get("/api/topics/{topicId}/posts", 999_999))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/topics/{topicId}/posts", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Аня\", \"content\": \"Допис у неіснуючу тему\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postFromAnotherTopicIsNotFound() throws Exception {
        long firstTopicId = createTopic();
        long secondTopicId = createTopic();

        String response = mockMvc.perform(post("/api/topics/{topicId}/posts", firstTopicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Іван\", \"content\": \"Допис першої теми\"}"))
                .andReturn().getResponse().getContentAsString();
        long postId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/api/topics/{topicId}/posts/{postId}", secondTopicId, postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingTopicRemovesItsPosts() throws Exception {
        long topicId = createTopic();
        mockMvc.perform(post("/api/topics/{topicId}/posts", topicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"author\": \"Дмитро\", \"content\": \"Допис, що зникне разом з темою\"}"));

        mockMvc.perform(delete("/api/topics/{id}", topicId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/topics/{topicId}/posts", topicId))
                .andExpect(status().isNotFound());
    }

    private long createTopic() throws Exception {
        String response = mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Тема для тестів дописів\", \"author\": \"Тестер\", \"category\": \"OTHER\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }
}
