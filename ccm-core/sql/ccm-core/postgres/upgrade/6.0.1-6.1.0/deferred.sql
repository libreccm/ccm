--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: deferred.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
alter table admin_app add
    constraint admi_app_applicatio_id_f_e0r3b foreign key (application_id)
      references applications(application_id);
alter table agentportlets add
    constraint agentport_superport_id_f_9z3yn foreign key (superportlet_id)
      references portlets(portlet_id);
alter table agentportlets add
    constraint agentportlet_portle_id_f_vi1h4 foreign key (portlet_id)
      references portlets(portlet_id);
alter table init_requirements add
    constraint init_require_requ_init_f_i6rgg foreign key (required_init)
      references inits(class_name);
alter table init_requirements add
    constraint init_requirements_init_f_cmmdn foreign key (init)
      references inits(class_name);
alter table lucene_ids add
    constraint lucene_ids_host_id_f_fcxb8 foreign key (host_id)
      references web_hosts(host_id);
alter table forms_lstnr_rmt_svr_post add
    constraint form_lst_rmt_svr_pos_l_f_59l2x foreign key (listener_id)
      references bebop_process_listeners(listener_id);
alter table sitemap_app add
    constraint sitem_app_applicati_id_f_echq6 foreign key (application_id)
      references applications(application_id);
