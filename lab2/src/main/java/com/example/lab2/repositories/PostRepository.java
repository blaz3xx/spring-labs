package com.example.lab2.repositories;

import com.example.lab2.models.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository 
public class PostRepository {
    private final List<Post> posts = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public PostRepository() {
        save(new Post(null, "Треба читати офіційну документацію", "Викладач", 1L));
    }

    public List<Post> findByTopicId(Long topicId) {
        return posts.stream()
                .filter(post -> post.getTopicId().equals(topicId))
                .collect(Collectors.toList());
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(counter.getAndIncrement());
            posts.add(post);
        }
        return post;
    }
}
