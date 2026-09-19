package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private WorkspaceTypeEntity type; // PERSONAL or COMPANY

    private UUID ownerId; // user who owns this workspace

    private UUID companyId; // only for COMPANY type

    private Instant createdAt;
    private Instant updatedAt;
}
