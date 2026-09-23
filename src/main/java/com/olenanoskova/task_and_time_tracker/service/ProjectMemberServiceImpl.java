package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.ProjectMemberAlreadyExistsException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectMemberNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMemberMapper;
import com.olenanoskova.task_and_time_tracker.repository.ProjectMemberRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberMapper projectMemberMapper;

    @Override
    public ProjectMember addMember(UUID projectId, ProjectMember member) {

        log.info("Attempting to add member {} to project {}", member.getUserId(), projectId);

        // Check project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        // Check user exists
        if (!userRepository.existsById(member.getUserId())) {
            throw new UserNotFoundException(member.getUserId());
        }

        // Check if already added
        Optional<ProjectMemberEntity> existing =
                projectMemberRepository.findByProjectIdAndUserId(projectId, member.getUserId());

        if (existing.isPresent()) {
            throw new ProjectMemberAlreadyExistsException(member.getUserId(), projectId);
        }

        member.setProjectId(projectId);
        member.setCreatedAt(Instant.now());
        member.setUpdatedAt(Instant.now());

        ProjectMemberEntity entity = projectMemberMapper.toEntity(member);
        ProjectMemberEntity saved = projectMemberRepository.save(entity);

        log.info("Successfully added member {} to project {}", member.getUserId(), projectId);

        return projectMemberMapper.toDomain(saved);
    }

    @Override
    public List<ProjectMember> getMembers(UUID projectId) {

        log.info("Fetching members for project {}", projectId);

        List<ProjectMemberEntity> entities = projectMemberRepository.findByProjectId(projectId);

        return entities.stream()
                .map(projectMemberMapper::toDomain)
                .toList();
    }

    @Override
    public ProjectMember getMemberById(UUID id) {

        log.info("Fetching project member with id {}", id);

        ProjectMemberEntity entity = projectMemberRepository.findById(id)
                .orElseThrow(() -> new ProjectMemberNotFoundException(id));

        return projectMemberMapper.toDomain(entity);
    }

    @Override
    public ProjectMember updateMember(UUID id, ProjectMember member) {

        log.info("Attempting to update project member with id {}", id);

        Optional<ProjectMemberEntity> optionalMember = projectMemberRepository.findById(id);

        if (optionalMember.isEmpty()) {
            throw new ProjectMemberNotFoundException(id);
        }

        ProjectMemberEntity entity = optionalMember.get();
        entity.setMemberRole(MemberRoleEntity.valueOf(member.getMemberRole().name()));
        entity.setUpdatedAt(Instant.now());

        ProjectMemberEntity saved = projectMemberRepository.save(entity);

        log.info("Successfully updated project member with id {}", id);

        return projectMemberMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteMember(UUID projectId, UUID userId) {

        log.info("Attempting to delete member {} from project {}", userId, projectId);

        Optional<ProjectMemberEntity> existing =
                projectMemberRepository.findByProjectIdAndUserId(projectId, userId);

        if (existing.isEmpty()) {
            throw new ProjectMemberNotFoundException(userId);
        }

        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);

        log.info("Successfully deleted member {} from project {}", userId, projectId);
    }

}
