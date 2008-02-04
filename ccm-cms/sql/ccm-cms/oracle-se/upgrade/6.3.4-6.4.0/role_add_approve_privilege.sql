--
-- Copyright (C) 2006 Red Hat Inc. All Rights Reserved.
--
-- $Id: add_approve_item_privilege.sql,v 1.1 2006/06/08 14:28:12 awux7820 Exp $

insert into acs_permissions
    (privilege, grantee_id, object_id, creation_date, creation_ip)
  select
    'cms_approve_item', ap.grantee_id, ap.object_id, sysdate, '127.0.0.1'
  from
    acs_permissions ap, roles r
  where
    r.name = 'Editor'
    and r.implicit_group_id = ap.grantee_id
    and ap.privilege = 'cms_edit_item'
    and ap.grantee_id not in (select ap2.grantee_id from
                                acs_permissions ap2
                              where ap2.object_id = ap.object_id
                                and ap2.privilege = 'cms_approve_item' ) ;
