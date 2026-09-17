package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.UserCompanyRoleMapper;
import com.olenanoskova.task_and_time_tracker.service.UserCompanyRoleService;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/roles")
@RequiredArgsConstructor
public class CompanyRoleController {

    private final UserCompanyRoleService roleService;
    private final UserCompanyRoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<List<UserCompanyRoleResponseDto>> getRoles(@PathVariable UUID companyId) {
        List<UserCompanyRole> roles = roleService.getRoles(companyId);
        List<UserCompanyRoleResponseDto> responseList = roles.stream()
                .map(roleMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<UserCompanyRoleResponseDto> createRole(
            @PathVariable UUID companyId,
            @Valid @RequestBody UserCompanyRoleCreateRequestDto request) {

        UserCompanyRole role = roleMapper.toDomain(request);
        UserCompanyRole createdRole = roleService.assignRole(companyId, role);
        UserCompanyRoleResponseDto response = roleMapper.toDto(createdRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
