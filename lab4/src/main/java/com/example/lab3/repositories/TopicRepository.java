package com.example.lab3.repositories;

import com.example.lab3.models.Topic;
import com.example.lab3.models.TopicCategory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TopicRepository {
    private final List<Topic> topics = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public TopicRepository() {
        save(new Topic(null, "Як швидко вивчити Spring Boot?", "Студент", TopicCategory.STUDY));
        save(new Topic(null, "Обговорення комп'ютерних ігор", "Гість", TopicCategory.GAMES));
    }

    public List<Topic> findAll() {
        return new ArrayList<>(topics);
    }

    // НОВЫЙ МЕТОД: Пагинация (разбиение на страницы)
    public List<Topic> findAllWithPagination(int page, int size) {
        return topics.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    // НОВЫЙ МЕТОД: Фильтрация по названию + Пагинация
    public List<Topic> searchByKeywordWithPagination(String keyword, int page, int size) {
        return topics.stream()
                .filter(topic -> topic.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
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
            // Обновление существующего топика
            topics.removeIf(t -> t.getId().equals(topic.getId()));
            topics.add(topic);
        }
        return topic;
    }

    public void deleteById(Long id) {
        topics.removeIf(topic -> topic.getId().equals(id));
    }

    public boolean existsById(Long id) {
        return topics.stream().anyMatch(topic -> topic.getId().equals(id));
    }
}