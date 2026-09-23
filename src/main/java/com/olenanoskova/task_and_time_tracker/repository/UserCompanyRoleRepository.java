package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCompanyRoleRepository extends JpaRepository<UserCompanyRoleEntity, UUID> {
    List<UserCompanyRoleEntity> findByCompanyId(UUID companyId);
    Optional<UserCompanyRoleEntity> findByUserIdAndCompanyId(UUID userId, UUID companyId);
    boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId);
    Optional<UserCompanyRoleEntity> findByCompanyIdAndUserId(UUID companyId, UUID userId);
    Optional<UserCompanyRoleEntity> findFirstByUserId(UUID userId);

    /**
     * All companies the user belongs to. A user may be a member of several
     * companies with same or different roles; single-company shortcuts on top
     * of this data are incorrect.
     */
    @Query("SELECT r.companyId FROM UserCompanyRoleEntity r WHERE r.userId = :userId")
    List<UUID> findCompanyIdsByUserId(UUID userId);
}

