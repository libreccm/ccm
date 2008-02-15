create table forum_post_images  
    (post_id  NUMBER,
	 image_id  NUMBER not null,
	 image_order NUMBER);



alter table forum_post_images add 
    constraint FORU_POS_IMAGE_IMAG_ID_F_WYRXA  foreign key (image_id)
      references cms_images(image_id);

alter table forum_post_images add 
    constraint FORU_POS_IMAGE_POST_ID_F_1HH02  foreign key (post_id)
      references forum_posts(post_id);

alter table forum_forums add (image_uploads_allowed CHAR(1));

update forum_forums set image_uploads_allowed = 0;



