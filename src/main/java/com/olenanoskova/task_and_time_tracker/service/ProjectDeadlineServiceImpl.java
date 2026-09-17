package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidDeadlineException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectDeadlineNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectDeadlineMapper;
import com.olenanoskova.task_and_time_tracker.repository.ProjectDeadlineRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectDeadlineEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectDeadlineServiceImpl implements ProjectDeadlineService {

    private final ProjectDeadlineRepository projectDeadlineRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDeadlineMapper projectDeadlineMapper;

    @Override
    public List<ProjectDeadline> getAllDeadlines(UUID projectId) {
        List<ProjectDeadlineEntity> entities = projectDeadlineRepository.findByProjectId(projectId);
        return entities.stream()
                .map(projectDeadlineMapper::toDomain)
                .toList();
    }

    @Override
    public ProjectDeadline createDeadline(UUID projectId, ProjectDeadline deadline) {

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        if (deadline.getDeadline() == null) {
            throw new InvalidDeadlineException("Deadline cannot be null");
        }

        deadline.setProjectId(projectId);
        deadline.setCreatedAt(Instant.now());
        deadline.setUpdatedAt(Instant.now());

        ProjectDeadlineEntity entity = projectDeadlineMapper.toEntity(deadline);
        ProjectDeadlineEntity saved = projectDeadlineRepository.save(entity);

        return projectDeadlineMapper.toDomain(saved);
    }

    @Override
    public ProjectDeadline getDeadlineById(UUID id) {
        ProjectDeadlineEntity entity = projectDeadlineRepository.findById(id)
                .orElseThrow(() -> new ProjectDeadlineNotFoundException(id));

        return projectDeadlineMapper.toDomain(entity);
    }

    @Override
    public ProjectDeadline updateDeadline(UUID id, ProjectDeadline deadline) {

        ProjectDeadlineEntity entity = projectDeadlineRepository.findById(id)
                .orElseThrow(() -> new ProjectDeadlineNotFoundException(id));

        if (deadline.getDeadline() == null) {
            throw new InvalidDeadlineException("Deadline cannot be null");
        }

        entity.setDeadline(deadline.getDeadline());
        entity.setReminderPeriods(deadline.getReminderPeriods());
        entity.setCreatedBy(deadline.getCreatedBy());
        entity.setUpdatedAt(Instant.now());

        ProjectDeadlineEntity saved = projectDeadlineRepository.save(entity);

        return projectDeadlineMapper.toDomain(saved);
    }

    @Override
    public void deleteDeadline(UUID id) {

        if (!projectDeadlineRepository.existsById(id)) {
            throw new ProjectDeadlineNotFoundException(id);
        }

        projectDeadlineRepository.deleteById(id);
    }
}
