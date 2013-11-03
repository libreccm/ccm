-- Remove the authoring kit steps for the non-dublin article type
delete from acs_objects where object_id in
  (select m.step_id
   from authoring_kit_step_map m,
        authoring_kits k,
        content_types t
   where t.type_id = k.type_id
     and k.kit_id = m.kit_id
     and t.classname = 'com.arsdigita.cms.Article'
  );

-- Remove the authoring kits for the non-dublin article type
delete from acs_objects where object_id in
  (select k.kit_id
   from authoring_kits k,
        content_types t
   where t.type_id = k.type_id
     and t.classname = 'com.arsdigita.cms.Article'
  );

-- Remove the non-dublin article type
delete from acs_objects where object_id in
  (select t.type_id
   from content_types t
   where t.classname = 'com.arsdigita.cms.Article'
  );
