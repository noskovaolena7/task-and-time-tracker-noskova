package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Company;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CompanyMapper {

    // DTO → Domain (Create)
    public Company toDomain(@Valid CompanyCreateRequestDto dto) {
        Company company = new Company();
        company.setOwnerId(dto.getOwnerId());
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());
        return company;
    }

    // DTO → Domain (Update)
    public void updateDomain(CompanyUpdateRequestDto dto, Company company) {
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public CompanyEntity toEntity(Company company) {
        CompanyEntity entity = new CompanyEntity();
        entity.setId(company.getId());
        entity.setName(company.getName());
        entity.setDescription(company.getDescription());
        entity.setOwnerId(company.getOwnerId());
        entity.setWorkspaceId(company.getWorkspaceId());
        entity.setCreatedAt(company.getCreatedAt());
        entity.setUpdatedAt(company.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public Company toDomain(CompanyEntity entity) {
        Company company = new Company();
        company.setId(entity.getId());
        company.setName(entity.getName());
        company.setDescription(entity.getDescription());
        company.setOwnerId(entity.getOwnerId());
        company.setWorkspaceId(entity.getWorkspaceId());
        company.setCreatedAt(entity.getCreatedAt());
        company.setUpdatedAt(entity.getUpdatedAt());
        return company;
    }

    // Domain → Response DTO
    public CompanyResponseDto toDto(Company company) {
        CompanyResponseDto dto = new CompanyResponseDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setOwnerId(company.getOwnerId());
        dto.setWorkspaceId(company.getWorkspaceId());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }
}
