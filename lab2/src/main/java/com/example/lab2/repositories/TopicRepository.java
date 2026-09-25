package com.example.lab2.repositories;

import com.example.lab2.models.Topic;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TopicRepository {
    private final List<Topic> topics = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public TopicRepository() {
        save(new Topic(null, "Як швидко вивчити Spring Boot?", "Студент"));
        save(new Topic(null, "Обговорення комп'ютерних ігор", "Гість"));
    }

    public List<Topic> findAll() {
        return new ArrayList<>(topics);
    }

    public Optional<Topic> findById(Long id) {
        return topics.stream()
                .filter(topic -> topic.getId().equals(id))
                .findFirst();
    }

    public Topic save(Topic topic) {
        if (topic.getId() == null) {
            topic.setId(counter.getAndIncrement());
            topics.add(topic);
        } else {
            topics.removeIf(t -> t.getId().equals(topic.getId()));
            topics.add(topic);
        }
        return topic;
    }

    public void deleteById(Long id) {
        topics.removeIf(topic -> topic.getId().equals(id));
    }
}