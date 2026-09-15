ALTER TABLE users RENAME COLUMN phone_verified_at TO verified_at;
ALTER TABLE users ADD COLUMN line_user_id VARCHAR(255);

CREATE UNIQUE INDEX ux_users_line_user_id ON users (line_user_id) WHERE line_user_id IS NOT NULL;
