package com.olenanoskova.task_and_time_tracker.exception;

public class CompanyAlreadyExistException extends RuntimeException {

    public CompanyAlreadyExistException(String name) {
        super("Company with name '" + name + "' already exists.");
    }
}
