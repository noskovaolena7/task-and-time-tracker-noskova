--liquibase formatted sql
--changeset Olena:013-workspace-survives-company

------------------------------------------------------------
-- A workspace lives until explicitly deleted: removing a company
-- only unlinks its workspace (company_id -> NULL) instead of
-- failing on the FK or cascading the delete.
------------------------------------------------------------

ALTER TABLE workspaces DROP CONSTRAINT IF EXISTS fk_workspaces_company;
ALTER TABLE workspaces
    ADD CONSTRAINT fk_workspaces_company FOREIGN KEY (company_id)
    REFERENCES companies(id) ON DELETE SET NULL;
