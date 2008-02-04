
update acs_objects set
object_type = 'com.arsdigita.cms.ReusableImageAsset',
default_domain_class = 'com.arsdigita.cms.ReusableImageAsset'
where object_type = 'com.arsdigita.cms.contenttypes.AttachableImage';

drop table cms_attachable_images;




@@ ../ddl/postgres/table-cms_item_image_attachment-auto.sql
alter table cms_item_image_attachment add
    constraint cms_ite_ima_att_att_id_f_ahq60 foreign key (attachment_id)
      references acs_objects(object_id);
alter table cms_item_image_attachment add
    constraint cms_ite_ima_att_ima_id_f_kz3mi foreign key (image_id)
      references cms_images(image_id);
alter table cms_item_image_attachment add
    constraint cms_ite_ima_att_ite_id_f_wg1gi foreign key (item_id)
      references cms_items(item_id);


insert into cms_item_image_attachment
(use_context, caption, image_id, item_id, attachment_id)
select '', map.caption, map.image_id, map.article_id, map.map_id
from cms_article_image_map map;

delete from cms_article_image_map;


delete from cms_items
where item_id in (
    select object_id from acs_objects
    where object_type = 'com.arsdigita.cms.ArticleImageAssociation' );

delete from cms_published_links
where pending_source in (
    select object_id from acs_objects
    where object_type = 'com.arsdigita.cms.ArticleImageAssociation' );

update inits
set class_name = 'com.arsdigita.cms.ItemImageAttachmentInitializer'
where class_name = 'com.arsdigita.cms.AttachableImageInitializer';

update acs_objects set
object_type = 'com.arsdigita.cms.contenttypes.ItemImageAttachment',
default_domain_class = 'com.arsdigita.cms.contenttypes.ItemImageAttachment'
where object_type = 'com.arsdigita.cms.ArticleImageAssociation';

update authoring_steps
set label_key = 'com.arsdigita.cms.image_step_label',  label_bundle = 'com.arsdigita.cms.ImageStepResources', description_key = 'com.arsdigita.cms.image_step_description', description_bundle = 'com.arsdigita.cms.ImageStepResources', component = 'com.arsdigita.cms.contenttypes.ui.ImageStep'
where component = 'com.arsdigita.cms.ui.authoring.ArticleImage';
