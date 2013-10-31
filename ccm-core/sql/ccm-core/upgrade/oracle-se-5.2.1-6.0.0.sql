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
-- $Id: oracle-se-5.2.1-6.0.0.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

PROMPT Red Hat WAF 5.2.1 -> 6.0.0 Upgrade Script (Oracle)

@@ default/5.2.1-6.0.0/drop-search-test.sql
@@ default/5.2.1-6.0.0/sequence-vcx_id_seq.sql
@@ default/5.2.1-6.0.0/sequence-vcx_txns_id_seq.sql
@@ default/5.2.1-6.0.0/alter-categories.sql
@@ default/5.2.1-6.0.0/table-web_hosts-auto.sql
@@ oracle-se/5.2.1-6.0.0/update-web-hosts.sql
@@ oracle-se/5.2.1-6.0.0/alter-lucene.sql
@@ oracle-se/5.2.1-6.0.0/mime-types.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_blob_operations-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_clob_operations-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_event_types-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_generic_operations-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_java_classes-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_obj_changes-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_operations-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_tags-auto.sql
@@ oracle-se/5.2.1-6.0.0/table-vcx_txns-auto.sql
@@ oracle-se/5.2.1-6.0.0/vcx_deferred.sql
@@ default/5.2.1-6.0.0/insert-vcx_event_types.sql
@@ default/5.2.1-6.0.0/insert-vcx_java_classes.sql
@@ default/5.2.1-6.0.0/add-index-cw_task_listeners_tid_ltid.sql
@@ oracle-se/5.2.1-6.0.0/misc.sql
@@ oracle-se/5.2.1-6.0.0/auto-upgrade.sql
