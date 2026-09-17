package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCompanyRoleRepository extends JpaRepository<UserCompanyRoleEntity, UUID> {
    List<UserCompanyRoleEntity> findByCompanyId(UUID companyId);
    Optional<UserCompanyRoleEntity> findByUserIdAndCompanyId(UUID userId, UUID companyId);
}

