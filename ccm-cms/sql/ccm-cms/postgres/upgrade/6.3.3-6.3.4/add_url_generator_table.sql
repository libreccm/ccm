--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: add_url_generator_table.sql,v 1.1 2006/06/08 14:28:12 awux7820 Exp $
-- $DateTime: 2004/04/07 16:07:11 $

create table cms_task_url_generators 
	   (generator_id integer NOT NULL,
	    task_type_id integer,
	    event VARCHAR(100),
		classname VARCHAR (128),
		CONSTRAINT cms_task_url_generator_pk PRIMARY KEY (generator_id),
		CONSTRAINT cms_url_gen_task_type_fk FOREIGN KEY (task_type_id) REFERENCES cms_task_types (task_type_id));
	
