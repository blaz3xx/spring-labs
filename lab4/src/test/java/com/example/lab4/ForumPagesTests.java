package com.example.lab4;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForumPagesTests {

    private static final String CARD = "<li class=\"card\">";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void indexIsSplitIntoPagesOfTwelveTopics() throws Exception {
        String token = createThirteenTopics();

        String firstPage = pageHtml("/?q=" + token);
        assertThat(count(firstPage, CARD)).isEqualTo(12);
        assertThat(firstPage).contains("class=\"pager\"").contains("Показано").contains("page=1");

        String secondPage = pageHtml("/?q=" + token + "&page=1");
        assertThat(count(secondPage, CARD)).isEqualTo(1);
    }

    @Test
    void pageNumberOutOfRangeIsClamped() throws Exception {
        String token = createThirteenTopics();

        String lastPage = pageHtml("/?q=" + token + "&page=999");
        assertThat(count(lastPage, CARD)).isEqualTo(1);

        String firstPage = pageHtml("/?q=" + token + "&page=-5");
        assertThat(count(firstPage, CARD)).isEqualTo(12);
    }

    @Test
    void searchLooksThroughAllPagesAndKeepsCategory() throws Exception {
        String token = createThirteenTopics();

        String found = pageHtml("/?q=" + token.toUpperCase() + "&category=STUDY");
        assertThat(count(found, CARD)).isEqualTo(12);
        assertThat(found).contains("Скинути пошук");

        String none = pageHtml("/?q=" + token + "&category=GAMES");
        assertThat(count(none, CARD)).isZero();
        assertThat(none).contains("Нічого не знайдено");
    }

    @Test
    void pagerIsHiddenWhenEverythingFitsOnOnePage() throws Exception {
        String html = pageHtml("/?q=" + UUID.randomUUID());
        assertThat(html).doesNotContain("class=\"pager\"");
    }

    @Test
    void unknownCategoryReturns400() throws Exception {
        mockMvc.perform(get("/").param("category", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pageContainsDarkFooterWithApiLinks() throws Exception {
        mockMvc.perform(get("/topic/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("site-footer")))
                .andExpect(content().string(containsString("/swagger-ui.html")))
                .andExpect(content().string(not(containsString("<script>alert"))));
    }

    private String createThirteenTopics() throws Exception {
        String token = "pg" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        for (int i = 1; i <= 13; i++) {
            mockMvc.perform(post("/topic/create")
                            .param("title", "Тема " + token + " №" + i)
                            .param("author", "Тестер")
                            .param("category", "STUDY"))
                    .andExpect(status().is3xxRedirection());
        }
        return token;
    }

    private String pageHtml(String url) throws Exception {
        return mockMvc.perform(get(url.replace(" ", "%20")))
                .andReturn().getResponse().getContentAsString();
    }

    private static int count(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }
}
