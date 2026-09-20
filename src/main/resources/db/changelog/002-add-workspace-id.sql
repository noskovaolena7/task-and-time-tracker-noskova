--liquibase formatted sql
--changeset Olena:002

ALTER TABLE users
    ADD COLUMN workspace_id UUID;
