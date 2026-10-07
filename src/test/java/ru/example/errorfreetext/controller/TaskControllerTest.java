package ru.example.errorfreetext.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.errorfreetext.constant.Language;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.dto.request.RequestForTask;
import ru.example.errorfreetext.dto.response.TaskStatusResponseDto;
import ru.example.errorfreetext.exception.TaskNotFoundException;
import ru.example.errorfreetext.service.TaskService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void createTaskReturns201AndId() throws Exception {
        UUID expectedId = UUID.randomUUID();
        when(taskService.createTask(any(RequestForTask.class))).thenReturn(expectedId);

        RequestForTask request = RequestForTask.builder()
                .language(Language.RU)
                .text("Превет мир")
                .build();

        mockMvc.perform(post("/error-free-text/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedId.toString()));
    }

    @Test
    void createTaskReturns400ForInvalidText() throws Exception {
        RequestForTask request = RequestForTask.builder()
                .language(Language.RU)
                .text("12")
                .build();

        mockMvc.perform(post("/error-free-text/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(40001));
    }

    @Test
    void getTaskStatusReturnsTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskStatusResponseDto response = new TaskStatusResponseDto(TaskStatus.DONE, "Привет мир", null);

        when(taskService.getTaskStatus(taskId)).thenReturn(response);

        mockMvc.perform(get("/error-free-text/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.correctedText").value("Привет мир"));
    }

    @Test
    void getTaskStatusReturns404WhenNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.getTaskStatus(taskId))
                .thenThrow(new TaskNotFoundException("Task with id: " + taskId + " not found"));

        mockMvc.perform(get("/error-free-text/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401))
                .andExpect(jsonPath("$.errorMessage").value("Task with id: " + taskId + " not found"));
    }
}
