--liquibase formatted sql
--changeset Olena:018-notification-sender

------------------------------------------------------------
-- Notifications double as team messages: remember the sender.
------------------------------------------------------------

ALTER TABLE notifications ADD COLUMN IF NOT EXISTS sender_id UUID;
