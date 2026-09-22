--liquibase formatted sql
--changeset Olena:004

CREATE TABLE IF NOT EXISTS invites (

    id UUID PRIMARY KEY,
    company_id UUID,
    code TEXT,
    expires_at TIMESTAMP,
    role TEXT
);
