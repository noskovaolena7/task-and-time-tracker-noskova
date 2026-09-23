package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyMemberDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CompanyResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.CompanyMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.CompanyService;
import com.olenanoskova.task_and_time_tracker.service.UserCompanyRoleService;
import com.olenanoskova.task_and_time_tracker.service.model.Company;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
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
    private final UserCompanyRoleService userCompanyRoleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyResponseDto> createCompany(
            @Valid @RequestBody CompanyCreateRequestDto request) {

        Company company = companyMapper.toDomain(request);
        Company createdCompany = companyService.createCompany(company);

        // The declared owner becomes OWNER of the new company, so isolation
        // by company works from the very first request.
        if (request.getOwnerId() != null) {
            UserCompanyRole ownerRole = new UserCompanyRole();
            ownerRole.setUserId(request.getOwnerId());
            ownerRole.setRole(MemberRole.OWNER);
            userCompanyRoleService.assignRole(createdCompany.getId(), ownerRole);
        }

        CompanyResponseDto response = companyMapper.toDto(createdCompany);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {

        // Scoped to own memberships only (see method body), so any
        // authenticated user may list - strangers see nothing.
        List<UUID> companyIds = securityService.getCurrentUserCompanyIds();
        List<Company> companies = companyIds.stream()
                .map(companyService::getCompanyById)
                .toList();
        List<CompanyResponseDto> responseList = companies.stream()
                .map(companyMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessCompany(#id)")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable UUID id) {

        Company company = companyService.getCompanyById(id);
        CompanyResponseDto response = companyMapper.toDto(company);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("@securityService.canAccessCompany(#id)")
    public ResponseEntity<List<CompanyMemberDto>> getMembers(@PathVariable UUID id) {
        UUID requesterId = securityService.getCurrentUserId();
        return ResponseEntity.ok(userCompanyRoleService.getVisibleMembers(id, requesterId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canManageCompany(#id)")
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
    @PreAuthorize("@securityService.canManageCompany(#id)")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
