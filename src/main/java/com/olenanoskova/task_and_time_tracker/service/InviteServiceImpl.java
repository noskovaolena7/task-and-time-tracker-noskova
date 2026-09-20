package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.InviteRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final InviteRepository inviteRepository;

    @Override
    public InviteEntity generateInvite(UUID companyId) {
        InviteEntity invite = new InviteEntity();
        invite.setCompanyId(companyId);
        invite.setCode(UUID.randomUUID().toString());
        invite.setExpiresAt(
                Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS));
        invite.setRole(MemberRoleEntity.WORKER);
        return inviteRepository.save(invite);
    }

    @Override
    public UUID resolveCompany(String inviteCode) {
        InviteEntity invite = inviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        if (invite.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Invite expired");
        }

        return invite.getCompanyId();
    }
}
