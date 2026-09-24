package com.olenanoskova.task_and_time_tracker.security;

import com.olenanoskova.task_and_time_tracker.repository.*;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private UserCompanyRoleRepository userCompanyRoleRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private ProjectDeadlineRepository projectDeadlineRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityService securityService;

    private MockedStatic<SecurityContextHolder> holder;

    @BeforeEach
    void setupStatic() {
        holder = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDownStatic() {
        holder.close();
    }

    private void loginAs(UUID userId) {
        SecurityContext context = mock(SecurityContext.class);
        var auth = new UsernamePasswordAuthenticationToken("user", "tok-" + userId);
        when(context.getAuthentication()).thenReturn(auth);
        holder.when(SecurityContextHolder::getContext).thenReturn(context);
        when(tokenService.getUserId("tok-" + userId)).thenReturn(userId.toString());
    }

    private TaskEntity task(UUID projectId, UUID createdBy, UUID assignedTo) {
        TaskEntity t = new TaskEntity();
        t.setId(UUID.randomUUID());
        t.setProjectId(projectId);
        t.setCreatedBy(createdBy);
        t.setAssignedTo(assignedTo);
        return t;
    }

    private void personalProject(UUID projectId, UUID ownerId) {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.findCompanyIdByProjectId(projectId)).thenReturn(null);
        when(projectRepository.findCreatedByByProjectId(projectId)).thenReturn(ownerId);
    }

    @Test
    void canUpdateTask_personalOwner_true() {
        UUID owner = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        TaskEntity t = task(projectId, UUID.randomUUID(), null);
        loginAs(owner);
        personalProject(projectId, owner);
        when(taskRepository.findById(t.getId())).thenReturn(Optional.of(t));

        assertTrue(securityService.canUpdateTask(t.getId()));
    }

    @Test
    void canUpdateTask_personalCreator_true() {
        UUID owner = UUID.randomUUID();
        UUID creator = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        TaskEntity t = task(projectId, creator, null);
        loginAs(creator);
        personalProject(projectId, owner);
        when(taskRepository.findById(t.getId())).thenReturn(Optional.of(t));

        assertTrue(securityService.canUpdateTask(t.getId()));
    }

    @Test
    void canUpdateTask_personalAssignee_true() {
        UUID owner = UUID.randomUUID();
        UUID assignee = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        TaskEntity t = task(projectId, UUID.randomUUID(), assignee);
        loginAs(assignee);
        personalProject(projectId, owner);
        when(taskRepository.findById(t.getId())).thenReturn(Optional.of(t));

        assertTrue(securityService.canUpdateTask(t.getId()));
    }

    @Test
    void canUpdateTask_personalPlainMember_false() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        TaskEntity t = task(projectId, UUID.randomUUID(), null);
        loginAs(member);
        personalProject(projectId, owner);
        when(taskRepository.findById(t.getId())).thenReturn(Optional.of(t));

        assertFalse(securityService.canUpdateTask(t.getId()));
    }
}
