ALTER TABLE member
    ADD COLUMN status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE member
    ADD CONSTRAINT member_status_check
        CHECK (status IN ('REGISTERED', 'ACTIVE'));
