--liquibase formatted sql
--changeset Olena:009-rebuild-schema

------------------------------------------------------------
-- DROP ALL TABLES
------------------------------------------------------------
DROP TABLE IF EXISTS task_reminders CASCADE;
DROP TABLE IF EXISTS project_deadlines CASCADE;
DROP TABLE IF EXISTS attachments CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS task_history CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS time_entries CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS project_members CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS invites CASCADE;
DROP TABLE IF EXISTS user_company_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS companies CASCADE;
DROP TABLE IF EXISTS workspaces CASCADE;

------------------------------------------------------------
-- CREATE TABLES (NO FOREIGN KEYS)
------------------------------------------------------------

CREATE TABLE workspaces (
                            id UUID PRIMARY KEY,
                            name TEXT NOT NULL,
                            owner_id UUID,
                            company_id UUID,
                            created_at TIMESTAMPTZ NOT NULL,
                            updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE companies (
                           id UUID PRIMARY KEY,
                           name TEXT NOT NULL,
                           owner_id UUID,
                           workspace_id UUID,
                           created_at TIMESTAMPTZ NOT NULL,
                           updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name TEXT NOT NULL,
                       last_name TEXT NOT NULL,
                       email TEXT NOT NULL UNIQUE,
                       password TEXT NOT NULL,
                       phone_number TEXT NOT NULL UNIQUE,
                       status TEXT NOT NULL DEFAULT 'ACTIVE',
                       role TEXT NOT NULL DEFAULT 'USER',
                       workspace_id UUID,
                       company_id UUID,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_company_roles (
                                    id UUID PRIMARY KEY,
                                    user_id UUID NOT NULL,
                                    company_id UUID NOT NULL,
                                    role TEXT NOT NULL,
                                    created_at TIMESTAMPTZ NOT NULL,
                                    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE invites (
                         id UUID PRIMARY KEY,
                         company_id UUID,
                         code TEXT NOT NULL,
                         expires_at TIMESTAMPTZ NOT NULL,
                         role TEXT NOT NULL
);

CREATE TABLE projects (
                          id UUID PRIMARY KEY,
                          company_id UUID NOT NULL,
                          name TEXT NOT NULL,
                          description TEXT NOT NULL,
                          created_by UUID NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE project_members (
                                 id UUID PRIMARY KEY,
                                 project_id UUID NOT NULL,
                                 user_id UUID NOT NULL,
                                 member_role TEXT NOT NULL,
                                 created_at TIMESTAMPTZ NOT NULL,
                                 updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE tasks (
                       id UUID PRIMARY KEY,
                       project_id UUID NOT NULL,
                       title TEXT NOT NULL,
                       description TEXT NOT NULL,
                       status TEXT NOT NULL,
                       priority TEXT NOT NULL,
                       created_by UUID NOT NULL,
                       assigned_to UUID,
                       due_date TIMESTAMPTZ,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ NOT NULL,
                       completed_at TIMESTAMPTZ
);

CREATE TABLE time_entries (
                              id UUID PRIMARY KEY,
                              task_id UUID NOT NULL,
                              user_id UUID NOT NULL,
                              start_time TIMESTAMPTZ NOT NULL,
                              end_time TIMESTAMPTZ,
                              duration_seconds INT,
                              created_at TIMESTAMPTZ NOT NULL,
                              updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE comments (
                          id UUID PRIMARY KEY,
                          task_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          message TEXT NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE task_history (
                              id UUID PRIMARY KEY,
                              task_id UUID NOT NULL,
                              user_id UUID NOT NULL,
                              field_changed TEXT NOT NULL,
                              old_value TEXT,
                              new_value TEXT,
                              changed_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               project_id UUID,
                               task_id UUID,
                               type TEXT NOT NULL,
                               message TEXT NOT NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               scheduled_at TIMESTAMPTZ,
                               sent_at TIMESTAMPTZ,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE attachments (
                             id UUID PRIMARY KEY,
                             project_id UUID NOT NULL,
                             task_id UUID,
                             file_name TEXT NOT NULL,
                             file_url TEXT NOT NULL,
                             owner_id UUID,
                             created_at TIMESTAMPTZ NOT NULL,
                             updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE project_deadlines (
                                   id UUID PRIMARY KEY,
                                   project_id UUID NOT NULL,
                                   deadline TIMESTAMPTZ NOT NULL,
                                   reminder_periods TEXT[] NOT NULL,
                                   created_by UUID NOT NULL,
                                   created_at TIMESTAMPTZ NOT NULL,
                                   updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE task_reminders (
                                id UUID PRIMARY KEY,
                                task_id UUID NOT NULL,
                                reminder_periods TEXT[] NOT NULL,
                                created_by UUID NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL,
                                updated_at TIMESTAMPTZ NOT NULL
);

------------------------------------------------------------
-- ADD FOREIGN KEYS (AFTER ALL TABLES EXIST)
------------------------------------------------------------

ALTER TABLE workspaces
    ADD CONSTRAINT fk_workspaces_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    ADD CONSTRAINT fk_workspaces_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE companies
    ADD CONSTRAINT fk_companies_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    ADD CONSTRAINT fk_companies_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id);

ALTER TABLE users
    ADD CONSTRAINT fk_users_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    ADD CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE user_company_roles
    ADD CONSTRAINT fk_ucr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_ucr_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;

ALTER TABLE invites
    ADD CONSTRAINT fk_invites_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE project_members
    ADD CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    ADD CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE time_entries
    ADD CONSTRAINT fk_te_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_te_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE task_history
    ADD CONSTRAINT fk_th_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_th_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_notif_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_notif_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE attachments
    ADD CONSTRAINT fk_att_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_att_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_att_owner FOREIGN KEY (owner_id) REFERENCES users(id);

ALTER TABLE project_deadlines
    ADD CONSTRAINT fk_pd_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_pd_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE task_reminders
    ADD CONSTRAINT fk_tr_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_tr_created_by FOREIGN KEY (created_by) REFERENCES users(id);
