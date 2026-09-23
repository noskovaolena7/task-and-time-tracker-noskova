--liquibase formatted sql
--changeset Olena:012-invites-cascade

------------------------------------------------------------
-- Invites are ephemeral join codes: removing a company must
-- remove its invites instead of failing on the FK.
------------------------------------------------------------

ALTER TABLE invites DROP CONSTRAINT IF EXISTS fk_invites_company;
ALTER TABLE invites
    ADD CONSTRAINT fk_invites_company FOREIGN KEY (company_id)
    REFERENCES companies(id) ON DELETE CASCADE;
