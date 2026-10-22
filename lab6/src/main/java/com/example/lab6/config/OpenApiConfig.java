package com.example.lab6.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Forum REST API",
        version = "2.0",
        description = "RESTful вебсервіс форуму на Spring Data JPA (PostgreSQL): теми та дописи. Демонструє запити JPQL через @Query та @NamedQuery, методи пошуку, згенеровані за назвою, і транзакційні операції (пакетне створення тем, об'єднання тем). Підтримує CRUD-операції, фільтрацію і пагінацію, "
                + "часткове оновлення за RFC 7386 (JSON Merge Patch) та RFC 6902 (JSON Patch). "
                + "Помилки повертаються у форматі Problem Details (RFC 9457)."))
public class OpenApiConfig {
}
