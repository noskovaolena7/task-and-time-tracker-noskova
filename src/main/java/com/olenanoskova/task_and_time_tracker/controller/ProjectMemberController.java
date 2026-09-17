package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMemberMapper;
import com.olenanoskova.task_and_time_tracker.service.ProjectMemberService;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService memberService;
    private final ProjectMemberMapper memberMapper;

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponseDto>> getProjectMembers(@PathVariable UUID projectId) {
        List<ProjectMember> members = memberService.getMembers(projectId);
        List<ProjectMemberResponseDto> responseList = members.stream()
                .map(memberMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<ProjectMemberResponseDto> addMemberToProject(
            @PathVariable UUID projectId,
            @RequestBody ProjectMemberCreateRequestDto request) {

        ProjectMember member = memberMapper.toDomain(projectId, request);
        ProjectMember assignedMember = memberService.addMember(projectId, member);
        ProjectMemberResponseDto response = memberMapper.toDto(assignedMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
