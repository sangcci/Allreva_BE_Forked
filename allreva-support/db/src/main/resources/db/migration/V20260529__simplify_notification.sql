ALTER TABLE public.notification DROP CONSTRAINT IF EXISTS notification_type_check;

ALTER TABLE public.notification DROP COLUMN IF EXISTS sender_name;
ALTER TABLE public.notification RENAME COLUMN room_id TO resource_id;
ALTER TABLE public.notification RENAME COLUMN room_name TO resource_name;

ALTER TABLE public.notification
    ADD CONSTRAINT notification_type_check
        CHECK (((type)::text = ANY ((ARRAY[
            'RENT_PARTICIPANT_JOINED'::character varying,
            'SURVEY_PARTICIPANT_JOINED'::character varying
        ])::text[])));
