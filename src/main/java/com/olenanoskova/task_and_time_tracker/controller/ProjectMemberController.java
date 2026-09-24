package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMemberMapper;
import com.olenanoskova.task_and_time_tracker.service.ProjectMemberService;
import com.olenanoskova.task_and_time_tracker.service.UserService;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberController {

    private final ProjectMemberService memberService;
    private final ProjectMemberMapper memberMapper;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("@securityService.canListProjectMembers(#projectId)")
    public ResponseEntity<List<ProjectMemberResponseDto>> getProjectMembers(@PathVariable UUID projectId) {
        List<ProjectMember> members = memberService.getMembers(projectId);
        List<ProjectMemberResponseDto> responseList = members.stream()
                .map(memberMapper::toDto)
                .peek(this::enrichWithUserData)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    private void enrichWithUserData(ProjectMemberResponseDto dto) {
        try {
            User user = userService.getUserById(dto.getUserId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
        } catch (RuntimeException e) {
            log.warn("Skipping user data for project member {}: {}", dto.getUserId(), e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("@securityService.canAddProjectMember(#projectId)")
    public ResponseEntity<ProjectMemberResponseDto> addMemberToProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectMemberCreateRequestDto request) {

        ProjectMember member = memberMapper.toDomain(projectId, request);
        ProjectMember assignedMember = memberService.addMember(projectId, member);
        ProjectMemberResponseDto response = memberMapper.toDto(assignedMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping("/{userId}")
    @PreAuthorize("@securityService.canRemoveProjectMember(#projectId)")     public ResponseEntity<Void> removeMember(
            @PathVariable UUID projectId,
            @PathVariable UUID userId) {

        memberService.deleteMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }


}
