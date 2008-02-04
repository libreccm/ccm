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
-- $Id: insert-vc_actions.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

  insert into vc_actions (action, description) 
  values (
    'create', 'Create a new value for the attribute'
  );

 insert into vc_actions (action, description)
  values (
    'update', 'Update the attribute''s value (for single-valued
attributes)'
  );

  insert into vc_actions (action, description)
  values (
    'add', 'Add a value for a multi-valued attribute'
  );

  insert into vc_actions (action, description)
  values (
    'remove', 'Remove a value from a multi-valued attribute'
  );

  insert into vc_actions (action, description)
  values (
    'create_content', 'Create new content for the object'
  );

  insert into vc_actions (action, description)
  values (
    'update_content', 'Modify existing content for the object'
  );
