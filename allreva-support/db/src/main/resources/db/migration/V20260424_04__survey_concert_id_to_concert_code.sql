ALTER TABLE public.survey DROP COLUMN concert_id;
ALTER TABLE public.survey ADD COLUMN concert_code VARCHAR(255) NOT NULL;
