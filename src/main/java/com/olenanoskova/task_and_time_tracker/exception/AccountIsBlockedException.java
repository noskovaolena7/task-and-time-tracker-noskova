package com.olenanoskova.task_and_time_tracker.exception;

public class AccountIsBlockedException extends RuntimeException {
    public AccountIsBlockedException() {
        super("Your account has been blocked.");
    }
}
