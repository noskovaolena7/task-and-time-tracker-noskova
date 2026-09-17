package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    Comment createComment(UUID taskId, Comment comment);

    List<Comment> getComments(UUID taskId);

    Comment getCommentById(UUID id);

    Comment updateComment(UUID id, Comment comment);

    void deleteComment(UUID id);
}
