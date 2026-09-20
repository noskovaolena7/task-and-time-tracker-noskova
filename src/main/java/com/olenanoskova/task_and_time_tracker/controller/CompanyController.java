package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.CompanyMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.CompanyService;
import com.olenanoskova.task_and_time_tracker.service.model.Company;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyResponseDto> createCompany(
            @Valid @RequestBody CompanyCreateRequestDto request) {

        Company company = companyMapper.toDomain(request);
        Company createdCompany = companyService.createCompany(company);
        CompanyResponseDto response = companyMapper.toDto(createdCompany);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@securityService.canListUsers()")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {

        List<Company> companies = companyService.getCompanies();
        List<CompanyResponseDto> responseList = companies.stream()
                .map(companyMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessUser(#id)")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable UUID id) {

        Company company = companyService.getCompanyById(id);
        CompanyResponseDto response = companyMapper.toDto(company);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.hasCompanyRole(#id, securityService.getCurrentUserId(), 'OWNER')")
    public ResponseEntity<CompanyResponseDto> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyUpdateRequestDto request) {

        Company company = companyService.getCompanyById(id);
        companyMapper.updateDomain(request, company);
        Company updated = companyService.updateCompany(id, company);
        CompanyResponseDto response = companyMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.hasCompanyRole(#id, securityService.getCurrentUserId(), 'OWNER')")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
