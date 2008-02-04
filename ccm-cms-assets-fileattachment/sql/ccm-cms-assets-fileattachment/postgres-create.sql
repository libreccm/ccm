--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: postgres-create.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/03/30 18:21:14 $
begin;
\i ddl/postgres/create.sql
\i default/index-ca_file_attchmnts_ownr_idx.sql
\i ddl/postgres/deferred.sql
commit;
