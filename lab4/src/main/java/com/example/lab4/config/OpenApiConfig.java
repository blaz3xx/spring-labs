package com.example.lab4.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Forum REST API",
        version = "1.0",
        description = "RESTful вебсервіс форуму: теми та дописи. Підтримує CRUD-операції, фільтрацію і пагінацію, "
                + "часткове оновлення за RFC 7386 (JSON Merge Patch) та RFC 6902 (JSON Patch). "
                + "Помилки повертаються у форматі Problem Details (RFC 9457)."))
public class OpenApiConfig {
}
