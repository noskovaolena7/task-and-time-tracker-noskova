package com.olenanoskova.task_and_time_tracker.integration;

import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TestcontainersPostgresIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("itest")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void ensureDockerAvailable() {
        Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable(),
                "Docker/Testcontainers is unavailable in this environment; skipping integration test."
        );
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void contextLoadsAndPersistsUsersAndProjects() {
        UserEntity user = new UserEntity();
        user.setEmail("ci@example.com");
        user.setFirstName("CI");
        user.setLastName("User");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        UserEntity savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        ProjectEntity project = new ProjectEntity();
        project.setName("CI Project");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        ProjectEntity savedProject = projectRepository.save(project);
        assertThat(savedProject.getId()).isNotNull();
    }
}
