--liquibase formatted sql
--changeset Olena:017-invited-by

------------------------------------------------------------
-- Remember who invited whom: invites store the creator,
-- memberships store the inviter. Needed so a USER sees only
-- their inviter and project coworkers (not the whole company).
------------------------------------------------------------

ALTER TABLE invites ADD COLUMN IF NOT EXISTS invited_by UUID;
ALTER TABLE user_company_roles ADD COLUMN IF NOT EXISTS invited_by UUID;
