package ru.example.errorfreetext.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.example.errorfreetext.entity.TaskEntity;
import ru.example.errorfreetext.exception.TaskNotFoundException;
import ru.example.errorfreetext.repository.TaskRepository;

import java.util.UUID;

import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.taskNotFoundWithIdMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindAndCheckService {

    private final TaskRepository taskRepository;

    /**
     * Находит и проверяет существование задачи по идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return {@link TaskEntity} найденная задача
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена
     */
    public TaskEntity findAndCheckTask(UUID taskId) {
        log.info("Поиск задачи в репозитории по ID: {}", taskId);

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskNotFoundWithIdMessage + taskId + " not found"));

        log.info("Поиск задачи в репозитории по ID: {} завершён, найдена задача со статусом: {}",
                taskId, task.getStatus());
        return task;
    }
}
