--
-- Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
-- $Id: oracle-se-6.3.1-6.3.2.sql 293 2005-02-22 15:10:39Z cgilbert $
-- $DateTime: 2004/08/16 18:10:38 $

PROMPT Red Hat Enterprise CMS 6.5.0 -> 6.5.1 Upgrade Script (Oracle)

@@ ../oracle-se/upgrade/6.5.0-6.5.1/drop-xml-index.sql

begin
        ctx_ddl.drop_section_group('autogroup');
        ctx_ddl.create_section_group('pathgroup', 'PATH_SECTION_GROUP');

end;
/
@@ ../../ccm-core/default/search/package-search_indexing.sql
@@ ../../ccm-core/default/search/index-xml_content_index.sql
