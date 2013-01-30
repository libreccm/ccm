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
-- $Id: insert-vcx_java_classes.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

-- NOTE: this needs to be kept in sync with
-- com.arsdigita.x.versioning.serialization.Types

insert into vcx_java_classes (id, name) 
values (0, 'java.lang.Void');

insert into vcx_java_classes (id, name) 
values (1, 'java.math.BigDecimal');

insert into vcx_java_classes (id, name) 
values (2, 'java.math.BigInteger');

insert into vcx_java_classes (id, name) 
values (3, 'not.implemented.Blob');

insert into vcx_java_classes (id, name) 
values (4, 'java.lang.Boolean');

insert into vcx_java_classes (id, name) 
values (5, 'java.lang.Byte');

insert into vcx_java_classes (id, name) 
values (6, 'java.lang.Character');

insert into vcx_java_classes (id, name) 
values (7, 'java.util.Date');

insert into vcx_java_classes (id, name) 
values (8, 'java.lang.Double');

insert into vcx_java_classes (id, name) 
values (9, 'java.lang.Float');

insert into vcx_java_classes (id, name) 
values (10, 'java.lang.Integer');

insert into vcx_java_classes (id, name) 
values (11, 'java.lang.Long');

insert into vcx_java_classes (id, name) 
values (12, 'com.arsdigita.persistence.OID');

insert into vcx_java_classes (id, name) 
values (13, 'java.lang.Short');

insert into vcx_java_classes (id, name) 
values (14, 'java.lang.String');

insert into vcx_java_classes (id, name) 
values (15, 'java.sql.Timestamp');
