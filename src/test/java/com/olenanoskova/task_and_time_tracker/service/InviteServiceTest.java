package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.InviteRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private com.olenanoskova.task_and_time_tracker.repository.UserRepository userRepository;

    @Mock
    private com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository userCompanyRoleRepository;

    @InjectMocks
    private InviteServiceImpl inviteService;

    @Test
    void generateInvite_success() {
        UUID companyId = UUID.randomUUID();
        InviteEntity saved = new InviteEntity();
        saved.setId(UUID.randomUUID());
        saved.setCompanyId(companyId);
        saved.setCode("invite-code-123");

        when(inviteRepository.save(any(InviteEntity.class))).thenReturn(saved);

        InviteEntity result = inviteService.generateInvite(companyId);
        assertNotNull(result);
        assertEquals(companyId, result.getCompanyId());
    }

    @Test
    void resolveCompany_success() {
        String code = "valid-code";
        UUID companyId = UUID.randomUUID();
        InviteEntity invite = new InviteEntity();
        invite.setCode(code);
        invite.setCompanyId(companyId);
        invite.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));

        when(inviteRepository.findByCode(code)).thenReturn(Optional.of(invite));

        UUID resolved = inviteService.resolveCompany(code);
        assertEquals(companyId, resolved);
    }

    @Test
    void resolveCompany_notFound_throws() {
        when(inviteRepository.findByCode("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inviteService.resolveCompany("unknown"));
    }

    @Test
    void resolveCompany_expired_throws() {
        String code = "expired-code";
        InviteEntity invite = new InviteEntity();
        invite.setCode(code);
        invite.setExpiresAt(Instant.now().minus(2, ChronoUnit.DAYS));

        when(inviteRepository.findByCode(code)).thenReturn(Optional.of(invite));

        assertThrows(RuntimeException.class, () -> inviteService.resolveCompany(code));
    }

    @Test
    void joinCompany_success_assignsInviteRole() {
        String code = "join-code";
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        InviteEntity invite = new InviteEntity();
        invite.setCode(code);
        invite.setCompanyId(companyId);
        invite.setRole(com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity.USER);
        invite.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));

        when(inviteRepository.findByCode(code)).thenReturn(Optional.of(invite));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userCompanyRoleRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.empty());
        when(userCompanyRoleRepository.save(any(com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UUID joined = inviteService.joinCompany(code, userId);
        assertEquals(companyId, joined);
    }

    @Test
    void joinCompany_alreadyMember_throws() {
        String code = "join-code";
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        InviteEntity invite = new InviteEntity();
        invite.setCode(code);
        invite.setCompanyId(companyId);
        invite.setRole(com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity.USER);
        invite.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));

        when(inviteRepository.findByCode(code)).thenReturn(Optional.of(invite));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userCompanyRoleRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(new com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity()));

        assertThrows(RuntimeException.class, () -> inviteService.joinCompany(code, userId));
    }
}
