package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.service.CompanyService;
import com.olenanoskova.task_and_time_tracker.mapper.CompanyMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    MockMvc mvc;

    @Mock
    CompanyService companyService;

    @Mock
    CompanyMapper companyMapper;

    @Mock
    SecurityService securityService;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.UserCompanyRoleService userCompanyRoleService;

    @InjectMocks
    CompanyController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCompanyById_returns404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(companyService.getCompanyById(id)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException(id));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/companies/{id}", id.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createCompany_returns201_andJson() throws Exception {
        UUID ownerId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.CompanyCreateRequestDto();
        req.setName("NewCo");
        req.setOwnerId(ownerId);

        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Company();
        domain.setId(UUID.randomUUID());
        domain.setName("NewCo");

        var resp = new com.olenanoskova.task_and_time_tracker.controller.dto.CompanyResponseDto();
        resp.setId(domain.getId());
        resp.setName(domain.getName());
        resp.setCreatedAt(java.time.Instant.now());

        when(companyMapper.toDomain(any(com.olenanoskova.task_and_time_tracker.controller.dto.CompanyCreateRequestDto.class))).thenReturn(domain);
        when(companyService.createCompany(domain)).thenReturn(domain);
        when(companyMapper.toDto(domain)).thenReturn(resp);

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/companies")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(domain.getId().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("NewCo"));
    }

    @Test
    void getAllCompanies_returns200_emptyList() throws Exception {
        when(securityService.getCurrentUserCompanyIds()).thenReturn(java.util.List.of());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/companies"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray());
    }
}