CREATE TABLE IF NOT EXISTS nano_user
(
    id            BIGINT PRIMARY KEY,
    username      VARCHAR,
    firstname     VARCHAR,
    is_bot        BOOLEAN,
    language_code VARCHAR,
    email         VARCHAR
);