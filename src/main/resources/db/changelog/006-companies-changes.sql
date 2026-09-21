--liquibase formatted sql

--changeset Olena:companies-001
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS owner_id UUID REFERENCES users(id);

--changeset Olena:companies-002
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS workspace_id UUID REFERENCES workspaces(id);
