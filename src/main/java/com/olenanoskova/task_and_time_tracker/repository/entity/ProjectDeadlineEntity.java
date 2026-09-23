package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project_deadlines")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectDeadlineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID projectId;

    private String title;

    private Instant deadline;

    // Maps directly onto the existing TEXT[] column (no @ElementCollection table needed)
    @Column(name = "reminder_periods")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> reminderPeriods;

    private UUID createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
