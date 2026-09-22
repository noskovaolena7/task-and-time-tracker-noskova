package com.olenanoskova.task_and_time_tracker.service;import com.olenanoskova.task_and_time_tracker.repository.WorkspaceRepository;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import java.util.Optional;import java.util.UUID;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    WorkspaceRepository repo;

    @Mock
    Object mapper;

    @InjectMocks
    WorkspaceServiceImpl service;

    @Test
    void smoke() {
        assertNotNull(service);
    }
}

