package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberService {

    ProjectMember addMember(UUID projectId, ProjectMember member);

    List<ProjectMember> getMembers(UUID projectId);

    ProjectMember getMemberById(UUID id);

    ProjectMember updateMember(UUID id, ProjectMember member);

    void deleteMember(UUID projectId, UUID userId);

}
