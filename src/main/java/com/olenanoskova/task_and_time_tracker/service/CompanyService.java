package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Company;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    Company createCompany(Company company);

    Company createCompanyForRegistration(String name, String description);

    List<Company> getCompanies();

    Company getCompanyById(UUID id);

    Company updateCompany(UUID id, Company company);

    void deleteCompany(UUID id);
}

