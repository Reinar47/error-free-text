package ru.example.errorfreetext.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.example.errorfreetext.Utils.EntityGeneratorTest;
import ru.example.errorfreetext.Utils.FindAndCheckService;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;
import ru.example.errorfreetext.entity.TaskEntity;
import ru.example.errorfreetext.exception.TaskNotFoundException;
import ru.example.errorfreetext.mapper.TaskMapper;
import ru.example.errorfreetext.repository.TaskRepository;
import ru.example.errorfreetext.service.impl.TaskServiceImpl;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private FindAndCheckService checkEntity;
    @InjectMocks
    private TaskServiceImpl taskService;

    private RequestForTask request;
    private TaskEntity mapped;
    private UUID id;
    private TaskEntity saved;
    private TaskEntity task;

    @BeforeEach
    void setup() {
        EntityGeneratorTest entityGeneratorTest = new EntityGeneratorTest();
        request = entityGeneratorTest.getRequest();
        mapped = entityGeneratorTest.getMapped();
        id = entityGeneratorTest.getId();
        saved = entityGeneratorTest.getSaved();
        task = entityGeneratorTest.getTask();
    }

    @Test
    void createsTaskAndReturnsId() {
        when(taskMapper.toNewTask(request)).thenReturn(mapped);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(saved);

        UUID actualId = taskService.createTask(request);

        assertThat(actualId).isEqualTo(id);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void returnsTaskStatus() {
        TaskStatusResponseDto expected = new TaskStatusResponseDto(TaskStatus.DONE, "Привет мир", null);

        when(checkEntity.findAndCheckTask(id)).thenReturn(task);
        when(taskMapper.toTaskStatusResponseDto(task)).thenReturn(expected);

        TaskStatusResponseDto actual = taskService.getTaskStatus(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void throwsWhenTaskNotFound() {
        when(checkEntity.findAndCheckTask(id))
                .thenThrow(new TaskNotFoundException("Task with id: " + id + " not found"));

        assertThatThrownBy(() -> taskService.getTaskStatus(this.id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
