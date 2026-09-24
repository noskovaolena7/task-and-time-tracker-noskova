package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.BadRequestException;
import com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMapper;
import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectMemberRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Project createProject(Project project) {

        log.info("Attempting to create project with name {}", project.getName());

        if (project.getCompanyId() != null) {
            // Company project: company must exist, name unique inside company
            if (!companyRepository.existsById(project.getCompanyId())) {
                throw new CompanyNotFoundException(project.getCompanyId());
            }

            Optional<ProjectEntity> existing =
                    projectRepository.findByNameAndCompanyId(project.getName(), project.getCompanyId());

            if (existing.isPresent()) {
                throw new ProjectAlreadyExistException(project.getName());
            }
        } else {
            // Personal project (no company): name unique per creator
            if (project.getCreatedBy() == null) {
                throw new BadRequestException("Creator ID is required for a personal project");
            }

            Optional<ProjectEntity> existing = projectRepository
                    .findByNameAndCreatedByAndCompanyIdIsNull(project.getName(), project.getCreatedBy());

            if (existing.isPresent()) {
                throw new ProjectAlreadyExistException(project.getName());
            }
        }

        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        ProjectEntity entity = projectMapper.toEntity(project);
        ProjectEntity saved = projectRepository.save(entity);

        log.info("Successfully created project with name {}", project.getName());

        return projectMapper.toDomain(saved);
    }

    @Override
    public List<Project> getProjects(Integer page, Integer size, UUID companyId) {

        log.info("Fetching projects page={}, size={}, companyId={}", page, size, companyId);

        List<ProjectEntity> entities;

        if (companyId != null) {
            entities = projectRepository.findByCompanyId(companyId);
        } else if (page != null && size != null) {
            entities = projectRepository.findAll(PageRequest.of(page, size)).getContent();
        } else {
            entities = projectRepository.findAll();
        }

        return entities.stream()
                .map(projectMapper::toDomain)
                .toList();
    }

    @Override
    public List<Project> getPersonalProjects(UUID userId, Integer page, Integer size) {

        log.info("Fetching personal projects for user {}, page={}, size={}", userId, page, size);

        List<ProjectEntity> entities = projectRepository.findByCompanyIdIsNullAndCreatedBy(userId);

        if (page != null && size != null) {
            int from = Math.min(page * size, entities.size());
            int to = Math.min(from + size, entities.size());
            entities = entities.subList(from, to);
        }

        return entities.stream()
                .map(projectMapper::toDomain)
                .toList();
    }

    @Override
    public List<Project> getMemberProjects(UUID userId) {

        log.info("Fetching member projects for user {}", userId);

        List<UUID> projectIds = projectMemberRepository.findProjectIdsByUserId(userId);

        return projectRepository.findAllById(projectIds).stream()
                .map(projectMapper::toDomain)
                .toList();
    }

    @Override
    public Project getProjectById(UUID id) {

        log.info("Fetching project with id {}", id);

        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        return projectMapper.toDomain(entity);
    }

    @Override
    public Project updateProject(UUID id, Project project) {

        log.info("Attempting to update project with id {}", id);

        Optional<ProjectEntity> optionalProject = projectRepository.findById(id);

        if (optionalProject.isEmpty()) {
            throw new ProjectNotFoundException(id);
        }

        ProjectEntity entity = optionalProject.get();
        entity.setName(project.getName());
        entity.setDescription(project.getDescription());
        entity.setUpdatedAt(Instant.now());

        ProjectEntity saved = projectRepository.save(entity);

        log.info("Successfully updated project with id {}", id);

        return projectMapper.toDomain(saved);
    }

    @Override
    public void deleteProject(UUID id) {

        log.info("Attempting to delete project with id {}", id);

        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException(id);
        }

        projectRepository.deleteById(id);

        log.info("Successfully deleted project with id {}", id);
    }
}
