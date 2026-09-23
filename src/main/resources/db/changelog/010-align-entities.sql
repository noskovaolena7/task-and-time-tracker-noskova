--liquibase formatted sql
--changeset Olena:010-align-entities

------------------------------------------------------------
-- Align the schema produced by 009-rebuild-schema with the
-- JPA entities. All statements are idempotent (IF NOT EXISTS /
-- DROP NOT NULL) so the changeset is safe on already-migrated
-- databases.
------------------------------------------------------------

-- B1: CompanyEntity.description
ALTER TABLE companies ADD COLUMN IF NOT EXISTS description TEXT;

-- B2: WorkspaceEntity.type (PERSONAL / COMPANY)
ALTER TABLE workspaces ADD COLUMN IF NOT EXISTS type TEXT;

-- B6: NotificationEntity.status (SENT / DELIVERED / READ / FAILED)
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS status TEXT;

-- B7: AttachmentEntity.uploadedBy / uploadedAt (009 has owner_id / created_at)
ALTER TABLE attachments ADD COLUMN IF NOT EXISTS uploaded_by UUID;
ALTER TABLE attachments ADD COLUMN IF NOT EXISTS uploaded_at TIMESTAMPTZ;
UPDATE attachments SET uploaded_at = COALESCE(created_at, updated_at) WHERE uploaded_at IS NULL;
UPDATE attachments SET uploaded_by = owner_id WHERE uploaded_by IS NULL;

-- B8: TaskReminderEntity.remindAt / message; reminder_periods no longer required
ALTER TABLE task_reminders ADD COLUMN IF NOT EXISTS remind_at TIMESTAMPTZ;
ALTER TABLE task_reminders ADD COLUMN IF NOT EXISTS message TEXT;
ALTER TABLE task_reminders ALTER COLUMN reminder_periods DROP NOT NULL;

-- B9: ProjectDeadlineEntity.title
ALTER TABLE project_deadlines ADD COLUMN IF NOT EXISTS title TEXT;

-- B12/B13: description is optional in the API/DTOs
ALTER TABLE tasks ALTER COLUMN description DROP NOT NULL;
ALTER TABLE projects ALTER COLUMN description DROP NOT NULL;
