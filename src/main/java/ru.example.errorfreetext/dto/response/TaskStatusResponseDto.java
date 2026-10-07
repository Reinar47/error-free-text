package ru.example.errorfreetext.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.example.errorfreetext.constant.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO ответа со статусом и результатом задачи")
public class TaskStatusResponseDto {

    @Schema(description = "Статус задачи",
            example = "DONE"
    )
    private TaskStatus status;

    @Schema(description = "Скорректированный текст (заполнен только при статусе DONE)",
            example = "Привет мир 123 https://ya.ru")
    private String correctedText;

    @Schema(description = "Описание ошибки (заполнено только при статусе ERROR)",
            example = "Yandex Speller request failed")
    private String errorMessage;
}
