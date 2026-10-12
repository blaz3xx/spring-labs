package com.example.lab4.repositories;

import com.example.lab4.models.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final List<Post> posts = new CopyOnWriteArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public PostRepository() {
        seed(1L, "Викладач", "Треба читати офіційну документацію", 230);
        seed(1L, "Аня", "Почніть зі Spring Initializr і гайду «Building a RESTful Web Service» на spring.io.", 200);
        seed(1L, "Дмитро", "А ще корисно одразу писати тести через MockMvc — так швидше розумієш, що повертає контролер.", 150);

        seed(2L, "Микита", "Зараз грають у щось із кооперативом чи просто кожен у своє?", 200);
        seed(2L, "Гість", "Здебільшого у кооператив, так цікавіше.", 190);

        seed(3L, "Софія", "REST — це стиль, а не протокол: ресурси, HTTP-методи й коди стану роблять API передбачуваним.", 180);
        seed(3L, "Артем", "І не забувайте про PATCH: часткове оновлення економить трафік.", 150);
        seed(3L, "Аня", "А ще Swagger. Одразу видно, що можна викликати і які коди повернуться.", 120);
        seed(3L, "Микита", "Postman теж рятує: колекцію з тестами можна запускати за один клік.", 96);

        seed(4L, "Андрій", "@Component ставимо на свій клас, а @Bean — на метод, коли об'єкт створюємо вручну, наприклад із сторонньої бібліотеки.", 150);
        seed(4L, "Іван", "Так, і ще через @Bean зручно налаштувати область видимості та параметри створення.", 130);

        seed(5L, "Аня", "Щось кооперативне, щоб ніхто не сварився. Підійде будь-яка гра, де рівні проходять разом.", 130);
        seed(5L, "Артем", "Підтримую. І бажано, щоб партія тривала не довше години.", 110);
        seed(5L, "Дмитро", "Можна ще проголосувати: кожен пропонує по одній грі, а потім вибираємо.", 90);

        seed(6L, "Софія", "Додайте @CrossOrigin на контролер або глобальну конфігурацію через WebMvcConfigurer — залежить від того, скільки ендпоїнтів треба відкрити.", 100);
        seed(6L, "Андрій", "Дякую! Глобальна конфігурація підійшла: у мене один фронтенд і весь /api/**.", 90);

        seed(8L, "Іван", "Для навчальних проєктів — так: LTS, нові можливості мови, і Spring Boot 4 її підтримує.", 70);
        seed(8L, "Артем", "Головне, щоб IDE знала цю версію. У мене плагін досі показував попередження про Java 27.", 55);
        seed(8L, "Дмитро", "Виправив: поставив 25 у pom.xml, попередження зникли.", 40);

        seed(9L, "Аня", "Swagger показує, що API вміє, Postman перевіряє, чи воно це справді робить. Разом вони доповнюють одне одного.", 60);
        seed(9L, "Софія", "Погоджуюсь. Я спершу дивлюсь документацію, а потім пишу колекцію з тестами.", 45);

        seed(10L, "Іван", "Бібліотека університету — тиха і з розетками. Приходьте зранку, щоб знайти місце.", 50);
        seed(10L, "Микита", "А ще є кілька коворкінгів недалеко від метро.", 35);

        seed(11L, "Микита", "Спробуйте ігри, де потрібно ділити ролі: один веде, інший шукає підказки.", 40);
        seed(11L, "Артем", "Дякую за ідею, запишу собі на п'ятницю.", 30);

        seed(12L, "Софія", "Заведіть спільний репозиторій і розбийте завдання на дрібні частини: так менше конфліктів.", 30);
        seed(12L, "Андрій", "І домовтеся про формат комітів, щоб історія залишалася зрозумілою.", 24);
        seed(12L, "Аня", "Ще допомагає короткий созвон перед здачею: перевірити, що все працює.", 14);

        seed(13L, "Дмитро", "Краще DTO: клієнт бачить лише потрібні поля, а модель можна змінювати без поломки API.", 24);
        seed(13L, "Іван", "Ще й безпечніше: випадково не віддаси службові поля.", 16);

        seed(14L, "Артем", "Вийшло кілька цікавих проєктів, дивіться добірку критиків за жовтень.", 18);

        seed(15L, "Софія", "Фрагмент — це шматок шаблону, який можна підключити в різних сторінках через th:replace або th:insert.", 14);
        seed(15L, "Микита", "Ми так винесли шапку й футер, тепер їх не треба копіювати на кожну сторінку.", 8);

        seed(16L, "Аня", "«Чистий код» Роберта Мартіна — класика, а з архітектури раджу «Чисту архітектуру» того ж автора.", 8);

        seed(18L, "Андрій", "Так, хоча б на ключові сценарії: тести ловлять помилки раніше за викладача.", 1);
    }

    private void seed(long topicId, String author, String content, long hoursAgo) {
        Post post = new Post(null, content, author, topicId);
        post.setCreationDate(LocalDateTime.now().minusHours(hoursAgo));
        save(post);
    }

    public List<Post> findAll() {
        return List.copyOf(posts);
    }

    public List<Post> findByTopicId(Long topicId) {
        return posts.stream()
                .filter(post -> post.getTopicId().equals(topicId))
                .sorted(Comparator.comparing(Post::getId))
                .toList();
    }

    public Optional<Post> findById(Long id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(counter.getAndIncrement());
            posts.add(post);
        } else {
            posts.replaceAll(existing -> existing.getId().equals(post.getId()) ? post : existing);
        }
        return post;
    }

    public void deleteById(Long id) {
        posts.removeIf(post -> post.getId().equals(id));
    }

    public void deleteByTopicId(Long topicId) {
        posts.removeIf(post -> post.getTopicId().equals(topicId));
    }
}
