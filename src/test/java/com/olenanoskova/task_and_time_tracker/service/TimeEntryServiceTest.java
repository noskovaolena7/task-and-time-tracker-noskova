package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.TimeEntryRepository;
import com.olenanoskova.task_and_time_tracker.mapper.TimeEntryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest {

    @Mock
    TimeEntryRepository repo;

    @Mock
    TimeEntryMapper mapper;

    @InjectMocks
    TimeEntryServiceImpl service;

    @Test
    void delete_nonExisting_throws() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteTimeEntry(id));
    }
}
