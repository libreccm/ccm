--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: add_ispublic.sql pboy $

ALTER TABLE forum_forums ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT true;

-- CREATE TABLE forum_temp AS SELECT * FROM forum_forums ;
-- DROP TABLE forum_forums CASCADE ;

-- CREATE TABLE forum_forums
-- (
--   forum_id INTEGER NOT NULL,
--   is_moderated BOOLEAN NOT NULL,
--   is_noticeboard BOOLEAN NOT NULL,
--   is_public BOOLEAN NOT NULL,
--   admin_group_id INTEGER,
--   mod_group_id INTEGER,
--   create_group_id INTEGER,
--   respond_group_id INTEGER,
--   read_group_id INTEGER,
--   category_id INTEGER NOT NULL,
--   lifecycle_definition_id INTEGER,
--   expire_after NUMERIC,
--   file_attachments_allowed BOOLEAN NOT NULL,
--   image_uploads_allowed BOOLEAN NOT NULL,
--   subscribe_thread_starter BOOLEAN NOT NULL,
--   no_category_posts_allowed BOOLEAN NOT NULL,
--   anonymous_posts_allowed BOOLEAN NOT NULL,
--   introduction CHARACTER VARYING(4000),
--   CONSTRAINT forum_forums_forum_id_p_9opkb PRIMARY KEY (forum_id),
--   CONSTRAINT foru_for_life_defin_id_f_ugal3 FOREIGN KEY (lifecycle_definition_id)
--       REFERENCES lifecycle_definitions (definition_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_foru_admi_grou_id_f_k0nw6 FOREIGN KEY (admin_group_id)
--       REFERENCES groups (group_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_foru_crea_grou_id_f_f7x57 FOREIGN KEY (create_group_id)
--       REFERENCES groups (group_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_foru_respo_gro_id_f_rnofz FOREIGN KEY (respond_group_id)
--       REFERENCES groups (group_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_forum_category_id_f_1u2dw FOREIGN KEY (category_id)
--       REFERENCES cat_categories (category_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_forum_mod_grou_id_f__smmb FOREIGN KEY (mod_group_id)
--       REFERENCES groups (group_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT foru_forum_rea_grou_id_f_itati FOREIGN KEY (read_group_id)
--       REFERENCES groups (group_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT forum_forums_forum_id_f_znjmf FOREIGN KEY (forum_id)
--       REFERENCES applications (application_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION
-- )
-- WITH (
--   OIDS=FALSE
-- );

-- INSERT INTO forum_forums (forum_id,
--                           is_moderated,
--                           is_noticeboard,
--                           is_public,
--                           admin_group_id,
--                           mod_group_id,
--                             create_group_id,
--                           respond_group_id,
--                          read_group_id,category_id,
--                          lifecycle_definition_id,
--                          expire_after,
--                          file_attachments_allowed,
--                          image_uploads_allowed,
--                          subscribe_thread_starter,
--                          no_category_posts_allowed,
--                          anonymous_posts_allowed,
--                          introduction)
--     SELECT forum_id,
--             is_moderated,
--             is_noticeboard,is_noticeboard,
--             admin_group_id,
--             mod_group_id,create_group_id,
--             respond_group_id,
--             read_group_id,category_id,
--             lifecycle_definition_id,
--             expire_after,
--             file_attachments_allowed,
--             image_uploads_allowed,
--             subscribe_thread_starter,
--             no_category_posts_allowed,
--             anonymous_posts_allowed,
--             introduction
--        FROM forum_temp;

-- UPDATE forum_forums SET is_public = TRUE;

-- restore constraint
-- ALTER TABLE forum_subscriptions
--  ADD CONSTRAINT foru_subscripti_for_id_f_xqfd9 FOREIGN KEY (forum_id)
--         REFERENCES forum_forums (forum_id) MATCH SIMPLE
--         ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DROP TABLE forum_temp;




