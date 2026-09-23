package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.RoleAlreadyAssignedException;
import com.olenanoskova.task_and_time_tracker.exception.UserCompanyRoleNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.UserCompanyRoleMapper;
import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCompanyRoleServiceTest {

    @Mock
    private UserCompanyRoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserCompanyRoleMapper roleMapper;

    @InjectMocks
    private UserCompanyRoleServiceImpl roleService;

    @Test
    void assignRole_companyNotFound_throws() {
        UUID companyId = UUID.randomUUID();
        when(companyRepository.existsById(companyId)).thenReturn(false);

        UserCompanyRole role = new UserCompanyRole();
        assertThrows(CompanyNotFoundException.class, () -> roleService.assignRole(companyId, role));
    }

    @Test
    void assignRole_userNotFound_throws() {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(false);

        UserCompanyRole role = new UserCompanyRole();
        role.setUserId(userId);

        assertThrows(UserNotFoundException.class, () -> roleService.assignRole(companyId, role));
    }

    @Test
    void assignRole_alreadyAssigned_throws() {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(roleRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(new UserCompanyRoleEntity()));

        UserCompanyRole role = new UserCompanyRole();
        role.setUserId(userId);

        assertThrows(RoleAlreadyAssignedException.class, () -> roleService.assignRole(companyId, role));
    }

    @Test
    void assignRole_success() {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(roleRepository.findByUserIdAndCompanyId(userId, companyId)).thenReturn(Optional.empty());

        UserCompanyRole role = new UserCompanyRole();
        role.setUserId(userId);
        role.setRole(MemberRole.MANAGER);

        UserCompanyRoleEntity entity = new UserCompanyRoleEntity();
        UserCompanyRoleEntity saved = new UserCompanyRoleEntity();
        UserCompanyRole domain = new UserCompanyRole();
        domain.setId(UUID.randomUUID());

        when(roleMapper.toEntity(role)).thenReturn(entity);
        when(roleRepository.save(entity)).thenReturn(saved);
        when(roleMapper.toDomain(saved)).thenReturn(domain);

        UserCompanyRole result = roleService.assignRole(companyId, role);
        assertNotNull(result.getId());
    }

    @Test
    void getRoles_returnsList() {
        UUID companyId = UUID.randomUUID();
        UserCompanyRoleEntity entity = new UserCompanyRoleEntity();
        UserCompanyRole domain = new UserCompanyRole();

        when(roleRepository.findByCompanyId(companyId)).thenReturn(List.of(entity));
        when(roleMapper.toDomain(entity)).thenReturn(domain);

        List<UserCompanyRole> roles = roleService.getRoles(companyId);
        assertEquals(1, roles.size());
    }

    @Test
    void getRoleById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserCompanyRoleNotFoundException.class, () -> roleService.getRoleById(id));
    }

    @Test
    void deleteRole_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserCompanyRoleNotFoundException.class, () -> roleService.deleteRole(id));
    }

    @Test
    void deleteRole_success() {
        UUID id = UUID.randomUUID();
        var entity = new com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity();
        entity.setId(id);
        entity.setUserId(UUID.randomUUID());
        entity.setCompanyId(UUID.randomUUID());
        entity.setRole(com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity.USER);
        when(roleRepository.findById(id)).thenReturn(Optional.of(entity));

        roleService.deleteRole(id);
        verify(roleRepository).deleteById(id);
    }
}
