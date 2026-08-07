CREATE TABLE Users
(

    id           UUID PRIMARY KEY,
    first_name   TEXT        NOT NULL,
    last_name    TEXT        NOT NULL,
    email        TEXT        NOT NULL UNIQUE,
    password     TEXT        NOT NULL,
    phone_number TEXT        NOT NULL UNIQUE,
    role         TEXT        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL

);

CREATE TABLE Projects
(
    id          UUID PRIMARY KEY,
    name        TEXT        NOT NULL,
    description TEXT        NOT NULL,
    created_by  UUID        NOT NULL REFERENCES users (id),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE Tasks
(
    id           UUID PRIMARY KEY,
    project_id   UUID        NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    title        TEXT        NOT NULL,
    description  TEXT        NOT NULL,
    status       TEXT        NOT NULL,
    priority     TEXT        NOT NULL,
    created_by   UUID        NOT NULL REFERENCES users (id),
    assigned_to  UUID        REFERENCES users (id),
    due_date     TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ
);

CREATE TABLE Time_entries
(
    id               UUID PRIMARY KEY,
    task_id          UUID        NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    user_id          UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    start_time       TIMESTAMPTZ NOT NULL,
    end_time         TIMESTAMPTZ,
    duration_seconds INT,
    created_at       TIMESTAMPTZ NOT NULL
);

CREATE TABLE Comments
(
    id         UUID PRIMARY KEY,
    task_id    UUID        NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE Task_history
(
    id            UUID PRIMARY KEY,
    task_id       UUID        NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    user_id       UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    field_changed TEXT        NOT NULL,
    old_value     TEXT,
    new_value     TEXT,
    changed_at    TIMESTAMPTZ NOT NULL
);

CREATE TABLE Notifications
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    task_id    UUID        NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    type       TEXT        NOT NULL,
    message    TEXT        NOT NULL,
    is_read    BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE Attachments
(
    id          UUID PRIMARY KEY,
    task_id     UUID        NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    file_url    TEXT        NOT NULL,
    uploaded_by UUID REFERENCES users (id),
    uploaded_at TIMESTAMPTZ NOT NULL
);