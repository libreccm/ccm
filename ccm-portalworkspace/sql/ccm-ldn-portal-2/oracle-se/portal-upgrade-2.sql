delete from acs_objects where object_type = 'com.arsdigita.london.portal.portlet.ForumPostingsPortlet';

delete from acs_objects where object_id = (
  select portlet_type_id 
    from portlet_types 
   where class_name = 'com.arsdigita.london.portal.portlet.ForumPostingsPortlet'
);
