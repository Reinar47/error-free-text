package ru.example.errorfreetext.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.TaskCreatedResponseDto;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;
import ru.example.errorfreetext.entity.TaskEntity;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originalText", source = "text")
    @Mapping(target = "correctedText", constant = "Идет проверка текста")
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "errorMessage", constant = "Ошибок нет")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskEntity toNewTask(RequestForTask request);


    TaskCreatedResponseDto toTaskCreatedResponseDto(TaskEntity task);


//    TaskStatusResponseDto toTaskStatusResponseDto(TaskEntity task);


    @Mapping(target = "correctedText", source = "task", qualifiedByName = "resolveCorrectedText")
    TaskStatusResponseDto toTaskStatusResponseDto(TaskEntity task);

    @Named("resolveCorrectedText")
    default String resolveCorrectedText(TaskEntity task) {
        if (task.getStatus() == TaskStatus.ERROR) {
            return "Исправлений нет";
        }
        return task.getCorrectedText();
    }
}
