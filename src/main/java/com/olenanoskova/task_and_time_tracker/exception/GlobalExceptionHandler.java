package com.olenanoskova.task_and_time_tracker.exception;

import com.olenanoskova.task_and_time_tracker.controller.dto.ErrorDto;
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

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCommentNotFound(CommentNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCompanyNotFound(CompanyNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CompanyAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleCompanyAlreadyExist(CompanyAlreadyExistException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProjectNotFound(ProjectNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProjectAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleProjectAlreadyExist(ProjectAlreadyExistException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ProjectDeadlineNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProjectDeadlineNotFound(ProjectDeadlineNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProjectMemberNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProjectMemberNotFound(ProjectMemberNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProjectMemberAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleProjectMemberAlreadyExists(ProjectMemberAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTaskNotFound(TaskNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TaskHistoryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTaskHistoryNotFound(TaskHistoryNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TaskReminderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTaskReminderNotFound(TaskReminderNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TimeEntryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTimeEntryNotFound(TimeEntryNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotificationNotFound(NotificationNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<ErrorDto> handleAttachmentNotFound(AttachmentNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserCompanyRoleNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserCompanyRoleNotFound(UserCompanyRoleNotFoundException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoleAlreadyAssignedException.class)
    public ResponseEntity<ErrorDto> handleRoleAlreadyAssigned(RoleAlreadyAssignedException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidDeadlineException.class)
    public ResponseEntity<ErrorDto> handleInvalidDeadline(InvalidDeadlineException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidReminderException.class)
    public ResponseEntity<ErrorDto> handleInvalidReminder(InvalidReminderException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<ErrorDto> handleInvalidTaskStatus(InvalidTaskStatusException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTimeEntryException.class)
    public ResponseEntity<ErrorDto> handleInvalidTimeEntry(InvalidTimeEntryException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccountIsBlockedException.class)
    public ResponseEntity<ErrorDto> handleAccountIsBlocked(AccountIsBlockedException ex) {
        log.error(ex.getMessage());
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
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