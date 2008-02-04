-- Morph old file objects into new ones
update acs_objects 
   set object_type = 'com.arsdigita.london.cms.dublin.types.DublinFileStorageItem',
       default_domain_class = 'com.arsdigita.london.cms.dublin.types.DublinFileStorageItem'
 where object_type = 'com.arsdigita.cms.contenttypes.FileStorageItem';

--  Change the content type.
update content_types 
   set object_type = 'com.arsdigita.london.cms.dublin.types.DublinFileStorageItem',
       classname = 'com.arsdigita.london.cms.dublin.types.DublinFileStorageItem'
  where object_type = 'com.arsdigita.cms.contenttypes.FileStorageItem';

-- No need to update the filestorage authoring kit, since the initializer will do that


-- Now remove the link contnet type

update cms_items set type_id = null
  where cms_items.type_id = (select content_types.type_id from content_types where object_type = 'com.arsdigita.cms.contenttypes.Link');

-- Remove the kit steps
delete from acs_objects where object_id in (
  select m.step_id 
    from authoring_kit_step_map m, 
         authoring_kits k,
	 content_types t
   where m.kit_id = k.kit_id
     and k.type_id = t.type_id
     and t.object_type = 'com.arsdigita.cms.contenttypes.Link'
);

-- Now the kit itself
delete from acs_objects where object_id in (
  select k.kit_id
    from authoring_kits k,
	 content_types t
   where k.type_id = t.type_id
     and t.object_type = 'com.arsdigita.cms.contenttypes.Link'
);

-- Finally the content type
delete from acs_objects where object_id = 
  (select type_id from content_types where object_type =  'com.arsdigita.cms.contenttypes.Link');
