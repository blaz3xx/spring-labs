package com.example.lab6.repositories;

import com.example.lab6.dto.TopicPostCount;
import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByTopic_IdOrderByIdAsc(Long topicId);

    Optional<Post> findByIdAndTopic_Id(Long id, Long topicId);

    List<Post> findByAuthorIgnoreCaseOrderByCreationDateDesc(String author);

    @Query("""
            SELECT p FROM Post p
            WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :text, '%'))
            ORDER BY p.creationDate DESC
            """)
    List<Post> searchByContent(@Param("text") String text);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Post p SET p.topic = :target WHERE p.topic = :source")
    int moveAll(@Param("source") Topic source, @Param("target") Topic target);

    List<TopicPostCount> countPerTopic();
}
