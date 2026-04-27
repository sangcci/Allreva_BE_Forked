ALTER TABLE public.seat_review_like DROP CONSTRAINT IF EXISTS seat_review_like_pkey;
ALTER TABLE public.seat_review_image DROP CONSTRAINT IF EXISTS seat_review_image_pkey;
ALTER TABLE public.seat_review DROP CONSTRAINT IF EXISTS seat_review_pkey;

DROP TABLE IF EXISTS public.seat_review_like;
DROP TABLE IF EXISTS public.seat_review_image;
DROP TABLE IF EXISTS public.seat_review;
