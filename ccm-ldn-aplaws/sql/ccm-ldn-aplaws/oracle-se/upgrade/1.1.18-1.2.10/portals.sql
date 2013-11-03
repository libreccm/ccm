-- create the appilcation types

-- first, we populate the application_types table with the appropriate
-- portal types

declare
    new_type_id integer;
begin
    select acs_object_id_seq.nextval into new_type_id from dual;

    update apm_package_types 
       set package_key = 'workspace',
           pretty_name = 'Portal Workspace',
           pretty_plural = null,
           dispatcher_class = 'com.arsdigita.london.portal.dispatcher.JSPDispatcher',
           package_uri = 'http://arsdigita.com/workspace'
      where package_key = 'portal';

    insert into application_types (application_type_id, description, has_embedded_view_p, has_full_page_view_p, object_type, package_type_id, singleton_p, title, workspace_application_p)
       values (new_type_id, 'Portal based collaborative workspaces', '0', '1', 'com.arsdigita.london.portal.Workspace',
               (select package_type_id from apm_package_types where package_key = 'workspace'), '0', 'Portal Workspace', '1');

    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title)
       values (new_type_id, 'A Portal!', 'com.arsdigita.portal.Portal', 'Portal');

    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays the current date and time', 'com.arsdigita.london.portal.portlet.TimeOfDayPortlet', 'Time of Day',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays a list of content sections', 'com.arsdigita.london.portal.portlet.ContentSectionsPortlet', 'Content Sections',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays a freeform block of HTML', 'com.arsdigita.london.portal.portlet.FreeformHTMLPortlet', 'Freeform HTML',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays the body of a content item', 'com.arsdigita.london.portal.portlet.ContentItemPortlet', 'Content Item',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays a login form or user details', 'com.arsdigita.london.portal.portlet.LoginPortlet', 'Site Login',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays an RSS Feed', 'com.arsdigita.london.portal.portlet.RSSFeedPortlet', 'RSS Feed',
               '1', '0', 'wide');
    select acs_object_id_seq.nextval into new_type_id from dual;
    insert into application_types (application_type_id, description, object_type, title, has_embedded_view_p, has_full_page_view_p, profile)
       values (new_type_id, 'Displays the assigned CMS tasks', 'com.arsdigita.london.portal.portlet.TaskListPortlet', 'Task List',
               '1', '0', 'wide');
end; 
/
show errors

-- deal with the workspaces...they now extend Application instead of
-- ACSObject
insert into applications (application_id, primary_url, title, description, timestamp, application_type_id, package_id)
     select workspace_id, '/portal/', 'Portal Homepage', null, sysdate, application_type_id,
            (select object_id from site_nodes where name = 'portal')
       from workspaces, application_types 
      where lower(application_types.object_type) = 'com.arsdigita.london.portal.workspace'
        and workspaces.party_id is null;


-- XXX we are dropping the title, renderer, and editor without keeping them
declare
    new_package_id integer;
    new_site_node_id integer;
    parent_node_id integer;
    cursor workspaces is 
        select * from workspaces where party_id is not null;
begin
    select node_id into parent_node_id from site_nodes where url = '/portal/';

    for workspace in workspaces loop
        begin
        select acs_object_id_seq.nextval into new_site_node_id from dual;
        select acs_object_id_seq.nextval into new_package_id from dual;

        insert into acs_objects (object_id, default_domain_class, display_name, object_type)
             values (new_package_id,
                     'com.arsdigita.kernel.PackageInstance', 
                     '/portal/personal-' || workspace.party_id || '/',
                     'com.arsdigita.kernel.PackageInstance');

        insert into apm_packages (package_id, locale_id, package_type_id, pretty_name)
             values (new_package_id, null, (select package_type_id from apm_package_types where package_key = 'workspace'),
                     'Portal Workspace');

        insert into acs_objects (object_id, default_domain_class, display_name, object_type)
             values (new_site_node_id,
                     'com.arsdigita.kernel.SiteNode', 
                     '/portal/personal-' || workspace.party_id || '/',
                     'com.arsdigita.kernel.SiteNode');

        insert into site_nodes (node_id, directory_p, name, object_id, parent_id, pattern_p, url)
             values (new_site_node_id, 1, 'personal-' || workspace.party_id, 
                     new_package_id, parent_node_id, 0, '/portal/personal-' || workspace.party_id || '/');


        insert into applications (application_id, primary_url, title, description, timestamp, application_type_id, package_id)
             values (workspace.workspace_id, '/portal/personal-' || workspace.party_id || '/', 
                     'Personal Workspace for ' || (select display_name from acs_objects where object_id = workspace.party_id), 
                     null, sysdate, (select application_type_id from application_types where object_type = 'com.arsdigita.london.portal.Workspace'), 
                     new_package_id);
        end;
    end loop;
end;
/
show errors



-- XXX we are dropping the title, renderer, and editor without keeping them
declare
    new_group_id integer;
    new_hidden_group_id integer;
    new_role_id integer;
    curr_workspace_id integer;
