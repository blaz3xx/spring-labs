package com.example.lab6.repositories;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Long> {

    @Query("""
            SELECT t FROM Topic t
            WHERE (CAST(:keyword AS String) IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%')))
              AND (:category IS NULL OR t.category = :category)
              AND (CAST(:author AS String) IS NULL OR LOWER(t.author) = LOWER(CAST(:author AS String)))
            ORDER BY t.id
            """)
    List<Topic> search(@Param("keyword") String keyword,
                       @Param("category") TopicCategory category,
                       @Param("author") String author);

    @Query("""
            SELECT new com.example.lab6.dto.CategoryStats(t.category, COUNT(t))
            FROM Topic t
            GROUP BY t.category
            ORDER BY t.category
            """)
    List<CategoryStats> countByCategoryGrouped();

    List<Topic> findPopular(@Param("minPosts") int minPosts);

    List<Topic> findByAuthorIgnoreCaseOrderByCreationDateDesc(String author);

    List<Topic> findTop5ByOrderByCreationDateDesc();

    boolean existsByTitleIgnoreCase(String title);

    long countByCategory(TopicCategory category);
}
