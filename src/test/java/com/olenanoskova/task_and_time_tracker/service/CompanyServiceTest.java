package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.mapper.CompanyMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    CompanyRepository companyRepository;

    @Mock
    CompanyMapper companyMapper;

    @InjectMocks
    CompanyServiceImpl companyService;

    @Test
    void getCompanyById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> companyService.getCompanyById(id));
    }

    @Test
    void createCompany_success_savesAndReturns() {
        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Company();
        domain.setName("Acme");

        when(companyRepository.findByName("Acme")).thenReturn(Optional.empty());
        when(companyMapper.toEntity(domain)).thenReturn(new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity());
        var saved = new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity();
        saved.setId(UUID.randomUUID());
        when(companyRepository.save(any())).thenReturn(saved);
        when(companyMapper.toDomain(saved)).thenReturn(domain);

        var result = companyService.createCompany(domain);
        assertNotNull(result);
        verify(companyRepository).save(any());
    }

    @Test
    void createCompany_duplicate_throws() {
        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Company();
        domain.setName("Dup");
        when(companyRepository.findByName("Dup")).thenReturn(Optional.of(new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity()));
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.CompanyAlreadyExistException.class,
                () -> companyService.createCompany(domain));
    }

    @Test
    void getCompanies_mapsAll() {
        var e1 = new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity();
        e1.setId(UUID.randomUUID());
        var e2 = new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity();
        e2.setId(UUID.randomUUID());
        when(companyRepository.findAll()).thenReturn(java.util.List.of(e1, e2));
        when(companyMapper.toDomain(e1)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.Company());
        when(companyMapper.toDomain(e2)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.Company());

        var list = companyService.getCompanies();
        assertEquals(2, list.size());
        verify(companyRepository).findAll();
    }

    @Test
    void updateCompany_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException.class,
                () -> companyService.updateCompany(id, new com.olenanoskova.task_and_time_tracker.service.model.Company()));
    }

    @Test
    void deleteCompany_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(companyRepository.existsById(id)).thenReturn(false);
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException.class,
                () -> companyService.deleteCompany(id));
    }
}
