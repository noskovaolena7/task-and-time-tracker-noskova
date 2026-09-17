package com.olenanoskova.task_and_time_tracker.exception;

import com.olenanoskova.task_and_time_tracker.controller.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(BadRequestException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFound(UserNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleUserAlreadyExist(UserAlreadyExistException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<ErrorDto> error(HttpStatus status, String message) {
        ErrorDto dto = new ErrorDto();
        dto.setMessage(message);
        dto.setCode(status.name());
        return ResponseEntity.status(status).body(dto);
    }
}