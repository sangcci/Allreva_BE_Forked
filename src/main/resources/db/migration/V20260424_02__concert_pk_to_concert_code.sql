ALTER TABLE public.concert DROP CONSTRAINT IF EXISTS concert_pkey;
ALTER TABLE public.concert ALTER COLUMN id DROP IDENTITY IF EXISTS;
DROP SEQUENCE IF EXISTS public.concert_id_seq;
ALTER TABLE public.concert DROP COLUMN id;

ALTER TABLE public.concert ADD CONSTRAINT concert_pkey PRIMARY KEY (concert_code);
