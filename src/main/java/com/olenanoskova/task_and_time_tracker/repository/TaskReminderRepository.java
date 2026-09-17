package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.TaskReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskReminderRepository extends JpaRepository<TaskReminderEntity, UUID> {
    List<TaskReminderEntity> findByTaskId(UUID taskId);
}
