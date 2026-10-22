package com.example.lab6;

import com.example.lab6.dto.MergeResponse;
import com.example.lab6.dto.TopicRequest;
import com.example.lab6.dto.TopicResponse;
import com.example.lab6.exceptions.NotFoundException;
import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.repositories.PostRepository;
import com.example.lab6.repositories.TopicRepository;
import com.example.lab6.services.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TransactionTests {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void batchWithoutErrorsCreatesAllTopics() {
        String prefix = unique();
        long before = topicRepository.count();

        List<TopicResponse> created = topicService.createBatch(List.of(
                request(prefix + " перша"), request(prefix + " друга"), request(prefix + " третя")));

        assertThat(created).hasSize(3).allSatisfy(topic -> assertThat(topic.id()).isNotNull());
        assertThat(topicRepository.count()).isEqualTo(before + 3);
    }

    @Test
    void batchWithDuplicateTitleIsRolledBackCompletely() {
        String prefix = unique();
        topicService.create(request(prefix + " існуюча"));
        long before = topicRepository.count();

        assertThatThrownBy(() -> topicService.createBatch(List.of(
                request(prefix + " нова до помилки"), request(prefix + " існуюча"))))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(topicRepository.count()).isEqualTo(before);
        assertThat(topicRepository.existsByTitleIgnoreCase(prefix + " нова до помилки")).isFalse();
    }

    @Test
    void mergeMovesPostsAndRemovesSource() {
        Topic source = topicWithPosts(2);
        Topic target = topicWithPosts(1);

        MergeResponse result = topicService.merge(source.getId(), target.getId());

        assertThat(result.movedPosts()).isEqualTo(2);
        assertThat(topicRepository.existsById(source.getId())).isFalse();
        assertThat(postRepository.findByTopic_IdOrderByIdAsc(target.getId())).hasSize(3);
    }

    @Test
    void mergeWithMissingTargetChangesNothing() {
        Topic source = topicWithPosts(2);

        assertThatThrownBy(() -> topicService.merge(source.getId(), Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class);

        assertThat(topicRepository.existsById(source.getId())).isTrue();
        assertThat(postRepository.findByTopic_IdOrderByIdAsc(source.getId())).hasSize(2);
    }

    @Test
    void deleteRemovesTopicWithPosts() {
        Topic topic = topicWithPosts(3);

        topicService.delete(topic.getId());

        assertThat(topicRepository.existsById(topic.getId())).isFalse();
        assertThat(postRepository.findByTopic_IdOrderByIdAsc(topic.getId())).isEmpty();
    }

    private Topic topicWithPosts(int posts) {
        Topic topic = new Topic(null, unique(), "Тест", TopicCategory.OTHER);
        for (int i = 1; i <= posts; i++) {
            topic.addPost(new Post("Допис " + i, "Тест"));
        }
        return topicRepository.save(topic);
    }

    private static TopicRequest request(String title) {
        return new TopicRequest(title, "Тест", TopicCategory.STUDY);
    }

    private static String unique() {
        return "Тема " + UUID.randomUUID().toString().substring(0, 8);
    }
}
