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

ALTER TABLE forum_forums ADD is_public CHAR(1);

UPDATE forum_forums SET is_public = '1';

ALTER TABLE forum_forums MODIFY (is_public CHAR(1) NOT NULL);

ALTER TABLE forum_forums 
ADD CONSTRAINT forum_forums_is_public_c_284tm check(is_public in ('0', '1'));



