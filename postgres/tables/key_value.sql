CREATE TABLE IF NOT EXISTS key_value
(
    id                SERIAL PRIMARY KEY,
    key               VARCHAR UNIQUE NOT NULL,
    value             JSONB       DEFAULT '{}',
    last_updated_time TIMESTAMPTZ,
    creation_time     TIMESTAMPTZ DEFAULT NOW()
);