package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, UUID> {
    Optional<ProjectMemberEntity> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMemberEntity> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMemberEntity> findByUserId(UUID userId);

    default List<UUID> findProjectIdsByUserId(UUID userId) {
        return findByUserId(userId).stream()
                .map(ProjectMemberEntity::getProjectId)
                .toList();
    }

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);

}
