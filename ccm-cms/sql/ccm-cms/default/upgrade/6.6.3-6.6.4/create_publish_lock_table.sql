CREATE TABLE cms_publish_lock (
    lock_id integer NOT NULL,
    locked_oid character varying(2048),
    lock_timestamp timestamp with time zone,
    action character varying(256)
);

ALTER TABLE ONLY cms_publish_lock
    ADD CONSTRAINT cms_publis_loc_lock_id_p_8n7d0 PRIMARY KEY (lock_id);

-- ALTER TABLE public.cms_publish_lock OWNER TO ccm;