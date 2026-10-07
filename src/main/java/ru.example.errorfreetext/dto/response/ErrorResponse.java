package ru.example.errorfreetext.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO ответа с информацией об ошибке")
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибке",
            example = "Task with id: 44bd78dc-d08c-41c6-b87d-fb82046bd470 not found")
    private String errorMessage;

    @Schema(description = "Код ошибки", example = "40401")
    private int errorCode;

    @Schema(description = "Время возникновения ошибки", example = "2026-10-06T18:31:42.201")
    private LocalDateTime timestamp;

    @Schema(description = "Путь запроса", example = "/error-free-text/tasks/44bd78dc-d08c-41c6-b87d-fb82046bd470")
    private String path;
}
