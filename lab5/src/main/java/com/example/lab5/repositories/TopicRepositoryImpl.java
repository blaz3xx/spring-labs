package com.example.lab5.repositories;

import com.example.lab5.models.Topic;
import com.example.lab5.models.TopicCategory;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TopicRepositoryImpl implements TopicRepository {

    private final JdbcClient jdbcClient;

    public TopicRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Long create(Topic topic) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO topics (title, author, category) VALUES (:title, :author, :category)")
                .param("title", topic.getTitle())
                .param("author", topic.getAuthor())
                .param("category", topic.getCategory() != null ? topic.getCategory().name() : null)
                .update(keyHolder, "id");
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Topic> read(Long id) {
        // Читаємо created_at з бази, щоб дата не оновлювалась щоразу на "зараз"
        return jdbcClient.sql("SELECT id, title, author, category, created_at AS creation_date FROM topics WHERE id = :id")
                .param("id", id)
                .query(Topic.class)
                .optional();
    }

    @Override
    public int update(Topic topic) {
        return jdbcClient.sql("UPDATE topics SET title = :title, author = :author, category = :category WHERE id = :id")
                .param("title", topic.getTitle())
                .param("author", topic.getAuthor())
                .param("category", topic.getCategory() != null ? topic.getCategory().name() : null)
                .param("id", topic.getId())
                .update();
    }

    @Override
    public int delete(Long id) {
        return jdbcClient.sql("DELETE FROM topics WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public List<Topic> findAll() {
        return search(null, null, null); 
    }

    @Override
    public List<Topic> search(String keyword, TopicCategory category, String author) {
        return jdbcClient.sql("""
                SELECT id, title, author, category, created_at AS creation_date
                FROM topics
                WHERE (CAST(:keyword AS text) IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
                  AND (CAST(:category AS text) IS NULL OR category = CAST(:category AS text))
                  AND (CAST(:author AS text) IS NULL OR LOWER(author) = LOWER(CAST(:author AS text)))
                ORDER BY id
                """)
                .param("keyword", keyword)
                .param("category", category != null ? category.name() : null)
                .param("author", author)
                .query(Topic.class)
                .list();
    }
}
