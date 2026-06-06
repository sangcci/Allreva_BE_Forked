ALTER TABLE public.rent DROP COLUMN concert_id;
ALTER TABLE public.rent ADD COLUMN concert_code VARCHAR(255) NOT NULL;
