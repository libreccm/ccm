--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: comment-ss_surveys.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:26:27 $
comment on table ss_surveys is '
 Every entry in this table represents a Simple Survey. It is yet unclear which
 attributes we need for the surveys but they would be stored in this table.
';
comment on column ss_surveys.form_id is '
 Every Survey is associated with one persistent form. Currently only one persistent
 form can be associated.
';
