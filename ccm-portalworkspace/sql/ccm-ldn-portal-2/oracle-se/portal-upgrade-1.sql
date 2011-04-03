
@@ portlet-upgrade-1.sql

delete from acs_objects where object_type = 'com.arsdigita.london.portal.Portlet';
delete from acs_objects where object_type = 'com.arsdigita.london.portal.Portal';
delete from acs_objects where object_id in (select workspace_id from workspaces where party_id != null);

-- The dispatcher stuff
update apm_package_types set dispatcher_class = 'com.arsdigita.dispatcher.JSPApplicationDispatcher' where package_key = 'portal';
