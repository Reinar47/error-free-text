package ru.example.errorfreetext.Utils;

import lombok.Getter;
import ru.example.errorfreetext.constant.Language;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.entity.TaskEntity;

import java.util.UUID;

@Getter
public class EntityGenerator {

    UUID id = UUID.randomUUID();

    RequestForTask request = RequestForTask.builder()
            .language(Language.RU)
            .text("Превет мир")
            .build();

    TaskEntity mapped = TaskEntity.builder()
            .originalText("Превет мир")
            .correctedText("dummyText")
            .status(TaskStatus.NEW)
            .language(Language.RU)
            .build();

    TaskEntity saved = TaskEntity.builder()
            .id(id)
            .originalText("Превет мир")
            .language(Language.RU)
            .status(TaskStatus.NEW)
            .build();

    TaskEntity task = TaskEntity.builder()
            .id(id)
            .status(TaskStatus.DONE)
            .correctedText("Привет мир")
            .build();
}

