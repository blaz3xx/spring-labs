package com.example.lab2.controllers;

import com.example.lab2.models.Post;
import com.example.lab2.models.Topic;
import com.example.lab2.services.ForumService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller 
public class ForumController {
    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("topics", forumService.getAllTopics());
        return "index";
    }

    @GetMapping("/topic/{id}")
    public String viewTopic(@PathVariable Long id, Model model) {
        model.addAttribute("topic", forumService.getTopicById(id));
        model.addAttribute("posts", forumService.getPostsByTopic(id));
        return "topic";
    }

    @PostMapping("/topic/create")
    public String createTopic(@RequestParam String title, @RequestParam String author) {
        Topic topic = new Topic(null, title, author);
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
    public String editTopic(@PathVariable Long id, @RequestParam String title, @RequestParam String author) {
        Topic topic = forumService.getTopicById(id);
        if (topic != null) {
            topic.setTitle(title);
            topic.setAuthor(author);
            forumService.saveTopic(topic);
        }
        return "redirect:/";
    }
}
