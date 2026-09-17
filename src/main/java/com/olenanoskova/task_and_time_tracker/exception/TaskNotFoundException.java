package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(UUID id) {
    super("Task not found: " + id);
  }
}

