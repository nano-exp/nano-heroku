CREATE TABLE IF NOT EXISTS nano_task
(
    id             SERIAL PRIMARY KEY,
    name           VARCHAR NOT NULL,
    description    VARCHAR     DEFAULT '',
    options        JSONB       DEFAULT '{}',
    status         VARCHAR     DEFAULT 'CREATED',
    start_time     TIMESTAMPTZ DEFAULT NULL,
    end_time       TIMESTAMPTZ DEFAULT NULL,
    creation_owner VARCHAR     DEFAULT NULL,
    creation_time  TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON COLUMN nano_task.status IS 'CREATED,RUNNING,DONE,ERROR';
CREATE INDEX index_nano_task_status on nano_task (status);