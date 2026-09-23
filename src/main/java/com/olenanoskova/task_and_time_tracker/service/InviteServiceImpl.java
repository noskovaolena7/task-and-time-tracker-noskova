package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.BadRequestException;
import com.olenanoskova.task_and_time_tracker.exception.RoleAlreadyAssignedException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.repository.InviteRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final UserCompanyRoleRepository userCompanyRoleRepository;

    @Override
    public InviteEntity generateInvite(UUID companyId) {
        InviteEntity invite = new InviteEntity();
        invite.setCompanyId(companyId);
        invite.setCode(UUID.randomUUID().toString());
        invite.setExpiresAt(
                Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS));
        invite.setRole(MemberRoleEntity.USER);
        return inviteRepository.save(invite);
    }

    @Override
    public UUID resolveCompany(String inviteCode) {
        InviteEntity invite = inviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new BadRequestException("Invalid invite code"));

        if (invite.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Invite expired");
        }

        return invite.getCompanyId();
    }

    @Override
    public UUID joinCompany(String inviteCode, UUID userId) {
        InviteEntity invite = inviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new BadRequestException("Invalid invite code"));

        if (invite.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Invite expired");
        }

        if (userId == null || !userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        UUID companyId = invite.getCompanyId();

        if (userCompanyRoleRepository.findByUserIdAndCompanyId(userId, companyId).isPresent()) {
            throw new RoleAlreadyAssignedException(userId, companyId);
        }

        UserCompanyRoleEntity membership = new UserCompanyRoleEntity();
        membership.setUserId(userId);
        membership.setCompanyId(companyId);
        membership.setRole(invite.getRole() != null ? invite.getRole() : MemberRoleEntity.USER);
        membership.setCreatedAt(Instant.now());
        membership.setUpdatedAt(Instant.now());
        userCompanyRoleRepository.save(membership);

        return companyId;
    }
}
