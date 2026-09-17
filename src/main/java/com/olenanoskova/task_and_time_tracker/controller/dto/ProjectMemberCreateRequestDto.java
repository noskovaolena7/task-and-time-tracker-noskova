package com.olenanoskova.task_and_time_tracker.controller.dto;

import java.util.UUID;

public class ProjectMemberCreateRequestDto {

     private UUID userId;
     private MemberRoleDto memberRoleDto;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MemberRoleDto getMemberRoleDto() {
        return memberRoleDto;
    }

    public void setMemberRoleDto(MemberRoleDto memberRoleDto) {
        this.memberRoleDto = memberRoleDto;
    }
}
