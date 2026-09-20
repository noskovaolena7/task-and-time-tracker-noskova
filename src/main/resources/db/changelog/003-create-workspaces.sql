--liquibase formatted sql
--changeset Olena:003

CREATE TABLE workspaces (
    id UUID PRIMARY KEY,
    name TEXT,
    type TEXT,
    owner_id UUID,
    company_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
