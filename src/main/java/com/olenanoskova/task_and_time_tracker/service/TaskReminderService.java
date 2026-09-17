package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;

import java.util.List;
import java.util.UUID;

public interface TaskReminderService {

    TaskReminder addReminder(UUID taskId, TaskReminder reminder);

    List<TaskReminder> getReminders(UUID taskId);

    TaskReminder getReminderById(UUID id);

    TaskReminder updateReminder(UUID id, TaskReminder reminder);

    void deleteReminder(UUID id);
}
