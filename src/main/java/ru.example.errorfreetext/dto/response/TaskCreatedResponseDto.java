package ru.example.errorfreetext.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO ответа с идентификатором созданной задачи")
public class TaskCreatedResponseDto {

    @Schema(description = "Идентификатор созданной задачи",
            example = "44bd78dc-d08c-41c6-b87d-fb82046bd470"
    )
    private UUID id;

}
