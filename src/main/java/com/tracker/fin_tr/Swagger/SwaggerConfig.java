package com.tracker.fin_tr.Swagger;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fin-TR API")
                        .version("1.0")
                        .description("API для управления финансовыми транзакциями")
                        .contact(new Contact()
                                .name("Alan")
                                .email("egormuhooe@gmail.com")))
                .externalDocs(new ExternalDocumentation()  // Ссылка на внешнюю документацию
                        .description("Полная документация")
                        .url("https://github.com/alany89/User"))
                .servers(List.of(  // Список серверов (dev, prod)
                        new Server().url("http://localhost:8080").description("Dev Server"),
                        new Server().url("https://api.fin-tr.com").description("Prod Server")));
    }
}
