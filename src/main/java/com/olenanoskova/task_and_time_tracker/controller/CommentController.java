package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.CommentCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CommentResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.CommentMapper;
import com.olenanoskova.task_and_time_tracker.service.CommentService;
import com.olenanoskova.task_and_time_tracker.service.model.Comment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getAll(@PathVariable UUID taskId) {
        List<Comment> comments = commentService.getComments(taskId);
        List<CommentResponseDto> responseList = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentCreateRequestDto request) {

        Comment comment = commentMapper.toDomain(request);
        Comment createdComment = commentService.createComment(taskId, comment);
        CommentResponseDto response = commentMapper.toDto(createdComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
