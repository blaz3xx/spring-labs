package com.example.lab3.controllers;

import com.example.lab3.models.Post;
import com.example.lab3.models.Topic;
import com.example.lab3.models.TopicCategory;
import com.example.lab3.services.ForumService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ForumController {
    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("topics", forumService.getAllTopics());
        model.addAttribute("categories", TopicCategory.values());
        return "index";
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
        Topic topic = new Topic(null, title, author, category);
        forumService.saveTopic(topic);
        return "redirect:/";
    }

    @PostMapping("/topic/{id}/delete")
    public String deleteTopic(@PathVariable Long id) {
        forumService.deleteTopic(id);
        return "redirect:/";
    }

    @PostMapping("/topic/{id}/post")
    public String createPost(@PathVariable Long id, @RequestParam String content, @RequestParam String author) {
        Post post = new Post(null, content, author, id);
        forumService.createPost(post);
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
            forumService.saveTopic(topic);
        }
        return "redirect:/";
    }
}