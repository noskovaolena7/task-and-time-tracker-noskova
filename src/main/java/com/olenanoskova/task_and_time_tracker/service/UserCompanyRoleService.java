package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;

import java.util.List;
import java.util.UUID;

public interface UserCompanyRoleService {

    UserCompanyRole assignRole(UUID companyId, UserCompanyRole role);

    List<UserCompanyRole> getRoles(UUID companyId);

    UserCompanyRole getRoleById(UUID id);

    UserCompanyRole updateRole(UUID id, UserCompanyRole role);

    void deleteRole(UUID id);

    /**
     * Company members with user details (name/email) for display, search
     * and role filtering. One call instead of N user lookups.
     */
    List<com.olenanoskova.task_and_time_tracker.controller.dto.CompanyMemberDto> getMembers(UUID companyId);

    /**
     * Members visible to the requester: OWNER/ADMIN see everyone; MANAGER and
     * USER see only themselves, their inviter and coworkers sharing a project
     * in this company. Nobody sees the whole roster by default.
     */
    List<com.olenanoskova.task_and_time_tracker.controller.dto.CompanyMemberDto> getVisibleMembers(
            UUID companyId, UUID requesterId);
}
