--liquibase formatted sql

--changeset Olena:users-001
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS status TEXT NOT NULL DEFAULT 'ACTIVE';

--changeset Olena:users-002
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role TEXT NOT NULL DEFAULT 'USER';

--changeset Olena:users-003
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS workspace_id UUID;

--changeset Olena:users-004
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS company_id UUID;
