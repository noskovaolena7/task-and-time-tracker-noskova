package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.AttachmentNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import com.olenanoskova.task_and_time_tracker.repository.AttachmentRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.AttachmentEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public Attachment uploadAttachment(UUID taskId, Attachment attachment) {

        log.info("Attempting to upload attachment {} for task {}", attachment.getFileName(), taskId);

        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        if (attachment.getUploadedBy() != null &&
                !userRepository.existsById(attachment.getUploadedBy())) {
            throw new UserNotFoundException(attachment.getUploadedBy());
        }

        attachment.setTaskId(taskId);
        attachment.setUploadedAt(Instant.now());
        attachment.setUpdatedAt(Instant.now());

        AttachmentEntity entity = attachmentMapper.toEntity(attachment);
        AttachmentEntity saved = attachmentRepository.save(entity);

        log.info("Successfully uploaded attachment {} for task {}", attachment.getFileName(), taskId);

        return attachmentMapper.toDomain(saved);
    }

    @Override
    public List<Attachment> getAttachmentsForTask(UUID taskId) {

        log.info("Fetching attachments for task {}", taskId);

        List<AttachmentEntity> entities = attachmentRepository.findByTaskId(taskId);

        return entities.stream()
                .map(attachmentMapper::toDomain)
                .toList();
    }

    @Override
    public Attachment getAttachmentById(UUID id) {

        log.info("Fetching attachment with id {}", id);

        AttachmentEntity entity = attachmentRepository.findById(id)
                .orElseThrow(() -> new AttachmentNotFoundException(id));

        return attachmentMapper.toDomain(entity);
    }

    @Override
    public UUID getProjectIdByTaskId(UUID taskId) {
        return taskRepository.findById(taskId)
                .map(TaskEntity::getProjectId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    public Attachment updateAttachment(UUID attachmentId, Attachment attachment) {

        log.info("Attempting to update attachment {}", attachmentId);

        AttachmentEntity entity = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));

        if (attachment.getFileName() != null) {
            entity.setFileName(attachment.getFileName());
        }
        if (attachment.getFileUrl() != null) {
            entity.setFileUrl(attachment.getFileUrl());
        }
        if (attachment.getUploadedBy() != null) {
            entity.setUploadedBy(attachment.getUploadedBy());
        }
        entity.setUpdatedAt(Instant.now());

        AttachmentEntity saved = attachmentRepository.save(entity);

        log.info("Successfully updated attachment {}", attachmentId);

        return attachmentMapper.toDomain(saved);
    }

    @Override
    public void deleteAttachment(UUID taskId, UUID attachmentId) {

        log.info("Attempting to delete attachment {}", attachmentId);

        if (!attachmentRepository.existsById(attachmentId)) {
            throw new AttachmentNotFoundException(attachmentId);
        }

        attachmentRepository.deleteById(attachmentId);

        log.info("Successfully deleted attachment {}", attachmentId);
    }
}
