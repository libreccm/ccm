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
-- $Id: add-description-field.sql,v 1.1.2.1 2005/10/04 12:09:59 cgyg9330 Exp $
-- $DateTime: 2004/08/17 23:15:09 $

-- add a description field at the level of cms_pages that may be used
-- by any content type, unless they choose to continue using their
-- custom version
ALTER TABLE cms_pages 
ADD (description VARCHAR2(4000) );

