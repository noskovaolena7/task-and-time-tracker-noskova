--liquibase formatted sql
--changeset Olena:011-personal-projects

------------------------------------------------------------
-- Personal workspaces: projects may exist without a company.
-- Such projects belong to their creator (projects.created_by).
------------------------------------------------------------

ALTER TABLE projects ALTER COLUMN company_id DROP NOT NULL;
