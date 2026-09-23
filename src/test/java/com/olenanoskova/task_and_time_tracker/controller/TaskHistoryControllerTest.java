package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskHistoryResponseDto;
import com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler;
import com.olenanoskova.task_and_time_tracker.mapper.TaskHistoryMapper;
import com.olenanoskova.task_and_time_tracker.service.TaskHistoryService;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskHistoryControllerTest {

    private MockMvc mvc;

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private TaskHistoryMapper taskHistoryMapper;

    @InjectMocks
    private TaskHistoryController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTaskHistory_returns200_withList() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        history.setTaskId(taskId);
        history.setFieldName("status");
        history.setOldValue("OPEN");
        history.setNewValue("IN_PROGRESS");
        history.setChangedAt(Instant.now());

        TaskHistoryResponseDto dto = new TaskHistoryResponseDto();
        dto.setId(history.getId());
        dto.setFieldChanged("status");
        dto.setOldValue("OPEN");
        dto.setNewValue("IN_PROGRESS");

        when(taskHistoryService.getTaskHistory(taskId)).thenReturn(List.of(history));
        when(taskHistoryMapper.toDtoList(List.of(history))).thenReturn(List.of(dto));

        mvc.perform(get("/tasks/{taskId}/history", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fieldChanged").value("status"))
                .andExpect(jsonPath("$[0].oldValue").value("OPEN"))
                .andExpect(jsonPath("$[0].newValue").value("IN_PROGRESS"));
    }
}
