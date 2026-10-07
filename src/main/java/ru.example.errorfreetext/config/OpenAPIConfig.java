package ru.example.errorfreetext.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String TASK_MANAGEMENT = "Управление задачами корректировки";
    private static final String TASK_PATH_TO_MATCH = "/error-free-text/tasks/**";
    private static final String TASK_DESCRIPTION = "API для создания задач корректировки текста и получения результата";

    @Value("${spring.application.name}")
    private String appName;

    @Value("${info.app.description}")
    private String appDescription;

    @Value("${info.app.version}")
    private String appVersion;

    @Value("${info.app.summary}")
    private String appSummary;

    @Bean
    public GroupedOpenApi taskApi() {
        return GroupedOpenApi.builder()
                .group(TASK_MANAGEMENT)
                .pathsToMatch(TASK_PATH_TO_MATCH)
                .addOpenApiCustomizer(openApi -> openApi.addTagsItem(new Tag()
                        .name(TASK_MANAGEMENT)
                        .description(TASK_DESCRIPTION)))
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info()
                .title(appName)
                .description(appDescription)
                .version(appVersion)
                .summary(appSummary));
    }
}