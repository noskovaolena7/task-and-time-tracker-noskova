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
    private final CompanyRepository companyRepository;
    private final WorkspaceRepository workspaceRepository;
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

    /**
     * Whether the user holds any role in the company (member of it),
     * regardless of which one.
     */
    public boolean hasAnyCompanyRole(UUID companyId, UUID userId) {
        if (companyId == null || userId == null) return false;
        return userCompanyRoleRepository.findByUserIdAndCompanyId(userId, companyId).isPresent();
    }

    /**
     * Companies where the user is OWNER, ADMIN or MANAGER (management scope).
     */
    public List<UUID> getManagedCompanyIds(UUID userId) {
        if (userId == null) return List.of();
        return userCompanyRoleRepository.findCompanyIdsByUserId(userId).stream()
                .filter(companyId ->
                        isOwnerOrAdmin(companyId, userId) || isManager(companyId, userId))
                .toList();
    }

    private boolean sameCompany(UUID userA, UUID userB) {
        if (userA == null || userB == null) return false;
        List<UUID> companiesA = userCompanyRoleRepository.findCompanyIdsByUserId(userA);
        List<UUID> companiesB = userCompanyRoleRepository.findCompanyIdsByUserId(userB);
        return companiesA.stream().anyMatch(companiesB::contains);
    }

    private boolean shareProject(UUID userA, UUID userB) {
        List<UUID> a = projectMemberRepository.findProjectIdsByUserId(userA);
        List<UUID> b = projectMemberRepository.findProjectIdsByUserId(userB);
        return a.stream().anyMatch(b::contains);
    }

    // -------------------------------
    //  PERSONAL WORKSPACE
    // -------------------------------

    /**
     * A personal project has no company (company_id IS NULL) and belongs
     * to its creator. Personal users manage such projects without any
     * company role.
     */
    public boolean isPersonalProject(UUID projectId) {
        if (projectId == null) return false;
        if (!projectRepository.existsById(projectId)) return false;
        return projectRepository.findCompanyIdByProjectId(projectId) == null;
    }

    public boolean isPersonalProjectOwner(UUID projectId, UUID userId) {
        if (projectId == null || userId == null) return false;
        return userId.equals(projectRepository.findCreatedByByProjectId(projectId));
    }

    // -------------------------------
    //  TASK SECURITY
    // -------------------------------

    /**
     * Authorizes {@code GET /tasks} listing. When a project filter is supplied,
     * project-level access applies; otherwise any authenticated user may list
     * (service layer scopes the result to visible projects).
     */
    public boolean canListTasks(UUID projectId) {
        if (getCurrentUserId() == null) return false;
        if (projectId == null) return true;
        return canAccessProject(projectId);
    }

    public boolean canCreateTask(UUID projectId) {
        UUID userId = getCurrentUserId();
        if (userId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, userId)
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
        }

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
        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, userId)
                    || userId.equals(task.getAssignedTo())
                    || userId.equals(task.getCreatedBy())
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
        }

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
        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, userId)
                    || userId.equals(task.getCreatedBy());
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, userId)) return true;

        return userId.equals(task.getCreatedBy());
    }

    // -------------------------------
    //  USER SECURITY
    // -------------------------------

    public boolean canCreateUser(UUID targetCompanyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || targetCompanyId == null) return false;

        return isOwnerOrAdmin(targetCompanyId, requesterId);
    }

    public boolean canListUsers() {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        return !getManagedCompanyIds(requesterId).isEmpty();
    }

    public boolean canAccessUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (requesterId.equals(targetUserId)) return true;

        List<UUID> shared = sharedCompanyIds(requesterId, targetUserId);
        if (shared.isEmpty()) return false;

        if (shared.stream().anyMatch(companyId ->
                isOwnerOrAdmin(companyId, requesterId) || isManager(companyId, requesterId))) {
            return true;
        }

        return shareProject(requesterId, targetUserId);
    }

    public boolean canUpdateUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (requesterId.equals(targetUserId)) return true;

        List<UUID> shared = sharedCompanyIds(requesterId, targetUserId);
        if (shared.isEmpty()) return false;

        return shared.stream().anyMatch(companyId -> isOwnerOrAdmin(companyId, requesterId));
    }

    public boolean canDeleteUser(UUID targetUserId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        List<UUID> shared = sharedCompanyIds(requesterId, targetUserId);
        if (shared.isEmpty()) return false;

        return shared.stream().anyMatch(companyId -> isOwnerOrAdmin(companyId, requesterId));
    }

    private List<UUID> sharedCompanyIds(UUID userA, UUID userB) {
        if (userA == null || userB == null) return List.of();
        List<UUID> companiesB = userCompanyRoleRepository.findCompanyIdsByUserId(userB);
        return userCompanyRoleRepository.findCompanyIdsByUserId(userA).stream()
                .filter(companiesB::contains)
                .toList();
    }

    // -------------------------------
    //  COMPANY SECURITY
    // -------------------------------

    /**
     * Authorizes access to a company resource by company id: owner/admin/manager
     * of that company, the recorded company owner, or any member of the same company.
     * Owners of *other* companies are rejected.
     */
    public boolean canAccessCompany(UUID companyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || companyId == null) return false;

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        // Fallback to the recorded owner (e.g. role row missing): still scoped to THIS company.
        UUID recordedOwner = companyRepository.findById(companyId)
                .map(com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity::getOwnerId)
                .orElse(null);
        if (requesterId.equals(recordedOwner)) return true;

        // Any membership in THIS company (a user may belong to several companies).
        return hasAnyCompanyRole(companyId, requesterId);
    }

    /**
     * Authorizes invite management for a company: owner or admin of that company.
     */
    public boolean canManageInvites(UUID companyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || companyId == null) return false;

        return isOwnerOrAdmin(companyId, requesterId);
    }

    // -------------------------------
    //  PROJECT SECURITY
    // -------------------------------

    public boolean canCreateProject(UUID companyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        // Personal project (no company): any authenticated user.
        if (companyId == null) return true;

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canAccessProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId)
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canUpdateProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canDeleteProject(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

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

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canRemoveProjectMember(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canListProjectMembers(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId)
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
        }

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

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        return isOwnerOrAdmin(companyId, requesterId)
                || isManager(companyId, requesterId);
    }

    public boolean canAccessDeadline(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId)
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
        }

        UUID companyId = projectRepository.findCompanyIdByProjectId(projectId);

        if (isOwnerOrAdmin(companyId, requesterId)) return true;
        if (isManager(companyId, requesterId)) return true;

        return projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
    }

    public boolean canUpdateDeadline(UUID projectId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null) return false;

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId);
        }

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

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId)
                    || (projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId)
                    && requesterId.equals(uploadedBy));
        }

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

        if (isPersonalProject(projectId)) {
            return isPersonalProjectOwner(projectId, requesterId)
                    || projectMemberRepository.existsByProjectIdAndUserId(projectId, requesterId);
        }

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

    /**
     * Authorizes role assignment inside a company:
     * - OWNER may grant USER, MANAGER, ADMIN and OWNER (co-owners with equal rights);
     * - ADMIN may grant USER and MANAGER only (cannot create admins/owners).
     */
    public boolean canAssignRole(UUID companyId, String roleName) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || companyId == null || roleName == null) return false;

        String role = roleName.toUpperCase();
        boolean isOwner = hasCompanyRole(companyId, requesterId, "OWNER");
        boolean isAdmin = hasCompanyRole(companyId, requesterId, "ADMIN");

        if (isOwner) {
            return role.equals("USER") || role.equals("MANAGER")
                    || role.equals("ADMIN") || role.equals("OWNER");
        }
        if (isAdmin) {
            return role.equals("USER") || role.equals("MANAGER");
        }
        return false;
    }

    /**
     * Authorizes company management (update/delete): recorded owner or OWNER role.
     */
    public boolean canManageCompany(UUID companyId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || companyId == null) return false;

        if (hasCompanyRole(companyId, requesterId, "OWNER")) return true;

        return companyRepository.findById(companyId)
                .map(company -> requesterId.equals(company.getOwnerId()))
                .orElse(false);
    }

    /**
     * Authorizes removal of a company role row:
     * - a user may always remove their own row (leave the company);
     * - OWNER may remove anyone's row;
     * - ADMIN may remove USER and MANAGER rows only.
     * The recorded company owner row cannot be removed via this check.
     */
    public boolean canRemoveCompanyRole(UUID companyId, UUID roleId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || companyId == null || roleId == null) return false;

        var roleEntity = userCompanyRoleRepository.findById(roleId).orElse(null);
        if (roleEntity == null || !companyId.equals(roleEntity.getCompanyId())) return false;

        if (requesterId.equals(roleEntity.getUserId())) return true; // leave

        boolean targetIsOwner = "OWNER".equals(roleEntity.getRole().name());
        if (targetIsOwner) return false;

        if (hasCompanyRole(companyId, requesterId, "OWNER")) return true;
        if (hasCompanyRole(companyId, requesterId, "ADMIN")) {
            String target = roleEntity.getRole().name();
            return "USER".equals(target) || "MANAGER".equals(target);
        }
        return false;
    }

    /**
     * Authorizes workspace management: only the recorded owner may delete it.
     * A personal workspace therefore stays until its owner deletes it.
     */
    public boolean canManageWorkspace(UUID workspaceId) {
        UUID requesterId = getCurrentUserId();
        if (requesterId == null || workspaceId == null) return false;

        return workspaceRepository.findById(workspaceId)
                .map(ws -> requesterId.equals(ws.getOwnerId()))
                .orElse(false);
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
    public List<UUID> getCurrentUserCompanyIds() {
        UUID userId = getCurrentUserId();
        if (userId == null) return List.of();

        return userCompanyRoleRepository.findCompanyIdsByUserId(userId);
    }


}
