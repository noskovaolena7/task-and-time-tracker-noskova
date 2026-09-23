package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserId(UUID userId);

    List<NotificationEntity> findByUserId(UUID userId, Pageable pageable);

    List<NotificationEntity> findBySenderId(UUID senderId);

    boolean existsByUserIdAndSenderId(UUID userId, UUID senderId);

    void deleteByUserIdAndSenderId(UUID userId, UUID senderId);
}
