CREATE TABLE IF NOT EXISTS tasks
(
    id              UUID            PRIMARY KEY     NOT NULL,
    original_text   TEXT                            NOT NULL,
    corrected_text  TEXT                            NOT NULL,
    language        VARCHAR(16)                     NOT NULL,
    status          VARCHAR(16)                     NOT NULL,
    error_message   VARCHAR(512),
    created_at      TIMESTAMP WITH TIME ZONE        NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_tasks_status_created_at
    ON tasks (status, created_at);