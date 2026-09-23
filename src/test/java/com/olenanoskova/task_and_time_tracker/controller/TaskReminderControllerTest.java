package com.olenanoskova.task_and_time_tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskReminderControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.TaskReminderService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.TaskReminderMapper mapper;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.TaskReminderController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void deleteReminder_returns204_onSuccess() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID reminderId = UUID.randomUUID();
        var reminder = new com.olenanoskova.task_and_time_tracker.service.model.TaskReminder();
        reminder.setId(reminderId);
        reminder.setTaskId(taskId);

        when(service.getReminderById(reminderId)).thenReturn(reminder);

        mvc.perform(delete("/tasks/{taskId}/reminders/{reminderId}", taskId.toString(), reminderId.toString()))
                .andExpect(status().isNoContent());
        verify(service).deleteReminder(reminderId);
    }

    @Test
    void deleteReminder_returns404_whenTaskMismatch() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID reminderId = UUID.randomUUID();
        var reminder = new com.olenanoskova.task_and_time_tracker.service.model.TaskReminder();
        reminder.setId(reminderId);
        reminder.setTaskId(UUID.randomUUID());

        when(service.getReminderById(reminderId)).thenReturn(reminder);

        mvc.perform(delete("/tasks/{taskId}/reminders/{reminderId}", taskId.toString(), reminderId.toString()))
                .andExpect(status().is4xxClientError());
    }
}
