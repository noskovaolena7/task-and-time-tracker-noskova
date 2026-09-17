package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(UUID id) {
        super("Attachment not found: " + id);
    }
}

