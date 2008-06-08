create table forum_post_files  
   (post_id  NUMBER,
    file_id  NUMBER not null,
    file_order NUMBER);



alter table forum_post_files add 
    constraint FORU_POS_FILES_FILE_ID_F_XBKED  foreign key (file_id)
      references cms_files(file_id);

alter table forum_post_files add 
    constraint FORU_POS_FILES_POST_ID_F_K0XJQ  foreign key (post_id)
      references forum_posts(post_id);
      
alter table forum_forums add (file_attachments_allowed CHAR(1));

update forum_forums set file_attachments_allowed = 0;



