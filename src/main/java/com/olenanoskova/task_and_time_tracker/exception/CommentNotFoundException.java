package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(UUID id) {
        super("Comment not found: " + id);
    }
}
