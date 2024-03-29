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
alter table application_types
add    container_group_id integer;

alter table applications 
add   container_group_id integer;

alter table application_types add
    constraint applic_typ_cont_gro_id_f_lszuh foreign key (container_group_id)
      references groups(group_id);

alter table applications add
    constraint applicat_contai_gro_id_f_kc1gi foreign key (container_group_id)
      references groups(group_id);
