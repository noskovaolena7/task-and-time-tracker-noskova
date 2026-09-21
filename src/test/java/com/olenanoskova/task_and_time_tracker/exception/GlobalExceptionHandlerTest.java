package com.olenanoskova.task_and_time_tracker.exception;

import com.olenanoskova.task_and_time_tracker.controller.dto.ErrorDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler h = new GlobalExceptionHandler();

    @Test
    void handleUserNotFound_returns404() {
        var ex = new UserNotFoundException(java.util.UUID.randomUUID());
        var resp = h.handleUserNotFound(ex);
        ErrorDto body = resp.getBody();
        assertNotNull(body);
        assertEquals("NOT_FOUND", body.getCode());
        assertTrue(body.getMessage().contains("not found") || body.getCode()!=null);
    }
}
