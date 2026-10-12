package com.example.lab4.repositories;

import com.example.lab4.models.Topic;
import com.example.lab4.models.TopicCategory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TopicRepository {

    private final List<Topic> topics = new CopyOnWriteArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public TopicRepository() {
        seed("Як швидко вивчити Spring Boot?", "Студент", TopicCategory.STUDY, 240);
        seed("Обговорення комп'ютерних ігор", "Гість", TopicCategory.GAMES, 216);
        seed("Що таке REST і навіщо він потрібен?", "Студент", TopicCategory.STUDY, 192);
        seed("Чим відрізняється @Component від @Bean?", "Аня", TopicCategory.STUDY, 168);
        seed("Яку гру для командного вечора обрати?", "Микита", TopicCategory.GAMES, 144);
        seed("Як налаштувати CORS у Spring Boot 4?", "Андрій", TopicCategory.STUDY, 120);
        seed("Де взяти датасет для курсової?", "Іван", TopicCategory.OTHER, 96);
        seed("Чи варто переходити на Java 25 уже зараз?", "Дмитро", TopicCategory.STUDY, 84);
        seed("Postman чи Swagger: чим тестувати API?", "Софія", TopicCategory.STUDY, 72);
        seed("Де зручно навчатися поруч з КПІ?", "Артем", TopicCategory.OTHER, 60);
        seed("Які ігри з кооперативом ви порадите?", "Аня", TopicCategory.GAMES, 48);
        seed("Як організувати командну роботу над лабами?", "Дмитро", TopicCategory.OTHER, 36);
        seed("DTO чи сутність: що повертати з контролера?", "Софія", TopicCategory.STUDY, 30);
        seed("Прохідні ігри осені: що вже вийшло?", "Микита", TopicCategory.GAMES, 24);
        seed("Як працюють фрагменти в Thymeleaf?", "Андрій", TopicCategory.STUDY, 18);
        seed("Які книжки про архітектуру ПЗ варто прочитати?", "Іван", TopicCategory.OTHER, 12);
        seed("Настільні ігри для вечора перед сесією", "Артем", TopicCategory.GAMES, 6);
        seed("Чи потрібні тести в навчальному проєкті?", "Софія", TopicCategory.STUDY, 2);
    }

    private void seed(String title, String author, TopicCategory category, long hoursAgo) {
        Topic topic = new Topic(null, title, author, category);
        topic.setCreationDate(LocalDateTime.now().minusHours(hoursAgo));
        save(topic);
    }

    public List<Topic> findAll() {
        return List.copyOf(topics);
    }

    public List<Topic> search(String keyword, TopicCategory category, String author) {
        return topics.stream()
                .filter(topic -> keyword == null || topic.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .filter(topic -> category == null || topic.getCategory() == category)
                .filter(topic -> author == null || topic.getAuthor().equalsIgnoreCase(author))
                .sorted(Comparator.comparing(Topic::getId))
                .toList();
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
            topics.replaceAll(existing -> existing.getId().equals(topic.getId()) ? topic : existing);
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
