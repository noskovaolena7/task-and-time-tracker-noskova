package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    NotificationMapper mapper;

    @Test
    void smoke() {
        assertNotNull(mapper);
    }

    @Test
    void toDomain_resolvesSenderName() {
        UUID senderId = UUID.randomUUID();
        var entity = new com.olenanoskova.task_and_time_tracker.repository.entity.NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setSenderId(senderId);
        var sender = new com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity();
        sender.setId(senderId);
        sender.setFirstName("Ben");
        sender.setLastName("Muster");
        sender.setEmail("ben@example.com");

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));

        var domain = mapper.toDomain(entity);

        assertEquals(senderId, domain.getSenderId());
        assertEquals("Ben Muster", domain.getSenderName());
        assertEquals("ben@example.com", domain.getSenderEmail());
    }
}
