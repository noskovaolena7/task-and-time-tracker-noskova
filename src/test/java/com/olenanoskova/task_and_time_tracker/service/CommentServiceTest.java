package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.CommentRepository;
import com.olenanoskova.task_and_time_tracker.mapper.CommentMapper;
import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;import java.util.UUID;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock CommentRepository repo; @Mock CommentMapper mapper; @InjectMocks CommentServiceImpl service;
    @Test void getById_notFound_throws(){ UUID id=UUID.randomUUID(); when(repo.findById(id)).thenReturn(Optional.empty()); assertThrows(RuntimeException.class,()->service.getCommentById(id)); }
}
