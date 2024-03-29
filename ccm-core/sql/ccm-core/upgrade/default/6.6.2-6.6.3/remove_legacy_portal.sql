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
-- $Id: remove_legacy_portal.sql pboy $

-- delete core portals legacy entry in apm_package_types
-- there is no entry in application_types bot package_type_id and there are
-- no entries for instances in apm_packages / sitenodes / APPLICATIONS nor
-- there is any entry in acs_objects!

delete  from apm_package_types
        where package_key like 'portal' ;