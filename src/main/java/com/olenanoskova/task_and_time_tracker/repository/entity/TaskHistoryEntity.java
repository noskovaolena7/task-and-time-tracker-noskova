package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID taskId;

    private UUID userId;

    @Column(name = "field_changed") // DB column is `field_changed`, domain field is `field`
    private String field;

    private String oldValue;

    private String newValue;

    @Column(name = "changed_at")
    private Instant changedAt;
}
