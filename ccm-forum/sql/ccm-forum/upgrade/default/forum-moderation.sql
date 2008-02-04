--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: forum-moderation.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:26:27 $


alter table forum_forums
add (
is_moderated          integer
                      constraint forum_moderated_nn
                      not null);

alter table forum_subscriptions 
add (
is_moderated                  integer
                              constraint forum_is_moderation_alert_nn 
                              not null);

create table forum_posts (
    post_id INTEGER not null
        constraint forum_posts_post_id_p_7qlj5
          primary key,
        -- referential constraint for post_id deferred due to circular dependencies
    moderator INTEGER,
        -- referential constraint for moderator deferred due to circular dependencies
    status CHAR(20) not null
);

alter table forum_posts add 
    constraint forum_posts_moderator_f_j008r foreign key (moderator)
      references parties(party_id);
alter table forum_posts add 
    constraint forum_posts_post_id_f_x7pw7 foreign key (post_id)
      references messages(message_id) on delete cascade;

-- Fill the new attributes
update forum_forums
       set is_moderated = 0;


insert into forum_posts
(post_id, status)
select message_id, 'approved' 
from  messages m , forum_forums f
where m.object_id = f.forum_id;

