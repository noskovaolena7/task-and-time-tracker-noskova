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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public Comment createComment(UUID taskId, Comment comment) {

        log.info("Attempting to create comment for task {}", taskId);

        // Check task exists
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        // Check user exists
        if (!userRepository.existsById(comment.getUserId())) {
            throw new UserNotFoundException(comment.getUserId());
        }

        comment.setTaskId(taskId);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        CommentEntity entity = commentMapper.toEntity(comment);
        CommentEntity saved = commentRepository.save(entity);

        log.info("Successfully created comment for task {}", taskId);

        return commentMapper.toDomain(saved);
    }

    @Override
    public List<Comment> getComments(UUID taskId) {

        log.info("Fetching comments for task {}", taskId);

        List<CommentEntity> entities = commentRepository.findByTaskId(taskId);

        return entities.stream()
                .map(commentMapper::toDomain)
                .toList();
    }

    @Override
    public Comment getCommentById(UUID id) {

        log.info("Fetching comment with id {}", id);

        CommentEntity entity = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        return commentMapper.toDomain(entity);
    }

    @Override
    public Comment updateComment(UUID id, Comment comment) {

        log.info("Attempting to update comment with id {}", id);

        Optional<CommentEntity> optionalComment = commentRepository.findById(id);

        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException(id);
        }

        CommentEntity entity = optionalComment.get();

        entity.setText(comment.getText());
        entity.setUpdatedAt(Instant.now());

        CommentEntity saved = commentRepository.save(entity);

        log.info("Successfully updated comment with id {}", id);

        return commentMapper.toDomain(saved);
    }

    @Override
    public void deleteComment(UUID id) {

        log.info("Attempting to delete comment with id {}", id);

        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException(id);
        }

        commentRepository.deleteById(id);

        log.info("Successfully deleted comment with id {}", id);
    }
}
