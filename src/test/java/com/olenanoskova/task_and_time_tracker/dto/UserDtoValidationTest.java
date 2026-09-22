package com.olenanoskova.task_and_time_tracker.dto;

import com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoValidationTest {

    static ValidatorFactory vf;
    static Validator validator;

    @BeforeAll
    static void init() {
        vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
    }

    @AfterAll
    static void close() { vf.close(); }

    @Test
    void userResponseDto_validationFailsForBlank() {
        UserResponseDto dto = new UserResponseDto();
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
