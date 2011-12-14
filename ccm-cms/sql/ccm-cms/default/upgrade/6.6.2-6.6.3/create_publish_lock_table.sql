CREATE TABLE cms_publish_lock (
    lock_id integer NOT NULL,
    locked_oid character varying(2048),
    lock_timestamp timestamp with time zone,
    action character varying(256)
);


-- ALTER TABLE public.cms_publish_lock OWNER TO ccm;