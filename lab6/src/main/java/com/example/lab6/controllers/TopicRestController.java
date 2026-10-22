package com.example.lab6.controllers;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.dto.MergeResponse;
import com.example.lab6.dto.PageResponse;
import com.example.lab6.dto.PopularTopicResponse;
import com.example.lab6.dto.TopicRequest;
import com.example.lab6.dto.TopicResponse;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.services.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.StringReader;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Теми", description = "Створення, перегляд, оновлення та видалення тем форуму")
public class TopicRestController {

    public static final String MERGE_PATCH_JSON = "application/merge-patch+json";
    public static final String JSON_PATCH_JSON = "application/json-patch+json";

    private final TopicService topicService;

    public TopicRestController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    @Operation(
            summary = "Отримати список тем",
            description = "Повертає одну сторінку тем, відсортованих за id. Список можна відфільтрувати за частиною "
                    + "назви (keyword), категорією (category) та автором (author); фільтри можна поєднувати. "
                    + "Параметри page і size керують пагінацією: page — номер сторінки з нуля, size — кількість тем на сторінці.")
    @ApiResponse(responseCode = "200", description = "Сторінку тем повернуто")
    @ApiResponse(responseCode = "400", description = "Некоректні параметри: від'ємний page, size поза межами 1–100 або невідома категорія",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public PageResponse<TopicResponse> findAll(
            @Parameter(description = "Частина назви теми, регістр не враховується", example = "spring")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Категорія теми")
            @RequestParam(required = false) TopicCategory category,
            @Parameter(description = "Ім'я автора, регістр не враховується", example = "Студент")
            @RequestParam(required = false) String author,
            @Parameter(description = "Номер сторінки, починаючи з 0", example = "0")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Номер сторінки не може бути від'ємним") int page,
            @Parameter(description = "Кількість тем на сторінці, від 1 до 100", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Розмір сторінки має бути від 1 до 100")
            @Max(value = 100, message = "Розмір сторінки має бути від 1 до 100") int size) {
        return topicService.findAll(keyword, category, author, page, size);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати тему за id",
            description = "Повертає одну тему за її ідентифікатором. Якщо теми з таким id немає, повертається 404.")
    @ApiResponse(responseCode = "200", description = "Тему знайдено")
    @ApiResponse(responseCode = "400", description = "id не є числом",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public TopicResponse findById(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long id) {
        return topicService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Створити тему",
            description = "Створює нову тему з переданих даних. У разі успіху повертає створену тему, статус 201 "
                    + "і заголовок Location з адресою нового ресурсу. Усі поля обов'язкові.")
    @ApiResponse(responseCode = "201", description = "Тему створено")
    @ApiResponse(responseCode = "400", description = "Дані не пройшли перевірку або тіло запиту некоректне",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Тема з такою назвою вже існує",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<TopicResponse> create(@Valid @RequestBody TopicRequest request) {
        TopicResponse created = topicService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Повністю оновити тему",
            description = "Замінює всі поля теми значеннями з тіла запиту. Потрібно передати всі поля, "
                    + "навіть ті, що не змінюються. Для зміни лише окремих полів використовуйте PATCH.")
    @ApiResponse(responseCode = "200", description = "Тему оновлено")
    @ApiResponse(responseCode = "400", description = "Дані не пройшли перевірку або тіло запиту некоректне",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Тема з такою назвою вже існує",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public TopicResponse update(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long id,
                                @Valid @RequestBody TopicRequest request) {
        return topicService.update(id, request);
    }

    @PatchMapping(path = "/{id}", consumes = {MERGE_PATCH_JSON, JSON_PATCH_JSON})
    @Operation(
            summary = "Частково оновити тему (RFC 7386 або RFC 6902)",
            description = "Змінює лише частину полів теми. Формат тіла визначає заголовок Content-Type. "
                    + "application/merge-patch+json (JSON Merge Patch, RFC 7386): передається об'єкт лише з полями, "
                    + "які потрібно змінити, решта лишається без змін; значення null видаляє поле, тому для обов'язкових "
                    + "полів призводить до помилки 400. "
                    + "application/json-patch+json (JSON Patch, RFC 6902): передається масив операцій add, remove, "
                    + "replace, move, copy, test, які виконуються по черзі; якщо хоча б одна не вдалася "
                    + "(наприклад, test не збігся), тема не змінюється. "
                    + "В обох випадках результат перевіряється за тими самими правилами, що й під час створення.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON Merge Patch (об'єкт) або JSON Patch (масив операцій) залежно від Content-Type",
            content = {
                    @Content(mediaType = MERGE_PATCH_JSON,
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(name = "Змінити назву і категорію",
                                    value = "{\"title\": \"Нова назва теми\", \"category\": \"OTHER\"}")),
                    @Content(mediaType = JSON_PATCH_JSON,
                            schema = @Schema(type = "array"),
                            examples = @ExampleObject(name = "Перевірити автора і змінити назву",
                                    value = "[{\"op\": \"test\", \"path\": \"/author\", \"value\": \"Студент\"},"
                                            + " {\"op\": \"replace\", \"path\": \"/title\", \"value\": \"Оновлена назва теми\"}]"))
            })
    @ApiResponse(responseCode = "200", description = "Тему частково оновлено")
    @ApiResponse(responseCode = "400", description = "Некоректний patch-документ, операція не виконалась, невідоме поле або результат не пройшов перевірку",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Тема з такою назвою вже існує",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "415", description = "Непідтримуваний Content-Type",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public TopicResponse patch(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long id,
                               @Parameter(hidden = true) @RequestHeader(HttpHeaders.CONTENT_TYPE) MediaType contentType,
                               @RequestBody String body) {
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            if (contentType.isCompatibleWith(MediaType.parseMediaType(JSON_PATCH_JSON))) {
                return topicService.jsonPatch(id, Json.createPatch(reader.readArray()));
            }
            return topicService.mergePatch(id, Json.createMergePatch(reader.readValue()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Видалити тему",
            description = "Видаляє тему разом з усіма її дописами. У разі успіху повертає 204 без тіла відповіді.")
    @ApiResponse(responseCode = "204", description = "Тему видалено")
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public void delete(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long id) {
        topicService.delete(id);
    }

    @PostMapping("/batch")
    @Operation(
            summary = "Створити кілька тем однією транзакцією",
            description = "Створює всі теми зі списку в межах однієї транзакції (@Transactional). Теми зберігаються "
                    + "по черзі; якщо будь-яка з них порушує обмеження бази даних (наприклад, назва вже зайнята "
                    + "іншою темою або повторюється в самому списку), транзакція відкочується і не зберігається "
                    + "жодна тема зі списку, навіть ті, що були вставлені до помилки. Список має містити від 1 до 20 тем, "
                    + "кожна перевіряється тими самими правилами, що й під час створення однієї теми.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Список тем для створення",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(name = "Успішне створення",
                                    value = "[{\"title\": \"Перша тема з пакета\", \"author\": \"Артем\", \"category\": \"STUDY\"},"
                                            + " {\"title\": \"Друга тема з пакета\", \"author\": \"Артем\", \"category\": \"OTHER\"}]"),
                            @ExampleObject(name = "Відкат через дублікат назви",
                                    value = "[{\"title\": \"Нова тема перед помилкою\", \"author\": \"Артем\", \"category\": \"STUDY\"},"
                                            + " {\"title\": \"Обговорення нових ігор\", \"author\": \"Артем\", \"category\": \"GAMES\"}]")
                    }))
    @ApiResponse(responseCode = "201", description = "Усі теми створено")
    @ApiResponse(responseCode = "400", description = "Порожній або завеликий список, некоректні дані однієї з тем",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Порушено обмеження БД; транзакцію відкочено, жодну тему не створено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ResponseStatus(HttpStatus.CREATED)
    public List<TopicResponse> createBatch(
            @RequestBody
            @NotEmpty(message = "Список тем не може бути порожнім")
            @Size(max = 20, message = "За один раз можна створити не більше 20 тем")
            List<@Valid TopicRequest> requests) {
        return topicService.createBatch(requests);
    }

    @PostMapping("/{id}/merge")
    @Operation(
            summary = "Об'єднати дві теми",
            description = "Транзакційна операція: переносить усі дописи теми-джерела (id) до цільової теми (targetId) "
                    + "одним запитом UPDATE, після чого видаляє тему-джерело. Обидві зміни виконуються в одній "
                    + "транзакції: якщо будь-який крок завершиться помилкою, дописи лишаться у своїй темі, а тема-джерело "
                    + "не буде видалена. Повертає цільову тему і кількість перенесених дописів.")
    @ApiResponse(responseCode = "200", description = "Теми об'єднано")
    @ApiResponse(responseCode = "400", description = "id і targetId збігаються або не є числами",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Тему-джерело або цільову тему не знайдено; дані не змінено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public MergeResponse merge(@Parameter(description = "Ідентифікатор теми-джерела, яку буде видалено", example = "6")
                               @PathVariable Long id,
                               @Parameter(description = "Ідентифікатор цільової теми, до якої переносяться дописи", example = "4")
                               @RequestParam Long targetId) {
        return topicService.merge(id, targetId);
    }

    @GetMapping("/popular")
    @Operation(
            summary = "Популярні теми",
            description = "Повертає теми, у яких щонайменше minPosts дописів, від найбільшої кількості дописів до найменшої. "
                    + "Реалізовано JPQL-запитом, оголошеним через @NamedQuery «Topic.findPopular» на сутності Topic.")
    @ApiResponse(responseCode = "200", description = "Список популярних тем повернуто")
    @ApiResponse(responseCode = "400", description = "minPosts менше 0",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<PopularTopicResponse> popular(
            @Parameter(description = "Мінімальна кількість дописів у темі", example = "3")
            @RequestParam(defaultValue = "3") @Min(value = 0, message = "minPosts не може бути від'ємним") int minPosts) {
        return topicService.findPopular(minPosts);
    }

    @GetMapping("/latest")
    @Operation(
            summary = "П'ять найновіших тем",
            description = "Повертає п'ять останніх створених тем, від найновішої. Запит згенеровано Spring Data "
                    + "за назвою методу findTop5ByOrderByCreationDateDesc.")
    @ApiResponse(responseCode = "200", description = "Список тем повернуто")
    public List<TopicResponse> latest() {
        return topicService.findLatest();
    }

    @GetMapping("/by-author/{author}")
    @Operation(
            summary = "Теми автора",
            description = "Повертає всі теми вказаного автора (без урахування регістру), від найновішої. Запит згенеровано "
                    + "Spring Data за назвою методу findByAuthorIgnoreCaseOrderByCreationDateDesc. Якщо тем немає, "
                    + "повертається порожній список.")
    @ApiResponse(responseCode = "200", description = "Список тем повернуто")
    public List<TopicResponse> byAuthor(@Parameter(description = "Ім'я автора", example = "Аня") @PathVariable String author) {
        return topicService.findByAuthor(author);
    }

    @GetMapping("/stats/categories")
    @Operation(
            summary = "Кількість тем за категоріями",
            description = "Повертає кількість тем у кожній категорії, де є хоча б одна тема. Реалізовано JPQL-запитом "
                    + "з GROUP BY та конструктором DTO (SELECT new ...), оголошеним через @Query.")
    @ApiResponse(responseCode = "200", description = "Статистику повернуто")
    public List<CategoryStats> categoryStats() {
        return topicService.categoryStats();
    }
}
