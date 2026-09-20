package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.CompanyAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.CompanyMapper;
import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public Company createCompany(Company company) {

        log.info("Attempting to create company with name {}", company.getName());

        Optional<CompanyEntity> optionalCompany = companyRepository.findByName(company.getName());
        if (optionalCompany.isPresent()) {
            throw new CompanyAlreadyExistException(company.getName());
        }

        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());

        CompanyEntity entity = companyMapper.toEntity(company);
        CompanyEntity saved = companyRepository.save(entity);

        log.info("Successfully created company with name {}", company.getName());

        return companyMapper.toDomain(saved);
    }

    @Override
    public Company createCompanyForRegistration(String name, String description) {

        log.info("Creating company during registration: {}", name);

        Optional<CompanyEntity> optionalCompany = companyRepository.findByName(name);
        if (optionalCompany.isPresent()) {
            throw new CompanyAlreadyExistException(name);
        }

        Company company = new Company();
        company.setName(name);
        company.setDescription(description);
        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());

        CompanyEntity saved = companyRepository.save(companyMapper.toEntity(company));

        log.info("Company created for registration: {}", name);

        return companyMapper.toDomain(saved);
    }

    @Override
    public List<Company> getCompanies() {

        log.info("Fetching all companies");

        List<CompanyEntity> entities = companyRepository.findAll();

        return entities.stream()
                .map(companyMapper::toDomain)
                .toList();
    }

    @Override
    public Company getCompanyById(UUID id) {

        log.info("Fetching company with id {}", id);

        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        return companyMapper.toDomain(entity);
    }

    @Override
    public Company updateCompany(UUID id, Company company) {

        log.info("Attempting to update company with id {}", id);

        Optional<CompanyEntity> optionalCompany = companyRepository.findById(id);

        if (optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException(id);
        }

        CompanyEntity entity = optionalCompany.get();
        entity.setName(company.getName());
        entity.setDescription(company.getDescription());
        entity.setUpdatedAt(Instant.now());

        CompanyEntity saved = companyRepository.save(entity);

        log.info("Successfully updated company with id {}", id);

        return companyMapper.toDomain(saved);
    }

    @Override
    public void deleteCompany(UUID id) {

        log.info("Attempting to delete company with id {}", id);

        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException(id);
        }

        companyRepository.deleteById(id);

        log.info("Successfully deleted company with id {}", id);
    }
}

