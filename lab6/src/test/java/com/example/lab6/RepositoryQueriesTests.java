package com.example.lab6;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.dto.TopicPostCount;
import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.repositories.PostRepository;
import com.example.lab6.repositories.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryQueriesTests {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Topic springTopic;
    private Topic gamesTopic;
    private Topic emptyTopic;

    @BeforeEach
    void setUp() {
        springTopic = topic("Як вивчити Spring Data JPA", "Аня", TopicCategory.STUDY,
                "Почніть з CrudRepository", "Spring генерує запити за назвою методу", "Дякую!");
        gamesTopic = topic("Кооперативні ігри", "Іван", TopicCategory.GAMES, "It Takes Two");
        emptyTopic = topic("Тема без відповідей", "аня", TopicCategory.STUDY);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void searchWithQueryAnnotationCombinesFilters() {
        assertThat(topicRepository.search("spring", null, null)).extracting(Topic::getId).containsExactly(springTopic.getId());
        assertThat(topicRepository.search(null, TopicCategory.STUDY, "АНЯ")).extracting(Topic::getId)
                .containsExactly(springTopic.getId(), emptyTopic.getId());
        assertThat(topicRepository.search(null, null, null)).hasSize(3);
        assertThat(topicRepository.search("ігри", TopicCategory.STUDY, null)).isEmpty();
    }

    @Test
    void categoryStatsQueryGroupsTopics() {
        assertThat(topicRepository.countByCategoryGrouped()).containsExactly(
                new CategoryStats(TopicCategory.GAMES, 1L),
                new CategoryStats(TopicCategory.STUDY, 2L));
    }

    @Test
    void namedQueryFindsPopularTopics() {
        assertThat(topicRepository.findPopular(2)).extracting(Topic::getId).containsExactly(springTopic.getId());
        assertThat(topicRepository.findPopular(0)).extracting(Topic::getId)
                .containsExactly(springTopic.getId(), gamesTopic.getId(), emptyTopic.getId());
    }

    @Test
    void namedQueryCountsPostsPerTopic() {
        assertThat(postRepository.countPerTopic()).containsExactlyInAnyOrder(
                new TopicPostCount(springTopic.getId(), 3L),
                new TopicPostCount(gamesTopic.getId(), 1L));
    }

    @Test
    void derivedTopicQueries() {
        assertThat(topicRepository.findByAuthorIgnoreCaseOrderByCreationDateDesc("АНЯ")).hasSize(2);
        assertThat(topicRepository.findTop5ByOrderByCreationDateDesc()).hasSize(3);
        assertThat(topicRepository.existsByTitleIgnoreCase("кооперативні ІГРИ")).isTrue();
        assertThat(topicRepository.existsByTitleIgnoreCase("Немає такої теми")).isFalse();
        assertThat(topicRepository.countByCategory(TopicCategory.STUDY)).isEqualTo(2);
    }

    @Test
    void derivedAndJpqlPostQueries() {
        List<Post> posts = postRepository.findByTopic_IdOrderByIdAsc(springTopic.getId());
        assertThat(posts).extracting(Post::getContent)
                .containsExactly("Почніть з CrudRepository", "Spring генерує запити за назвою методу", "Дякую!");
        assertThat(postRepository.findByIdAndTopic_Id(posts.getFirst().getId(), springTopic.getId())).isPresent();
        assertThat(postRepository.findByIdAndTopic_Id(posts.getFirst().getId(), gamesTopic.getId())).isEmpty();
        assertThat(postRepository.searchByContent("SPRING")).extracting(Post::getContent)
                .containsExactly("Spring генерує запити за назвою методу");
        assertThat(postRepository.findByAuthorIgnoreCaseOrderByCreationDateDesc("автор")).hasSize(4);
    }

    @Test
    void moveAllTransfersPostsBetweenTopics() {
        Topic source = topicRepository.findById(springTopic.getId()).orElseThrow();
        Topic target = topicRepository.findById(gamesTopic.getId()).orElseThrow();

        assertThat(postRepository.moveAll(source, target)).isEqualTo(3);
        assertThat(postRepository.findByTopic_IdOrderByIdAsc(gamesTopic.getId())).hasSize(4);
        assertThat(postRepository.findByTopic_IdOrderByIdAsc(springTopic.getId())).isEmpty();
    }

    @Test
    void identityAndSequenceGenerateKeys() {
        Topic topic = topicRepository.save(new Topic(null, "Нова тема з ключем", "Тест", TopicCategory.OTHER));
        topic.addPost(new Post("Перший допис", "Тест"));
        entityManager.flush();

        assertThat(topic.getId()).isNotNull();
        assertThat(topic.getCreationDate()).isNotNull();
        assertThat(topic.getPosts().getFirst().getId()).isNotNull();
    }

    private Topic topic(String title, String author, TopicCategory category, String... contents) {
        Topic topic = new Topic(null, title, author, category);
        for (String content : contents) {
            topic.addPost(new Post(content, "Автор"));
        }
        return entityManager.persist(topic);
    }
}
