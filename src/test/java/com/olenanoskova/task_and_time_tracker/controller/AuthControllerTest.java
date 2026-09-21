package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterUserRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterCompanyRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.AuthService authService;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.AuthController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void signUpPersonal_returns201_andToken() throws Exception {
        var req = new RegisterUserRequestDto();
        req.setFirstName("Alice");
        req.setLastName("Brown");
        req.setEmail("a@b.com");
        req.setPassword("passwd1");
        req.setPhoneNumber("1234567");

        when(authService.signUpPersonalUser(any())).thenReturn("tok-123");

        mvc.perform(post("/auth/sign-up/personal")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("tok-123"));
    }

    @Test
    void signUpCompany_returns201_andToken() throws Exception {
        var req = new RegisterCompanyRequestDto();
        req.setFirstName("CompFirst");
        req.setLastName("CompLast");
        req.setEmail("c@d.com");
        req.setPassword("passwd2");
        req.setPhoneNumber("7654321");
        req.setCompanyName("CInc");

        when(authService.signUpCompanyUser(any())).thenReturn("tok-xyz");

        mvc.perform(post("/auth/sign-up/company")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("tok-xyz"));
    }
}
