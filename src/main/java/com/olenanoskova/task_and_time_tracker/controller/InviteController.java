package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.InviteAcceptResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.InviteCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.InviteResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.InviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invites")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("@securityService.canManageInvites(#request.companyId)")
    public ResponseEntity<InviteResponseDto> createInvite(
            @Valid @RequestBody InviteCreateRequestDto request) {

        InviteEntity invite = inviteService.generateInvite(request.getCompanyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(invite));
    }

    @PostMapping("/{code}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InviteAcceptResponseDto> acceptInvite(@PathVariable String code) {

        UUID userId = securityService.getCurrentUserId();
        UUID companyId = inviteService.joinCompany(code, userId);
        return ResponseEntity.ok(new InviteAcceptResponseDto(companyId));
    }

    private InviteResponseDto toDto(InviteEntity invite) {
        InviteResponseDto dto = new InviteResponseDto();
        dto.setId(invite.getId());
        dto.setCompanyId(invite.getCompanyId());
        dto.setCode(invite.getCode());
        dto.setExpiresAt(invite.getExpiresAt());
        dto.setRole(invite.getRole() == null
                ? null
                : MemberRoleDto.valueOf(invite.getRole().name()));
        return dto;
    }
}
