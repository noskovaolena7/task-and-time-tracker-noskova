package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.CommentNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.CommentMapper;
import com.olenanoskova.task_and_time_tracker.repository.CommentRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.CommentEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Comment;
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
class CommentServiceTest {

    @Mock
    private CommentRepository repo;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentServiceImpl service;

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> service.getCommentById(id));
    }

    @Test
    void getById_found() {
        UUID id = UUID.randomUUID();
        CommentEntity entity = new CommentEntity();
        Comment domain = new Comment();
        domain.setId(id);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Comment res = service.getCommentById(id);
        assertEquals(id, res.getId());
    }

    @Test
    void createComment_taskNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        Comment comment = new Comment();
        assertThrows(TaskNotFoundException.class, () -> service.createComment(taskId, comment));
    }

    @Test
    void createComment_userNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(false);

        Comment comment = new Comment();
        comment.setUserId(userId);

        assertThrows(UserNotFoundException.class, () -> service.createComment(taskId, comment));
    }

    @Test
    void createComment_success() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setText("Hello");

        CommentEntity entity = new CommentEntity();
        CommentEntity saved = new CommentEntity();
        Comment domain = new Comment();
        domain.setId(UUID.randomUUID());

        when(mapper.toEntity(comment)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(domain);

        Comment res = service.createComment(taskId, comment);
        assertNotNull(res.getId());
    }

    @Test
    void getComments_returnsList() {
        UUID taskId = UUID.randomUUID();
        CommentEntity entity = new CommentEntity();
        Comment domain = new Comment();

        when(repo.findByTaskId(taskId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Comment> list = service.getComments(taskId);
        assertEquals(1, list.size());
    }

    @Test
    void deleteComment_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> service.deleteComment(id));
    }

    @Test
    void deleteComment_success() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);

        service.deleteComment(id);
        verify(repo).deleteById(id);
    }
}
