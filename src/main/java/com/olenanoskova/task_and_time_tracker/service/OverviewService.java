package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.UpcomingDeadlineDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UpcomingReminderDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UpcomingResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository;
import com.olenanoskova.task_and_time_tracker.service.model.Project;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Aggregates upcoming deadlines and reminders across everything visible to
 * the user (personal projects + all own companies). Powers the "Upcoming"
 * dashboard widget; the proactive scheduler variant is a separate feature.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverviewService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectDeadlineService projectDeadlineService;
    private final TaskReminderService taskReminderService;
    private final UserCompanyRoleRepository userCompanyRoleRepository;

    public UpcomingResponseDto getUpcoming(UUID userId, int daysAhead, int limit) {
        Instant now = Instant.now();
        Instant horizon = now.plus(daysAhead, java.time.temporal.ChronoUnit.DAYS);

        List<UUID> companyIds = userCompanyRoleRepository.findCompanyIdsByUserId(userId);

        List<Project> projects = new ArrayList<>(projectService.getPersonalProjects(userId, null, null));
        for (UUID companyId : companyIds) {
            projects.addAll(projectService.getProjects(null, null, companyId));
        }

        List<UpcomingDeadlineDto> deadlines = new ArrayList<>();
        List<UpcomingReminderDto> reminders = new ArrayList<>();

        for (Project project : projects) {
            for (ProjectDeadline dl : projectDeadlineService.getAllDeadlines(project.getId())) {
                if (dl.getDeadline() == null || dl.getDeadline().isAfter(horizon)) {
                    continue;
                }
                UpcomingDeadlineDto dto = new UpcomingDeadlineDto();
                dto.setId(dl.getId());
                dto.setProjectId(project.getId());
                dto.setProjectName(project.getName());
                dto.setTitle(dl.getTitle());
                dto.setDeadline(dl.getDeadline());
                dto.setOverdue(dl.getDeadline().isBefore(now));
                deadlines.add(dto);
            }
            for (Task task : taskService.getTasks(null, null, null, project.getId(), null)) {
                for (TaskReminder r : taskReminderService.getReminders(task.getId())) {
                    if (r.getRemindAt() == null || r.getRemindAt().isAfter(horizon)) {
                        continue;
                    }
                    UpcomingReminderDto dto = new UpcomingReminderDto();
                    dto.setId(r.getId());
                    dto.setTaskId(task.getId());
                    dto.setTaskTitle(task.getTitle());
                    dto.setProjectId(project.getId());
                    dto.setProjectName(project.getName());
                    dto.setRemindAt(r.getRemindAt());
                    dto.setMessage(r.getMessage());
                    dto.setOverdue(r.getRemindAt().isBefore(now));
                    reminders.add(dto);
                }
            }
        }

        deadlines.sort(Comparator.comparing(UpcomingDeadlineDto::getDeadline));
        reminders.sort(Comparator.comparing(UpcomingReminderDto::getRemindAt));

        UpcomingResponseDto response = new UpcomingResponseDto();
        response.setDeadlines(deadlines.stream().limit(limit).toList());
        response.setReminders(reminders.stream().limit(limit).toList());
        return response;
    }
}
