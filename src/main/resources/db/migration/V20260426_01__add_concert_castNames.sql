ALTER TABLE concert
    ADD COLUMN cast_names jsonb NOT NULL DEFAULT '[]';
