package com.example.lab6;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void createReadAndDeleteTopic() throws Exception {
        String title = unique();
        String location = mvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(title)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value(title))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value(title));
        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void duplicateTitleReturns409() throws Exception {
        String title = unique();
        mvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content(topicJson(title)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content(topicJson(title)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Конфлікт даних"));
    }

    @Test
    void batchIsAllOrNothing() throws Exception {
        String first = unique();
        String second = unique();
        mvc.perform(post("/api/topics/batch").contentType(MediaType.APPLICATION_JSON)
                        .content("[" + topicJson(first) + "," + topicJson(second) + "]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)));

        String rolledBack = unique();
        mvc.perform(post("/api/topics/batch").contentType(MediaType.APPLICATION_JSON)
                        .content("[" + topicJson(rolledBack) + "," + topicJson(first) + "]"))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/topics").param("keyword", rolledBack))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void batchValidatesItsItems() throws Exception {
        mvc.perform(post("/api/topics/batch").contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/topics/batch").contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"title\": \"ab\", \"author\": \"\", \"category\": \"STUDY\"}]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mergeEndpoint() throws Exception {
        long source = createTopic(unique());
        long target = createTopic(unique());
        mvc.perform(post("/api/topics/" + source + "/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Тест\", \"content\": \"Допис для перенесення\"}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/topics/" + source + "/merge").param("targetId", String.valueOf(source)))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/topics/" + source + "/merge").param("targetId", "999999"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/topics/" + source + "/merge").param("targetId", String.valueOf(target)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movedPosts").value(1))
                .andExpect(jsonPath("$.removedTopicId").value(source));

        mvc.perform(get("/api/topics/" + source)).andExpect(status().isNotFound());
        mvc.perform(get("/api/topics/" + target + "/posts")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void queryEndpoints() throws Exception {
        String author = "Автор" + UUID.randomUUID().toString().substring(0, 6);
        long topic = createTopic(unique(), author);
        mvc.perform(post("/api/topics/" + topic + "/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"" + author + "\", \"content\": \"Унікальний текст " + author + "\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/topics/by-author/" + author.toUpperCase())).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/topics/popular").param("minPosts", "1")).andExpect(status().isOk());
        mvc.perform(get("/api/topics/popular").param("minPosts", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/topics/latest")).andExpect(status().isOk());
        mvc.perform(get("/api/topics/stats/categories")).andExpect(status().isOk());
        mvc.perform(get("/api/posts/search").param("text", author.toLowerCase())).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/posts/search").param("text", " ")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/posts").param("author", author)).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void pagesRender() throws Exception {
        long topic = createTopic(unique());
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/topic/" + topic)).andExpect(status().isOk());
    }

    private long createTopic(String title) throws Exception {
        return createTopic(title, "Тест");
    }

    private long createTopic(String title, String author) throws Exception {
        String body = mvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"" + title + "\", \"author\": \"" + author + "\", \"category\": \"STUDY\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private static String topicJson(String title) {
        return "{\"title\": \"" + title + "\", \"author\": \"Тест\", \"category\": \"STUDY\"}";
    }

    private static String unique() {
        return "Тема " + UUID.randomUUID().toString().substring(0, 8);
    }
}