begin
    select acs_object_id_seq.nextval into new_group_id from dual;
    select acs_object_id_seq.nextval into new_hidden_group_id from dual;
    select acs_object_id_seq.nextval into new_role_id from dual;
    select workspace_id into curr_workspace_id from workspaces where party_id is null;

    insert into acs_objects (object_id, default_domain_class, display_name, object_type)
             values (new_group_id,
                     'com.arsdigita.kernel.Group', 
                     'Portal Homepage',
                     'com.arsdigita.kernel.Group');
    insert into parties (party_id, primary_email, uri)
            values (new_group_id, null, null);
    insert into groups (group_id, name) values (new_group_id, 'Portal Homepage');

    insert into acs_objects (object_id, default_domain_class, display_name, object_type)
             values (new_hidden_group_id,
                     'com.arsdigita.kernel.Group', 
                     'Portal Homepage Administrators',
                     'com.arsdigita.kernel.Group');
    insert into parties (party_id, primary_email, uri)
            values (new_hidden_group_id, null, null);
    insert into groups (group_id, name) values (new_hidden_group_id, 'Portal Homepage Administrators');

    insert into roles (role_id, description, group_id, implicit_group_id, name)
            values (new_role_id, null, new_group_id, new_hidden_group_id, 'Administrators');

    update workspaces set party_id = new_group_id where party_id is null;

    insert into acs_permissions (privilege, object_id, grantee_id, creation_date, creation_ip, creation_user)
            values ('admin', curr_workspace_id, new_hidden_group_id, sysdate, '127.0.0.1', null);

    insert into acs_permissions (privilege, object_id, grantee_id, creation_date, creation_ip, creation_user)
            values ('read', curr_workspace_id, new_group_id, sysdate, '127.0.0.1', null);
end;
/
show errors


--update apm_package_types set package_uri = 'http://arsdigita.com/portal' where package_key = 'portal';
--update apm_packages 
--   set package_type_id = (select package_type_id from apm_package_types where package_key = 'workspace')
-- where package_id in (select object_id from site_nodes where url like '/portal%');

-- XXXXXXXX   I think that everything below this line should work

alter table portals drop column title;

--
-- update the portals
--
insert into applications 
(application_id, primary_url, title, description, timestamp, 
 application_type_id)
select
portal_id, null, portals_old.title, null, sysdate,
   (select application_type_id from application_types 
    where lower(application_types.object_type) = 'com.arsdigita.portal.portal')
from portals_old;

insert into portals select portal_id, '0' from portals_old;

update acs_objects 
set default_domain_class = 'com.arsdigita.portal.Portal',
    object_type = 'com.arsdigita.portal.Portal'
where object_id in (select portal_id from portals_old);

insert into workspace_portal_map
(workspace_id, portal_id, tab_number)
select workspace_id, portal_id, tab_number from portals_old;

-- we drop the package_id since it is no longer needed
alter table workspaces drop column package_id;


-- update the portlets
insert into applications (
  application_id, primary_url, title, description, timestamp, 
  application_type_id, cell_number, sort_key
)
select portlet_id, null, portlets_old.title, null, sysdate, application_type_id,
       column_number+1, row_number
  from portlets_old, application_types, portlet_types
 where lower(application_types.object_type) = lower(class_name) 
   and portlets_old.portlet_type_id = portlet_types.portlet_type_id;

insert into portlets (portlet_id, portal_id) 
select portlet_id, portal_id from portlets_old;

-- make all of the tables point to portlets instead of portlets old
alter table PORTLET_CONTENT_ITEM drop constraint PTL_CI_PORTLET_ID_FK;
alter table PORTLET_CONTENT_ITEM
  add constraint PORT_CONT_ITE_PORTL_ID_F_N19Z_
      foreign key (PORTLET_ID) references PORTLETS(portlet_id) on delete CASCADE ;
alter table PORTLET_FREEFORM_HTML drop constraint PTL_FFH_PORTLET_ID_FK;
alter table PORTLET_FREEFORM_HTML
  add constraint PORT_FREEF_HTM_PORT_ID_F_NE2B4
      foreign key (PORTLET_ID) references PORTLETS (portlet_id) on delete CASCADE ;
alter table PORTLET_RSS_FEED drop constraint PTL_RSS_PORTLET_ID_FK;
alter table PORTLET_RSS_FEED
  add constraint PORTL_RSS_FEE_PORTL_ID_F_CB7OG
      foreign key (PORTLET_ID) references PORTLETS (portlet_id) on delete CASCADE ;


drop table portlets_old;
drop table portals_old;

-- XXX we are dropping the title, renderer, and editor without keeping them
declare
    cursor types is 
        select portlet_type_id from portlet_types;
begin
    for type in types loop
        begin
           execute immediate 'delete from portlet_types where portlet_type_id = ' || type.portlet_type_id;
           execute immediate 'delete from acs_objects where object_id = ' || type.portlet_type_id;
        end;
    end loop;
end;
/
show errors

drop table portlet_types;

