--
-- Copyright (C) chris.gilbert@westsussex.gov.uk All Rights Reserved.
-- Copyright (C) pb@zes.uni-bremen.de            All Rights Reserved.
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

\echo APLAWS ccm-forum 6.5.0 -> 6.5.1 Upgrade Script (PostgreSQL)
 
begin;

\i ./postgres/6.5.0-6.5.1/add_privileges.sql
\i ./postgres/6.5.0-6.5.1/add_groups.sql
\i ./postgres/6.5.0-6.5.1/add_thread_subscriber.sql
\i ./postgres/6.5.0-6.5.1/add_file_attachments.sql
\i ./postgres/6.5.0-6.5.1/add_forum_introduction.sql
\i ./postgres/6.5.0-6.5.1/add_image_uploads.sql
\i ./postgres/6.5.0-6.5.1/add_forum_no_category_posts.sql
\i ./postgres/6.5.0-6.5.1/add_anonymous_option.sql

commit;



