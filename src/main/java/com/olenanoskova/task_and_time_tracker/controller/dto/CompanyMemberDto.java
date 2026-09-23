package com.olenanoskova.task_and_time_tracker.controller.dto;

import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import lombok.Data;

import java.util.UUID;

@Data
public class CompanyMemberDto {

    /** Role row id (for promotion/removal). */
    private UUID id;

    private UUID userId;

    private String firstName;

    private String lastName;

    private String email;

    private MemberRole role;
}
