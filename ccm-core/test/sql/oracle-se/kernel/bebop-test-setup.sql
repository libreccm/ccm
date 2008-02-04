--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: bebop-test-setup.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


declare 
 node_id    number;
 package_id number;
 main_site  number;
 version_id number;
begin
    apm_package_type.create_type (
     PACKAGE_KEY => 'bebop_test',
     PRETTY_NAME => 'Bebop Test Package',
     PRETTY_PLURAL => 'Bebop Test Packages',
     PACKAGE_URI =>  'http://arsdigita.com',
     PACKAGE_TYPE => 'apm_application',
     SINGLETON_P => 'f'
    );

    package_id := apm_package.new (package_key => 'bebop_test');

    apm_package.enable (package_id => package_id);
   
    select node_id into main_site from site_nodes
      where parent_id is null;

    node_id := site_node.new(
       name => 'bebop-test',
       parent_id => main_site,
       directory_p => 't',
       pattern_p => 't',
       object_id => package_id
    );

    version_id := apm_package_version.new(
     package_key => 'bebop_test',
     version_uri => 'http://',
     version_name => '0.1d',
     summary => 'bebop test package', 
     description_format => 'text/plain',
     description => 'bebop test package', 
     release_date => sysdate,
     vendor => 'arsdigita',
     vendor_uri => 'arsdigita.com', 
     dispatcher_class => 'com.arsdigita.bebop.demo.BebopTestDispatcher'
    );

    apm_package_version.enable (version_id => version_id);

end;
/
show errors
