package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentCreateRequestDto {

    private String fileName;
    private String fileUrl;
    private UUID uploadedBy;

}
