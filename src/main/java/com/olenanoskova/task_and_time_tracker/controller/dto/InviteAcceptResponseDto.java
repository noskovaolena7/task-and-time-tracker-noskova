package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InviteAcceptResponseDto {

    private UUID companyId;

    public InviteAcceptResponseDto(UUID companyId) {
        this.companyId = companyId;
    }
}
