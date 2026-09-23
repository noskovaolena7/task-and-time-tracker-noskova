package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.RoleAlreadyAssignedException;
import com.olenanoskova.task_and_time_tracker.exception.UserCompanyRoleNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.UserCompanyRoleMapper;
import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCompanyRoleServiceImpl implements UserCompanyRoleService {

    private final UserCompanyRoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyRoleMapper roleMapper;

    @Override
    public UserCompanyRole assignRole(UUID companyId, UserCompanyRole role) {

        log.info("Attempting to assign role {} to user {} in company {}",
                role.getRole(), role.getUserId(), companyId);

        // Check company exists
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }

        // Check user exists
        if (!userRepository.existsById(role.getUserId())) {
            throw new UserNotFoundException(role.getUserId());
        }

        // Check if role already assigned
        Optional<UserCompanyRoleEntity> existing =
                roleRepository.findByUserIdAndCompanyId(role.getUserId(), companyId);

        if (existing.isPresent()) {
            throw new RoleAlreadyAssignedException(role.getUserId(), companyId);
        }

        role.setCompanyId(companyId);
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());

        UserCompanyRoleEntity entity = roleMapper.toEntity(role);
        UserCompanyRoleEntity saved = roleRepository.save(entity);

        log.info("Successfully assigned role {} to user {} in company {}",
                role.getRole(), role.getUserId(), companyId);

        return roleMapper.toDomain(saved);
    }

    @Override
    public List<UserCompanyRole> getRoles(UUID companyId) {

        log.info("Fetching roles for company {}", companyId);

        List<UserCompanyRoleEntity> entities = roleRepository.findByCompanyId(companyId);

        return entities.stream()
                .map(roleMapper::toDomain)
                .toList();
    }

    @Override
    public UserCompanyRole getRoleById(UUID id) {

        log.info("Fetching UserCompanyRole with id {}", id);

        UserCompanyRoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new UserCompanyRoleNotFoundException(id));

        return roleMapper.toDomain(entity);
    }

    @Override
    public UserCompanyRole updateRole(UUID id, UserCompanyRole role) {

        log.info("Attempting to update UserCompanyRole with id {}", id);

        Optional<UserCompanyRoleEntity> optionalRole = roleRepository.findById(id);

        if (optionalRole.isEmpty()) {
            throw new UserCompanyRoleNotFoundException(id);
        }

        UserCompanyRoleEntity entity = optionalRole.get();
        entity.setRole(role.getRole() != null ? com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity.valueOf(role.getRole().name()) : entity.getRole());
        entity.setUpdatedAt(Instant.now());

        UserCompanyRoleEntity saved = roleRepository.save(entity);

        log.info("Successfully updated UserCompanyRole with id {}", id);

        return roleMapper.toDomain(saved);
    }

    @Override
    public void deleteRole(UUID id) {

        log.info("Attempting to delete UserCompanyRole with id {}", id);

        UserCompanyRoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new UserCompanyRoleNotFoundException(id));

        roleRepository.deleteById(id);

        transferRecordedOwnershipIfNeeded(entity);

        log.info("Successfully deleted UserCompanyRole with id {}", id);
    }

    /**
     * Keeps companies.owner_id truthful when an OWNER row disappears: if the
     * leaving user was the recorded owner, ownership passes to another
     * remaining OWNER (or to null when none is left). All work processes
     * (projects, members, roles) are untouched, so nothing is lost on
     * ownership change.
     */
    private void transferRecordedOwnershipIfNeeded(UserCompanyRoleEntity deleted) {
        if (deleted.getRole() != MemberRoleEntity.OWNER) {
            return;
        }
        UUID companyId = deleted.getCompanyId();
        companyRepository.findById(companyId).ifPresent(company -> {
            if (!deleted.getUserId().equals(company.getOwnerId())) {
                return;
            }
            UUID successor = roleRepository.findByCompanyId(companyId).stream()
                    .filter(r -> r.getRole() == MemberRoleEntity.OWNER
                            && !r.getUserId().equals(deleted.getUserId()))
                    .map(UserCompanyRoleEntity::getUserId)
                    .findFirst()
                    .orElse(null);
            company.setOwnerId(successor);
            company.setUpdatedAt(Instant.now());
            companyRepository.save(company);
            log.info("Transferred recorded ownership of company {} to {}", companyId, successor);
        });
    }
}
