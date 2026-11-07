-- Existing identities retain their data and start with no login until explicitly provisioned.
ALTER TABLE users
    ADD COLUMN username VARCHAR(255),
    ADD COLUMN password_hash VARCHAR(255),
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD CONSTRAINT uk_users_username UNIQUE (username),
    ADD CONSTRAINT chk_user_login_pair CHECK
        ((username IS NULL AND password_hash IS NULL) OR (username IS NOT NULL AND password_hash IS NOT NULL)),
    ADD CONSTRAINT chk_user_enabled_credentials CHECK
        (NOT enabled OR (username IS NOT NULL AND password_hash IS NOT NULL)),
    ADD CONSTRAINT chk_user_username CHECK (username IS NULL OR length(trim(username)) > 0);
