package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_company_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCompanyRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID companyId;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private MemberRoleEntity role;

    @Column(name = "invited_by")
    private UUID invitedBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}

