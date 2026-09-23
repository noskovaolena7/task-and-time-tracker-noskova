--liquibase formatted sql
--changeset Olena:014-workspace-delete-unlinks

------------------------------------------------------------
-- Deleting a workspace only unlinks it (users/company workspace_id
-- -> NULL) instead of failing on the FK. The workspace itself lives
-- until explicitly deleted.
------------------------------------------------------------

ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_users_workspace;
ALTER TABLE users
    ADD CONSTRAINT fk_users_workspace FOREIGN KEY (workspace_id)
    REFERENCES workspaces(id) ON DELETE SET NULL;

ALTER TABLE companies DROP CONSTRAINT IF EXISTS fk_companies_workspace;
ALTER TABLE companies
    ADD CONSTRAINT fk_companies_workspace FOREIGN KEY (workspace_id)
    REFERENCES workspaces(id) ON DELETE SET NULL;
