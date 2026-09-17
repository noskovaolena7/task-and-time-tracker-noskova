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
}
