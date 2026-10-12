package com.example.lab4;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TopicRestControllerTests {

    private static final String MERGE_PATCH = "application/merge-patch+json";
    private static final String JSON_PATCH = "application/json-patch+json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson("Нова тема про REST", "Артем", "STUDY")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Нова тема про REST"))
                .andExpect(jsonPath("$.author").value("Артем"))
                .andExpect(jsonPath("$.category").value("STUDY"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        long id = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
        mockMvc.perform(get("/api/topics/{id}", id)).andExpect(status().isOk());
        assertThat(result.getResponse().getHeader("Location"))
                .endsWith("/api/topics/" + id);
    }

    @Test
    void createWithInvalidDataReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"abc\", \"author\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.author").exists())
                .andExpect(jsonPath("$.errors.category").exists());
    }

    @Test
    void createWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdReturnsTopic() throws Exception {
        long id = createTopic("Тема для пошуку за id", "Аня", "GAMES");

        mockMvc.perform(get("/api/topics/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.category").value("GAMES"));
    }

    @Test
    void findByIdReturns404WhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/topics/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ресурс не знайдено"));
    }

    @Test
    void findByIdReturns400WhenIdIsNotNumber() throws Exception {
        mockMvc.perform(get("/api/topics/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listSupportsPagination() throws Exception {
        String author = uniqueName();
        createTopic("Перша тема автора", author, "STUDY");
        createTopic("Друга тема автора", author, "STUDY");
        createTopic("Третя тема автора", author, "STUDY");

        mockMvc.perform(get("/api/topics").param("author", author).param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Третя тема автора"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void listFiltersByKeywordAndCategory() throws Exception {
        String keyword = uniqueName();
        createTopic("Навчання " + keyword, "Іван", "STUDY");
        createTopic("Ігри " + keyword, "Іван", "GAMES");

        mockMvc.perform(get("/api/topics").param("keyword", keyword.toUpperCase()).param("category", "GAMES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Ігри " + keyword));
    }

    @Test
    void listReturns400ForInvalidParameters() throws Exception {
        mockMvc.perform(get("/api/topics").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.page").exists());

        mockMvc.perform(get("/api/topics").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size").exists());

        mockMvc.perform(get("/api/topics").param("category", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putReplacesAllFields() throws Exception {
        long id = createTopic("Тема до повного оновлення", "Дмитро", "STUDY");

        mockMvc.perform(put("/api/topics/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson("Повністю нова назва", "Микита", "OTHER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Повністю нова назва"))
                .andExpect(jsonPath("$.author").value("Микита"))
                .andExpect(jsonPath("$.category").value("OTHER"));
    }

    @Test
    void putReturns404WhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/topics/{id}", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson("Назва неіснуючої теми", "Софія", "OTHER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void putReturns400WhenFieldIsMissing() throws Exception {
        long id = createTopic("Тема для невдалого PUT", "Софія", "STUDY");

        mockMvc.perform(put("/api/topics/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Лише назва без автора\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("author")));
    }

    @Test
    void mergePatchChangesOnlyGivenFields() throws Exception {
        long id = createTopic("Тема для merge patch", "Андрій", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(MERGE_PATCH)
                        .content("{\"title\": \"Назва змінена через merge patch\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Назва змінена через merge patch"))
                .andExpect(jsonPath("$.author").value("Андрій"))
                .andExpect(jsonPath("$.category").value("STUDY"));
    }

    @Test
    void mergePatchWithNullForRequiredFieldReturns400() throws Exception {
        long id = createTopic("Тема для merge patch з null", "Андрій", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(MERGE_PATCH)
                        .content("{\"category\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.category").exists());
    }

    @Test
    void mergePatchWithUnknownFieldReturns400() throws Exception {
        long id = createTopic("Тема для merge patch з id", "Андрій", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(MERGE_PATCH)
                        .content("{\"id\": 42}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWithPlainJsonContentTypeReturns415() throws Exception {
        long id = createTopic("Тема для неправильного типу", "Андрій", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Будь-яка назва теми\"}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void jsonPatchAppliesOperationsInOrder() throws Exception {
        long id = createTopic("Тема для json patch", "Артем", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(JSON_PATCH)
                        .content("""
                                [
                                  {"op": "test", "path": "/author", "value": "Артем"},
                                  {"op": "replace", "path": "/title", "value": "Назва змінена через json patch"},
                                  {"op": "replace", "path": "/category", "value": "OTHER"}
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Назва змінена через json patch"))
                .andExpect(jsonPath("$.category").value("OTHER"))
                .andExpect(jsonPath("$.author").value("Артем"));
    }

    @Test
    void jsonPatchWithFailedTestReturns400AndKeepsTopic() throws Exception {
        long id = createTopic("Тема, яка не має змінитись", "Артем", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(JSON_PATCH)
                        .content("""
                                [
                                  {"op": "test", "path": "/author", "value": "Хтось інший"},
                                  {"op": "replace", "path": "/title", "value": "Цього не має статися"}
                                ]
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/topics/{id}", id))
                .andExpect(jsonPath("$.title").value("Тема, яка не має змінитись"));
    }

    @Test
    void jsonPatchThatIsNotArrayReturns400() throws Exception {
        long id = createTopic("Тема для неправильного patch", "Артем", "STUDY");

        mockMvc.perform(patch("/api/topics/{id}", id)
                        .contentType(JSON_PATCH)
                        .content("{\"op\": \"replace\", \"path\": \"/title\", \"value\": \"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonPatchReturns404WhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(patch("/api/topics/{id}", 999_999)
                        .contentType(JSON_PATCH)
                        .content("[{\"op\": \"replace\", \"path\": \"/title\", \"value\": \"Будь-яка назва\"}]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204AndTopicDisappears() throws Exception {
        long id = createTopic("Тема для видалення", "Іван", "OTHER");

        mockMvc.perform(delete("/api/topics/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/topics/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns404WhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/topics/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    private long createTopic(String title, String author, String category) throws Exception {
        String response = mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(topicJson(title, author, category)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private static String topicJson(String title, String author, String category) {
        return "{\"title\": \"%s\", \"author\": \"%s\", \"category\": \"%s\"}".formatted(title, author, category);
    }

    private static String uniqueName() {
        return "test" + UUID.randomUUID().toString().substring(0, 8);
    }
}
