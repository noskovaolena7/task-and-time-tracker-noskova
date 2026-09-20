package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invites")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InviteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID companyId;

    private String code;

    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private MemberRoleEntity role; // WORKER by default
}
