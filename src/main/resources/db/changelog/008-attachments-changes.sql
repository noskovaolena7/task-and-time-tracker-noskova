--liquibase formatted sql

--changeset Olena:attachments-001
ALTER TABLE attachments
    ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES projects(id);