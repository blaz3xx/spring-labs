package com.example.lab5.controllers;

import com.example.lab5.dto.PageResponse;
import com.example.lab5.dto.TopicRequest;
import com.example.lab5.dto.TopicResponse;
import com.example.lab5.models.TopicCategory;
import com.example.lab5.services.TopicService;
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
}
