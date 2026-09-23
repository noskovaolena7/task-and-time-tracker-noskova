package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.AttachmentNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import com.olenanoskova.task_and_time_tracker.repository.AttachmentRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.AttachmentEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Attachment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository repo;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttachmentMapper mapper;

    @InjectMocks
    private AttachmentServiceImpl service;

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(AttachmentNotFoundException.class, () -> service.getAttachmentById(id));
    }

    @Test
    void getById_found() {
        UUID id = UUID.randomUUID();
        AttachmentEntity entity = new AttachmentEntity();
        Attachment domain = new Attachment();
        domain.setId(id);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Attachment res = service.getAttachmentById(id);
        assertEquals(id, res.getId());
    }

    @Test
    void uploadAttachment_taskNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        Attachment att = new Attachment();
        assertThrows(TaskNotFoundException.class, () -> service.uploadAttachment(taskId, att));
    }

    @Test
    void uploadAttachment_success() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);

        Attachment att = new Attachment();
        att.setFileName("file.png");

        AttachmentEntity entity = new AttachmentEntity();
        AttachmentEntity saved = new AttachmentEntity();
        Attachment domain = new Attachment();
        domain.setId(UUID.randomUUID());

        when(mapper.toEntity(att)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(domain);

        Attachment res = service.uploadAttachment(taskId, att);
        assertNotNull(res.getId());
    }

    @Test
    void getAttachmentsForTask_returnsList() {
        UUID taskId = UUID.randomUUID();
        AttachmentEntity entity = new AttachmentEntity();
        Attachment domain = new Attachment();

        when(repo.findByTaskId(taskId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Attachment> list = service.getAttachmentsForTask(taskId);
        assertEquals(1, list.size());
    }

    @Test
    void deleteAttachment_notFound_throws() {
        UUID taskId = UUID.randomUUID();
        UUID attId = UUID.randomUUID();
        when(repo.findById(attId)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () -> service.deleteAttachment(taskId, attId));
    }

    @Test
    void deleteAttachment_success() {
        UUID taskId = UUID.randomUUID();
        UUID attId = UUID.randomUUID();
        AttachmentEntity entity = new AttachmentEntity();
        entity.setId(attId);
        entity.setTaskId(taskId);
        when(repo.findById(attId)).thenReturn(Optional.of(entity));

        service.deleteAttachment(taskId, attId);
        verify(repo).deleteById(attId);
    }
}
