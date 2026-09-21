package com.olenanoskova.task_and_time_tracker.security;

import com.olenanoskova.task_and_time_tracker.repository.*;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.service.TokenService;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserCompanyRoleRepository userCompanyRoleRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;
    private final TokenService tokenService;


    // -------------------------------
    //  BASIC CHECKS
    // -------------------------------

    private boolean isOwnerOrAdmin(UUID companyId, UUID userId) {
        return hasCompanyRole(companyId, userId, "OWNER")
                || hasCompanyRole(companyId, userId, "ADMIN");
    }

    private boolean isManager(UUID companyId, UUID userId) {
        return hasCompanyRole(companyId, userId, "MANAGER");
    }

    public boolean hasCompanyRole(UUID companyId, UUID userId, String role) {
        return userCompanyRoleRepository
                .findByCompanyIdAndUserId(companyId, userId)
                .map(r -> r.getRole().name().equals(role))
                .orElse(false);
    }

    private boolean sameCompany(UUID userA, UUID userB) {
        UUID companyA = userCompanyRoleRepository.findCompanyIdByUserId(userA).orElse(null);
        UUID companyB = userCompanyRoleRepository.findCompanyIdByUserId(userB).orElse(null);
        return companyA != null && companyA.equals(companyB);
    }

    private boolean shareProject(UUID userA, UUID userB) {
        List<UUID> a = projectMemberRepository.findProjectIdsByUserId(userA);
        List<UUID> b = projectMemberRepository.findProjectIdsByUserId(userB);
        return a.stream().anyMatch(b::contains);
    }

    // -------------------------------
    //  TASK SECURITY
    // -------------------------------

    public boolean canCreateTask(UUID projectId) {
        UUID userId = getCurrentUserId();
        if (userId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, userId)
                || isManager(companyId, userId)
                || projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    public boolean canAccessTask(UUID taskId) {
        UUID userId = getCurrentUserId();
        if (userId == null) return false;

        TaskEntity task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return false;

        UUID projectId = task.getProjectId();
        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, userId)) return true;

        return userId.equals(task.getAssignedTo())
                || userId.equals(task.getCreatedBy());
    }

    public boolean canDeleteTask(UUID taskId) {
        UUID userId = getCurrentUserId();
        if (userId == null) return false;

        TaskEntity task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return false;

        UUID projectId = task.getProjectId();
        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, userId)) return true;

        return userId.equals(task.getCreatedBy());
    }

    // -------------------------------
    //  USER SECURITY
    // -------------------------------

    public boolean canCreateUser(UUID targetCompanyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID requesterCompanyId = userCompanyRoleRepository.findCompanyIdByUserId(requesterId).orElse(null);
        if (requesterCompanyId == null) return false;

        return isOwnerOrAdmin(requesterCompanyId, requesterId)
                && requesterCompanyId.equals(targetCompanyId);
    }

    public boolean canListUsers() {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = userCompanyRoleRepository.findCompanyIdByUserId(requesterId).orElse(null);
        if (companyId == null) return false;

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canAccessUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (requesterId.equals(targetUserId)) return true;

        if (sameCompany(requesterId, targetUserId)) {
            UUID companyId = userCompanyRoleRepository.findCompanyIdByUserId(requesterId).orElse(null);

            if (isOwnerOrAdmin(companyId, requesterId)) return true;
            if (isManager(companyId, requesterId)) return true;

            return shareProject(requesterId, targetUserId);
        } else {
            return false;
        }
    }

    public boolean canUpdateUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (requesterId.equals(targetUserId)) return true;

        if (sameCompany(requesterId, targetUserId)) {
            UUID companyId = userCompanyRoleRepository.findCompanyIdByUserId(requesterId).orElse(null);
            return isOwnerOrAdmin(companyId, requesterId);
        } else {
            return false;
        }
    }

    public boolean canDeleteUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (sameCompany(requesterId, targetUserId)) {
            UUID companyId = userCompanyRoleRepository.findCompanyIdByUserId(requesterId).orElse(null);
            return isOwnerOrAdmin(companyId, requesterId);
        } else {
            return false;
        }
    }

    // -------------------------------
    //  PROJECT SECURITY
    // -------------------------------

    public boolean canCreateProject(UUID companyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canAccessProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canUpdateProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canDeleteProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId);
    }

    // -------------------------------
    //  PROJECT MEMBER SECURITY
    // -------------------------------

    public boolean isProjectMember(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canAddProjectMember(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canRemoveProjectMember(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canListProjectMembers(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        return isProjectMember(projectId);
    }

    // -------------------------------
    //  DEADLINE SECURITY
    // -------------------------------

    public boolean canCreateDeadline(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canAccessDeadline(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canUpdateDeadline(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    // -------------------------------
    //  ATTACHMENT SECURITY
    // -------------------------------

    public boolean canManageAttachment(UUID projectId, UUID uploadedBy) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) {
            return true;
        }

        boolean isProjectMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
        if (!isProjectMember) {
            return false;
        }

        return requesterId.equals(uploadedBy);
    }

    public boolean canViewAttachment(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) {
            return true;
        }

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canManageAttachmentById(UUID attachmentId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        var entity = attachmentRepository.findById(attachmentId).orElse(null);
        if (entity == null) return false;

        UUID projectId = entity.getProjectId();
        UUID uploadedBy = entity.getUploadedBy();

        return canManageAttachment(projectId, uploadedBy);
    }

    public boolean canViewAttachmentsOfTask(UUID taskId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID projectId = taskRepository.findById(taskId)
                .map(TaskEntity::getProjectId)
                .orElse(null);

        if (projectId == null) return false;

        return canViewAttachment(projectId);
    }

    public boolean canCreateAttachment(UUID taskId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        UUID projectId = taskRepository.findById(taskId)
                .map(TaskEntity::getProjectId)
                .orElse(null);

        if (projectId == null) return false;

        return canManageAttachment(projectId, requesterId);
    }

    // -------------------------------
    //  GET CURRENT USER
    // -------------------------------

    public UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        }

        String token = (String) authentication.getCredentials();
        if (token == null) return null;

        String userId = tokenService.getUserId(token);
        return UUID.fromString(userId);
    }

    public Role getCurrentUserRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        }

        String token = (String) authentication.getCredentials();
        if (token == null) return null;

        return tokenService.getRole(token);
    }
    public UUID getCurrentUserCompanyId() {
        UUID userId = getCurrentUserId();
        if (userId == null) return null;

        return userCompanyRoleRepository.findCompanyIdByUserId(userId).orElse(null);
    }


}
