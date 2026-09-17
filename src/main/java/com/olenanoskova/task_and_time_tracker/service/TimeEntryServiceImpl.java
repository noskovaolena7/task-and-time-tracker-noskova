package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidTimeEntryException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TimeEntryNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TimeEntryMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.TimeEntryRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TimeEntryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TimeEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TimeEntryMapper timeEntryMapper;

    @Override
    public TimeEntry createTimeEntry(UUID taskId, TimeEntry timeEntry) {

        log.info("Attempting to create time entry for task {}", taskId);

        // Check task exists
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        // Check user exists
        if (!userRepository.existsById(timeEntry.getUserId())) {
            throw new UserNotFoundException(timeEntry.getUserId());
        }

        // Validate time
        if (timeEntry.getStartTime() == null || timeEntry.getEndTime() == null) {
            throw new InvalidTimeEntryException("StartTime or EndTime is null");
        }

        if (timeEntry.getEndTime().isBefore(timeEntry.getStartTime())) {
            throw new InvalidTimeEntryException("EndTime cannot be before StartTime");
        }

        long durationSeconds = Duration.between(timeEntry.getStartTime(), timeEntry.getEndTime()).getSeconds();

        timeEntry.setTaskId(taskId);
        timeEntry.setDurationSeconds(durationSeconds);
        timeEntry.setCreatedAt(Instant.now());
        timeEntry.setUpdatedAt(Instant.now());

        TimeEntryEntity entity = timeEntryMapper.toEntity(timeEntry);
        TimeEntryEntity saved = timeEntryRepository.save(entity);

        log.info("Successfully created time entry for task {}", taskId);

        return timeEntryMapper.toDomain(saved);
    }

    @Override
    public List<TimeEntry> getTimeEntries(UUID taskId) {

        log.info("Fetching time entries for task {}", taskId);

        List<TimeEntryEntity> entities = timeEntryRepository.findByTaskId(taskId);

        return entities.stream()
                .map(timeEntryMapper::toDomain)
                .toList();
    }

    @Override
    public TimeEntry getTimeEntryById(UUID id) {

        log.info("Fetching time entry with id {}", id);

        TimeEntryEntity entity = timeEntryRepository.findById(id)
                .orElseThrow(() -> new TimeEntryNotFoundException(id));

        return timeEntryMapper.toDomain(entity);
    }

    @Override
    public TimeEntry updateTimeEntry(UUID id, TimeEntry timeEntry) {

        log.info("Attempting to update time entry with id {}", id);

        Optional<TimeEntryEntity> optionalEntry = timeEntryRepository.findById(id);

        if (optionalEntry.isEmpty()) {
            throw new TimeEntryNotFoundException(id);
        }

        TimeEntryEntity entity = optionalEntry.get();

        // Validate user
        if (!userRepository.existsById(timeEntry.getUserId())) {
            throw new UserNotFoundException(timeEntry.getUserId());
        }

        // Validate time
        if (timeEntry.getStartTime() == null || timeEntry.getEndTime() == null) {
            throw new InvalidTimeEntryException("StartTime or EndTime is null");
        }

        if (timeEntry.getEndTime().isBefore(timeEntry.getStartTime())) {
            throw new InvalidTimeEntryException("EndTime cannot be before StartTime");
        }

        long durationSeconds = Duration.between(timeEntry.getStartTime(), timeEntry.getEndTime()).getSeconds();

        entity.setUserId(timeEntry.getUserId());
        entity.setStartTime(timeEntry.getStartTime());
        entity.setEndTime(timeEntry.getEndTime());
        entity.setDurationSeconds(durationSeconds);
        entity.setUpdatedAt(Instant.now());

        TimeEntryEntity saved = timeEntryRepository.save(entity);

        log.info("Successfully updated time entry with id {}", id);

        return timeEntryMapper.toDomain(saved);
    }

    @Override
    public void deleteTimeEntry(UUID id) {

        log.info("Attempting to delete time entry with id {}", id);

        if (!timeEntryRepository.existsById(id)) {
            throw new TimeEntryNotFoundException(id);
        }

        timeEntryRepository.deleteById(id);

        log.info("Successfully deleted time entry with id {}", id);
    }
}
