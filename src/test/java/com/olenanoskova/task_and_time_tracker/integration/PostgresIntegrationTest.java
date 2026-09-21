package com.olenanoskova.task_and_time_tracker.integration;

import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.TimeEntryRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TimeEntryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Requires Docker/Testcontainers")
@SpringBootTest
@Testcontainers
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("itest")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.liquibase.enabled", () -> "false");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Test
    void repositorySaveAndFind_user_project_timeEntry() {
        UserEntity u = new UserEntity();
        u.setEmail("itest@example.com");
        u.setFirstName("I");
        u.setLastName("Test");
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());

        UserEntity savedUser = userRepository.save(u);
        assertThat(savedUser.getId()).isNotNull();

        ProjectEntity p = new ProjectEntity();
        p.setName("IT Project");
        p.setCompanyId(null);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        ProjectEntity savedProject = projectRepository.save(p);
        assertThat(savedProject.getId()).isNotNull();

        TimeEntryEntity t = new TimeEntryEntity();
        t.setTaskId(null);
        t.setUserId(savedUser.getId());
        t.setStartTime(Instant.now());
        t.setEndTime(Instant.now());
        t.setDurationSeconds(60L);
        TimeEntryEntity savedTE = timeEntryRepository.save(t);
        assertThat(savedTE.getId()).isNotNull();
    }
}
