package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectDeadlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectDeadlineRepository extends JpaRepository<ProjectDeadlineEntity, UUID> {
    List<ProjectDeadlineEntity> findByProjectId(UUID projectId);
}
