CREATE TABLE IF NOT EXISTS note
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    last_changed TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS note_update_log
(
    id           SERIAL PRIMARY KEY,
    note_id      INT          NOT NULL,
    action       VARCHAR(255) NOT NULL,
    changed      TIMESTAMP    NOT NULL,
    new_password VARCHAR(255),
    old_password VARCHAR(255)
);