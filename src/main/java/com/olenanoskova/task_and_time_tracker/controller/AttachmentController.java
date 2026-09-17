package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import com.olenanoskova.task_and_time_tracker.service.AttachmentService;
import com.olenanoskova.task_and_time_tracker.service.model.Attachment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;

    @GetMapping
    public ResponseEntity<List<AttachmentResponseDto>> getAllAttachments(@PathVariable UUID taskId) {
        List<Attachment> attachments = attachmentService.getAttachmentsForTask(taskId);
        List<AttachmentResponseDto> responseList = attachments.stream()
                .map(attachmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<AttachmentResponseDto> createAttachment(
            @PathVariable UUID taskId,
            @Valid @RequestBody AttachmentCreateRequestDto requestDto) {

        Attachment attachment = attachmentMapper.toDomain(requestDto);
        Attachment createdAttachment = attachmentService.uploadAttachment(taskId, attachment);
        AttachmentResponseDto responseDto = attachmentMapper.toDto(createdAttachment);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
