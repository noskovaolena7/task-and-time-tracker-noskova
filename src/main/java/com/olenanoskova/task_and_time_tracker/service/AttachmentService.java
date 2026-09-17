package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {

    Attachment uploadAttachment(UUID taskId, Attachment attachment);

    List<Attachment> getAttachmentsForTask(UUID taskId);

    Attachment getAttachmentById(UUID id);

    Attachment updateAttachment(UUID id, Attachment attachment);

    void deleteAttachment(UUID id);
}
