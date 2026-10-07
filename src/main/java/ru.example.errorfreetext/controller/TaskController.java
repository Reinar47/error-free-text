package ru.example.errorfreetext.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.ErrorResponse;
import ru.example.errorfreetext.dto.response.TaskCreatedResponseDto;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;
import ru.example.errorfreetext.service.TaskService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/error-free-text/tasks")
@Tag(name = "Управление задачами", description = "API для корректировки текста через Yandex Speller")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать задачу на корректировку текста",
            description = "Принимает текст и язык, сохраняет задачу со статусом NEW и возвращает её идентификатор")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskCreatedResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TaskCreatedResponseDto createTask(@Valid @RequestBody RequestForTask request) {
        log.info("Получен запрос на создание задачи. Язык: {}", request.getLanguage());

        return new TaskCreatedResponseDto(taskService.createTask(request));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Получить статус задачи",
            description = "Возвращает статус задачи и, если она завершена, скорректированный текст")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно получен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskStatusResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TaskStatusResponseDto getTaskStatus(
            @Parameter(description = "Идентификатор задачи", required = true,
                    schema = @Schema(type = "string", format = "uuid",
                            example = "44bd78dc-d08c-41c6-b87d-fb82046bd470"))
            @PathVariable @NotNull(message = "{validation.taskIdNotNull.message}") UUID taskId) {

        log.info("Получен запрос на получение статуса задачи с ID: {}", taskId);

        return taskService.getTaskStatus(taskId);
    }
}