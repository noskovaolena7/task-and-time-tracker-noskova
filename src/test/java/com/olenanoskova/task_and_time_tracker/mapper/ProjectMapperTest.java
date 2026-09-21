package com.olenanoskova.task_and_time_tracker.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    ProjectMapper mapper = new ProjectMapper();

    @Test
    void smoke() {
        assertNotNull(mapper);
    }
}
