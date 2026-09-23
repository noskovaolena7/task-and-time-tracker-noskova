package com.olenanoskova.task_and_time_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleResponseDto;
import com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler;
import com.olenanoskova.task_and_time_tracker.mapper.UserCompanyRoleMapper;
import com.olenanoskova.task_and_time_tracker.service.UserCompanyRoleService;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyRoleControllerTest {

    private MockMvc mvc;

    @Mock
    private UserCompanyRoleService roleService;

    @Mock
    private UserCompanyRoleMapper roleMapper;

    @InjectMocks
    private CompanyRoleController controller;

    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getRoles_returns200_withRoles() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserCompanyRole role = new UserCompanyRole();
        role.setId(UUID.randomUUID());
        role.setCompanyId(companyId);
        role.setRole(MemberRole.USER);

        UserCompanyRoleResponseDto dto = new UserCompanyRoleResponseDto();
        dto.setId(role.getId());
        dto.setCompanyId(companyId);
        dto.setRole(com.olenanoskova.task_and_time_tracker.controller.dto.RoleDto.USER);

        when(roleService.getRoles(companyId)).thenReturn(List.of(role));
        when(roleMapper.toDto(role)).thenReturn(dto);

        mvc.perform(get("/companies/{companyId}/roles", companyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(role.getId().toString()));
    }

    @Test
    void createRole_returns201_onSuccess() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserCompanyRoleCreateRequestDto req = new UserCompanyRoleCreateRequestDto();
        req.setUserId(UUID.randomUUID());
        req.setRole(com.olenanoskova.task_and_time_tracker.controller.dto.RoleDto.MANAGER);

        UserCompanyRole domain = new UserCompanyRole();
        domain.setId(UUID.randomUUID());
        domain.setCompanyId(companyId);
        domain.setUserId(req.getUserId());
        domain.setRole(MemberRole.MANAGER);

        UserCompanyRoleResponseDto dto = new UserCompanyRoleResponseDto();
        dto.setId(domain.getId());
        dto.setCompanyId(companyId);
        dto.setUserId(req.getUserId());
        dto.setRole(com.olenanoskova.task_and_time_tracker.controller.dto.RoleDto.MANAGER);

        when(roleMapper.toDomain(any(UserCompanyRoleCreateRequestDto.class))).thenReturn(domain);
        when(roleService.assignRole(eq(companyId), any(UserCompanyRole.class))).thenReturn(domain);
        when(roleMapper.toDto(domain)).thenReturn(dto);

        mvc.perform(post("/companies/{companyId}/roles", companyId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(domain.getId().toString()))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }
}
