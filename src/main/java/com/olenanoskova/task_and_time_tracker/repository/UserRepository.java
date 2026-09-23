package com.olenanoskova.task_and_time_tracker.repository;

import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    List<UserEntity> findAllByCompanyId(UUID companyId);
}
