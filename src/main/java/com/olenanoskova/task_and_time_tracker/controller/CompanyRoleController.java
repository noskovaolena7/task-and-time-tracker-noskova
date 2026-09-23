package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.UserCompanyRoleMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.UserCompanyRoleService;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/roles")
@RequiredArgsConstructor
public class CompanyRoleController {

    private final UserCompanyRoleService roleService;
    private final UserCompanyRoleMapper roleMapper;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("@securityService.canViewRoles(#companyId)")
    public ResponseEntity<List<UserCompanyRoleResponseDto>> getRoles(@PathVariable UUID companyId) {
        List<UserCompanyRole> roles = roleService.getRoles(companyId);
        List<UserCompanyRoleResponseDto> responseList = roles.stream()
                .map(roleMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("@securityService.canAssignRole(#companyId, #request.role.name())")
    public ResponseEntity<UserCompanyRoleResponseDto> createRole(
            @PathVariable UUID companyId,
            @Valid @RequestBody UserCompanyRoleCreateRequestDto request) {

        UserCompanyRole role = roleMapper.toDomain(request);
        role.setInvitedBy(securityService.getCurrentUserId());
        UserCompanyRole createdRole = roleService.assignRole(companyId, role);
        UserCompanyRoleResponseDto response = roleMapper.toDto(createdRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("@securityService.canAssignRole(#companyId, #request.role.name())")
    public ResponseEntity<UserCompanyRoleResponseDto> updateRole(
            @PathVariable UUID companyId,
            @PathVariable UUID roleId,
            @Valid @RequestBody UserCompanyRoleCreateRequestDto request) {

        UserCompanyRole existing = roleService.getRoleById(roleId);
        if (!companyId.equals(existing.getCompanyId())) {
            throw new com.olenanoskova.task_and_time_tracker.exception.UserCompanyRoleNotFoundException(roleId);
        }
        UserCompanyRole role = roleMapper.toDomain(request);
        role.setCompanyId(companyId);
        UserCompanyRole updated = roleService.updateRole(roleId, role);

        return ResponseEntity.ok(roleMapper.toDto(updated));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("@securityService.canRemoveCompanyRole(#companyId, #roleId)")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID companyId,
            @PathVariable UUID roleId) {

        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

}
