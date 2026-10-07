package ru.example.errorfreetext.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.errorfreetext.constant.Language;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания задачи на корректировку текста")
public class RequestForTask {

    @Schema(description = "Язык текста (RU или EN)",
            example = "RU",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.languageNotNull.message}")
    private Language language;

    @Schema(description = "Текст, подлежащий корректировке (минимум 3 символа, не только цифры и спецсимволы)",
            example = "Превет мир 123 https://ya.ru",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.textNotBlank.message}")
    @Pattern(
            regexp = "^(?=.{3,})(?=.*\\p{L}).*$",
            message = "{validation.textPattern.message}"
    )
    private String text;
}
