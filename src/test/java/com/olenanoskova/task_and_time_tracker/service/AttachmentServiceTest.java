package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.AttachmentRepository;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock AttachmentRepository repo;
    @Mock AttachmentMapper mapper;
    @InjectMocks AttachmentServiceImpl service;

    @Test
    void getById_notFound_throws(){
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(RuntimeException.class, ()-> service.getAttachmentById(id));
    }
}
