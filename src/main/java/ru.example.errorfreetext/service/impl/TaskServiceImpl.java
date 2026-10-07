package ru.example.errorfreetext.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.errorfreetext.Utils.FindAndCheckService;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;
import ru.example.errorfreetext.entity.TaskEntity;
import ru.example.errorfreetext.mapper.TaskMapper;
import ru.example.errorfreetext.repository.TaskRepository;
import ru.example.errorfreetext.service.TaskService;

import java.util.UUID;

import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.taskCreatedMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final FindAndCheckService checkEntity;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UUID createTask(RequestForTask request) {
        log.info("Создание задачи на корректировку текста. Язык: {}", request.getLanguage());

        TaskEntity newTask = taskMapper.toNewTask(request);

        TaskEntity savedTask = taskRepository.save(newTask);

        log.info("{}{}", taskCreatedMessage, savedTask.getId());
        return savedTask.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TaskStatusResponseDto getTaskStatus(UUID taskId) {
        log.info("Запрос статуса задачи с ID: {}", taskId);

        TaskEntity task = checkEntity.findAndCheckTask(taskId);

        log.info("Статус задачи с ID: {} — {}", taskId, task.getStatus());
        return taskMapper.toTaskStatusResponseDto(task);
    }
}
