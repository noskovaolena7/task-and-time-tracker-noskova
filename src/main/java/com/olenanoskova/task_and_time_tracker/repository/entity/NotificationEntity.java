package com.olenanoskova.task_and_time_tracker.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Column(name = "sender_id")
    private UUID senderId;

    private UUID projectId;

    private UUID taskId;

    @Enumerated(EnumType.STRING)
    private NotificationStatusEntity status;

    private String type;

    private String message;

    @Column(name = "is_read") // DB column is `is_read`, entity field is `read`
    private Boolean read;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Boolean getIsRead() {
        return read;
    }

    public void setIsRead(Boolean isRead) {
        this.read = isRead;
    }
}
