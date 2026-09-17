package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserCompanyRoleCreateRequestDto {

    private UUID userId;
    private RoleDto role;

}
