package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("""
        select t from TaskEntity t
        where (:status is null or t.status = :status)
          and (:projectId is null or t.projectId = :projectId)
          and (:assignedTo is null or t.assignedTo = :assignedTo)
        """)
    List<TaskEntity> findWithFilters(
            @Param("status") TaskStatusEntity status,
            @Param("projectId") UUID projectId,
            @Param("assignedTo") UUID assignedTo
    );
}
