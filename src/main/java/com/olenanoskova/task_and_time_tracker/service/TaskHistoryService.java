package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;

import java.util.List;
import java.util.UUID;

public interface TaskHistoryService {

    List<TaskHistory> getTaskHistory(UUID taskId);

    TaskHistory getHistoryRecordById(UUID id);
}
