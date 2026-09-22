--liquibase formatted sql
--changeset Olena:003

CREATE TABLE IF NOT EXISTS workspaces (
    id UUID PRIMARY KEY,
    name TEXT,
    type TEXT,
    owner_id UUID,
    company_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS workspace_id UUID;
