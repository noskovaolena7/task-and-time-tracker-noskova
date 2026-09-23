--liquibase formatted sql
--changeset Olena:015-creators-set-null

------------------------------------------------------------
-- Company content outlives its authors: when a user is deleted,
-- created_by on company tasks/deadlines/reminders becomes NULL
-- ("author deleted") instead of blocking the delete.
-- Personal projects are deleted by the service beforehand.
------------------------------------------------------------

ALTER TABLE tasks ALTER COLUMN created_by DROP NOT NULL;
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS fk_tasks_created_by;
ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by)
    REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE project_deadlines ALTER COLUMN created_by DROP NOT NULL;
ALTER TABLE project_deadlines DROP CONSTRAINT IF EXISTS fk_pd_created_by;
ALTER TABLE project_deadlines
    ADD CONSTRAINT fk_pd_created_by FOREIGN KEY (created_by)
    REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE task_reminders ALTER COLUMN created_by DROP NOT NULL;
ALTER TABLE task_reminders DROP CONSTRAINT IF EXISTS fk_tr_created_by;
ALTER TABLE task_reminders
    ADD CONSTRAINT fk_tr_created_by FOREIGN KEY (created_by)
    REFERENCES users(id) ON DELETE SET NULL;
