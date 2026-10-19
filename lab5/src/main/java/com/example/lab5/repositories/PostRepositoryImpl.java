package com.example.lab5.repositories;

import com.example.lab5.models.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> rowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTopicId(rs.getLong("topic_id")); 
        post.setContent(rs.getString("content"));
        post.setAuthor(rs.getString("author"));
        post.setCreationDate(rs.getTimestamp("created_at").toLocalDateTime());
        return post;
    };

    @Override
    public Long create(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO posts (topic_id, content, author) VALUES (?, ?, ?)";
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, post.getTopicId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAuthor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue(); 
    }

    @Override
    public Optional<Post> read(Long id) {
        String sql = "SELECT id, topic_id, content, author, created_at FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, rowMapper, id);
        return posts.stream().findFirst();
    }

    @Override
    public int update(Post post) {
        String sql = "UPDATE posts SET content = ?, author = ? WHERE id = ?";
        return jdbcTemplate.update(sql, post.getContent(), post.getAuthor(), post.getId());
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Post> findAll() {
        String sql = "SELECT id, topic_id, content, author, created_at FROM posts ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Post> findByTopicId(Long topicId) {
        String sql = "SELECT id, topic_id, content, author, created_at FROM posts WHERE topic_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper, topicId);
    }

    @Override
    public int deleteByTopicId(Long topicId) {
        String sql = "DELETE FROM posts WHERE topic_id = ?";
        return jdbcTemplate.update(sql, topicId);
    }
}
