package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidTimeEntryException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TimeEntryNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TimeEntryMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.TimeEntryRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TimeEntryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TimeEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest {

    @Mock
    private TimeEntryRepository repo;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimeEntryMapper mapper;

    @InjectMocks
    private TimeEntryServiceImpl service;

    @Test
    void delete_nonExisting_throws() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);
        assertThrows(TimeEntryNotFoundException.class, () -> service.deleteTimeEntry(id));
    }

    @Test
    void delete_existing_success() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);
        service.deleteTimeEntry(id);
        verify(repo).deleteById(id);
    }

    @Test
    void createTimeEntry_taskNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        TimeEntry te = new TimeEntry();
        assertThrows(TaskNotFoundException.class, () -> service.createTimeEntry(taskId, te));
    }

    @Test
    void createTimeEntry_invalidTimes_throws() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);

        TimeEntry te = new TimeEntry();
        te.setUserId(userId);
        te.setStartTime(Instant.now());
        te.setEndTime(Instant.now().minusSeconds(100)); // end before start

        assertThrows(InvalidTimeEntryException.class, () -> service.createTimeEntry(taskId, te));
    }

    @Test
    void createTimeEntry_success() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);

        TimeEntry te = new TimeEntry();
        te.setUserId(userId);
        te.setStartTime(Instant.now().minusSeconds(3600));
        te.setEndTime(Instant.now());

        TimeEntryEntity entity = new TimeEntryEntity();
        TimeEntryEntity saved = new TimeEntryEntity();
        TimeEntry domain = new TimeEntry();
        domain.setId(UUID.randomUUID());

        when(mapper.toEntity(te)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(domain);

        TimeEntry res = service.createTimeEntry(taskId, te);
        assertNotNull(res.getId());
    }

    @Test
    void getTimeEntries_returnsList() {
        UUID taskId = UUID.randomUUID();
        TimeEntryEntity entity = new TimeEntryEntity();
        TimeEntry domain = new TimeEntry();

        when(repo.findByTaskId(taskId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<TimeEntry> list = service.getTimeEntries(taskId);
        assertEquals(1, list.size());
    }

    @Test
    void getTimeEntryById_found() {
        UUID id = UUID.randomUUID();
        TimeEntryEntity entity = new TimeEntryEntity();
        TimeEntry domain = new TimeEntry();
        domain.setId(id);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        TimeEntry res = service.getTimeEntryById(id);
        assertEquals(id, res.getId());
    }
}
