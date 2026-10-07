package ru.example.errorfreetext.service;

import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;

import java.util.UUID;

public interface TaskService {

    /**
     * Создаёт задачу на корректировку текста.
     *
     * @param request данные для создания задачи
     * @return идентификатор созданной задачи
     */
    UUID createTask(RequestForTask request);

    /**
     * Возвращает статус и результат задачи по её идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return статус и результат
     */
    TaskStatusResponseDto getTaskStatus(UUID taskId);
}
