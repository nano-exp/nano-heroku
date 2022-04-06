CREATE TABLE IF NOT EXISTS nano_token
(
    id               SERIAL PRIMARY KEY,
    token            VARCHAR UNIQUE,
    name             VARCHAR,
    chat_id          BIGINT,
    user_id          BIGINT,
    status           VARCHAR     DEFAULT 'VALID',
    privilege        JSONB       DEFAULT '[]',
    last_active_time TIMESTAMPTZ,
    creation_time    TIMESTAMPTZ DEFAULT NOW()
);

COMMENT ON COLUMN nano_token.status IS 'VALID,INVALID,VERIFYING:{username}:{code}';