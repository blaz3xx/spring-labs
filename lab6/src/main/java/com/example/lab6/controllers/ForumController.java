package com.example.lab6.controllers;

import com.example.lab6.dto.PageLink;
import com.example.lab6.dto.PageResponse;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.services.ForumService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Controller
public class ForumController {
    private static final int PAGE_SIZE = 12;

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) TopicCategory category,
                        @RequestParam(required = false) String q,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        String keyword = (q == null || q.isBlank()) ? null : q.strip();
        Map<TopicCategory, Long> categoryCounts = forumService.countTopicsByCategory();
        Map<Long, Long> replyCounts = forumService.countPostsByTopic();

        List<Topic> found = forumService.getTopics(keyword, category);
        int totalPages = Math.max(1, (int) Math.ceil((double) found.size() / PAGE_SIZE));
        int current = Math.max(0, Math.min(page, totalPages - 1));
        List<Topic> pageTopics = PageResponse.of(found, current, PAGE_SIZE, Function.identity()).content();

        model.addAttribute("topics", pageTopics);
        model.addAttribute("categories", TopicCategory.values());
        model.addAttribute("activeCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("replyCounts", replyCounts);
        model.addAttribute("totalTopics", categoryCounts.values().stream().mapToLong(Long::longValue).sum());
        model.addAttribute("totalReplies", replyCounts.values().stream().mapToLong(Long::longValue).sum());

        model.addAttribute("foundCount", found.size());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("from", found.isEmpty() ? 0 : current * PAGE_SIZE + 1);
        model.addAttribute("to", current * PAGE_SIZE + pageTopics.size());
        model.addAttribute("pageLinks", pageLinks(current, totalPages, category, keyword));
        model.addAttribute("prevHref", current > 0 ? pageHref(current - 1, category, keyword) : null);
        model.addAttribute("nextHref", current < totalPages - 1 ? pageHref(current + 1, category, keyword) : null);
        model.addAttribute("resetHref", pageHref(0, category, null));
        return "index";
    }

    private List<PageLink> pageLinks(int current, int totalPages, TopicCategory category, String keyword) {
        List<PageLink> links = new ArrayList<>();
        int previous = -1;
        for (int p = 0; p < totalPages; p++) {
            boolean visible = p == 0 || p == totalPages - 1 || Math.abs(p - current) <= 1;
            if (!visible) {
                continue;
            }
            if (previous != -1 && p - previous > 1) {
                links.add(PageLink.gap());
            }
            links.add(new PageLink(String.valueOf(p + 1), pageHref(p, category, keyword), p == current));
            previous = p;
        }
        return links;
    }

    private String pageHref(int page, TopicCategory category, String keyword) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");
        if (category != null) {
            builder.queryParam("category", category.name());
        }
        if (keyword != null) {
            builder.queryParam("q", keyword);
        }
        if (page > 0) {
            builder.queryParam("page", page);
        }
        return builder.build().encode().toUriString();
    }

    @GetMapping("/topic/{id}")
    public String viewTopic(@PathVariable Long id, Model model) {
        Topic topic = forumService.getTopicById(id);
        if (topic == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Тему не знайдено");
        }
        model.addAttribute("topic", topic);
        model.addAttribute("posts", forumService.getPostsByTopic(id));
        return "topic";
    }

    @PostMapping("/topic/create")
    public String createTopic(@RequestParam String title,
                              @RequestParam String author,
                              @RequestParam(defaultValue = "OTHER") TopicCategory category) {
        saveOrConflict(new Topic(null, title, author, category));
        return "redirect:/";
    }

    @PostMapping("/topic/{id}/delete")
    public String deleteTopic(@PathVariable Long id) {
        forumService.deleteTopic(id);
        return "redirect:/";
    }

    @PostMapping("/topic/{id}/post")
    public String createPost(@PathVariable Long id, @RequestParam String content, @RequestParam String author) {
        forumService.createPost(id, content, author);
        return "redirect:/topic/" + id;
    }

    @PostMapping("/topic/{id}/edit")
    public String editTopic(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String author,
                            @RequestParam(defaultValue = "OTHER") TopicCategory category) {
        Topic topic = forumService.getTopicById(id);
        if (topic != null) {
            topic.setTitle(title);
            topic.setAuthor(author);
            topic.setCategory(category);
            saveOrConflict(topic);
        }
        return "redirect:/";
    }

    private void saveOrConflict(Topic topic) {
        try {
            forumService.saveTopic(topic);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Тема з такою назвою вже існує");
        }
    }
}
