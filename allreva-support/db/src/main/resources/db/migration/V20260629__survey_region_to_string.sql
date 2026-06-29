ALTER TABLE survey DROP CONSTRAINT IF EXISTS survey_region_check;

ALTER TABLE survey
    ALTER COLUMN region TYPE varchar(255)
    USING CASE region
        WHEN 0 THEN '서울'
        WHEN 1 THEN '경기'
        WHEN 2 THEN '인천'
        WHEN 3 THEN '강원'
        WHEN 4 THEN '세종'
        WHEN 5 THEN '천안'
        WHEN 6 THEN '청주'
        WHEN 7 THEN '대전'
        WHEN 8 THEN '대구'
        WHEN 9 THEN '경북'
        WHEN 10 THEN '부산'
        WHEN 11 THEN '울산'
        WHEN 12 THEN '마산'
        WHEN 13 THEN '창원'
        WHEN 14 THEN '경남'
        WHEN 15 THEN '광주'
        WHEN 16 THEN '전북'
        WHEN 17 THEN '전주'
        WHEN 18 THEN '전남'
    END;
