package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    Optional<ProjectEntity> findByNameAndCompanyId(String name, UUID companyId);

    List<ProjectEntity> findByCompanyId(UUID companyId);

    List<ProjectEntity> findByCreatedBy(UUID createdBy);

    List<ProjectEntity> findByCompanyIdIsNullAndCreatedBy(UUID createdBy);

    Optional<ProjectEntity> findByNameAndCreatedByAndCompanyIdIsNull(String name, UUID createdBy);

    @Query("SELECT p.companyId FROM ProjectEntity p WHERE p.id = :projectId")
    UUID findCompanyIdByProjectId(UUID projectId);

    @Query("SELECT p.createdBy FROM ProjectEntity p WHERE p.id = :projectId")
    UUID findCreatedByByProjectId(UUID projectId);

}

