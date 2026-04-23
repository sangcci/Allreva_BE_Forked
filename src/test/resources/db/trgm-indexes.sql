CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE member
    ADD CONSTRAINT uq_member_email_provider UNIQUE (email, provider);
