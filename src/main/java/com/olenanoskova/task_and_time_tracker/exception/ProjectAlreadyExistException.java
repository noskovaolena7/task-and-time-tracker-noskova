package com.olenanoskova.task_and_time_tracker.exception;

public class ProjectAlreadyExistException extends RuntimeException {
    public ProjectAlreadyExistException(String name) {
        super("Project already exists with name: " + name);
    }
}

