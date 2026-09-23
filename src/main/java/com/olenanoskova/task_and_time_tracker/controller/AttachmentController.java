package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import com.olenanoskova.task_and_time_tracker.security.CustomUserDetails;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.AttachmentService;
import com.olenanoskova.task_and_time_tracker.service.model.Attachment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;
    private final SecurityService securityService;


    @GetMapping
    @PreAuthorize("@securityService.canViewAttachmentsOfTask(#taskId)")
    public ResponseEntity<List<AttachmentResponseDto>> getAllAttachments(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal CustomUserDetails user ) {
        List<Attachment> attachments = attachmentService.getAttachmentsForTask(taskId);
        List<AttachmentResponseDto> responseList = attachments.stream()
                .map(attachmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("@securityService.canCreateAttachment(#taskId)")
    public ResponseEntity<AttachmentResponseDto> createAttachment(
            @PathVariable UUID taskId,
            @Valid @RequestBody AttachmentCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user) {

        UUID projectId = attachmentService.getProjectIdByTaskId(taskId);
        Attachment attachment = attachmentMapper.toDomain(projectId, taskId, requestDto, securityService.getCurrentUserId());

        Attachment createdAttachment = attachmentService.uploadAttachment(taskId, attachment);
        AttachmentResponseDto responseDto = attachmentMapper.toDto(createdAttachment);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    @PutMapping("/{attachmentId}")
    @PreAuthorize("@securityService.canManageAttachmentById(#attachmentId)")
    public ResponseEntity<AttachmentResponseDto> updateAttachment(
            @PathVariable UUID taskId,
            @PathVariable UUID attachmentId,
            @Valid @RequestBody AttachmentCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user   // added
    ) {
        UUID projectId = attachmentService.getProjectIdByTaskId(taskId);
        Attachment updated = attachmentService.updateAttachment(
                attachmentId,
                attachmentMapper.toDomain(projectId, taskId, requestDto, securityService.getCurrentUserId())
        );
        return ResponseEntity.ok(attachmentMapper.toDto(updated));
    }

    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("@securityService.canManageAttachmentById(#attachmentId)")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID taskId,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal CustomUserDetails user   // added
    ) {

        attachmentService.deleteAttachment(taskId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}
