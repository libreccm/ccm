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
-- $Id: function-convert_to_html.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


create or replace function convert_to_html (
        v_doc_id           in integer
) return varchar
  is
  result   varchar(512);
  err_num  number;
  err_msg  varchar(600);
begin
   -- Make sure nothing in destination table
   delete from post_convert_html where query_id = v_doc_id;
   result := ''; -- assume success
   -- Do conversion
   begin
      ctx_doc.filter('convert_to_html_index', v_doc_id, 
                     'post_convert_html', v_doc_id, FALSE);
   exception
   when others then
	err_num := SQLCODE;
	err_msg := SQLERRM;
        result := 'Error code=' || err_num || ' Error msg=' || err_msg;
   end;
   return result;
end convert_to_html;
/
show errors;
