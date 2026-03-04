-- pg_trgm 확장 활성화
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 검색 GIN 인덱스
CREATE INDEX IF NOT EXISTS idx_concert_title_trgm
    ON concert USING GIN (title gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_survey_title_trgm
    ON survey USING GIN (title gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_rent_title_trgm
    ON rent USING GIN (title gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_concert_hall_address_trgm
    ON concert_hall USING GIN (address gin_trgm_ops);
