package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    UserMapper mapper = new UserMapper();

    @Test
    void toDomain_and_back_handlesNulls() {
        UserEntity e = new UserEntity();
        e.setId(null);
        e.setRole(com.olenanoskova.task_and_time_tracker.repository.entity.RoleEntity.USER);
        e.setStatus(com.olenanoskova.task_and_time_tracker.repository.entity.StatusEntity.ACTIVE);
        User d = mapper.toDomain(e);
        assertNotNull(d);
        UserEntity back = mapper.toEntity(d);
        assertNotNull(back);
    }
}
