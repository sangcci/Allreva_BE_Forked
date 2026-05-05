-- flyway:executeInTransaction=false
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_concert_title_trgm ON concert USING gin (title gin_trgm_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_concert_hall_address_trgm ON concert_hall USING gin (address gin_trgm_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_rent_title_trgm ON rent USING gin (title gin_trgm_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_survey_title_trgm ON survey USING gin (title gin_trgm_ops);
