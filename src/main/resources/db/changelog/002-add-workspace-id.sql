--liquibase formatted sql
--changeset Olena:002

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS workspace_id UUID;
