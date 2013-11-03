create table forum_post_files  
   (file_id    INTEGER not null,
    file_order INTEGER,
    post_id    INTEGER);

alter table forum_post_files add 
    constraint FORU_POS_FILES_FILE_ID_P_IO1IR  primary key (file_id);

alter table forum_post_files add 
    constraint FORU_POS_FILES_FILE_ID_F_XBKED  foreign key (file_id)
      references cms_files(file_id);

alter table forum_post_files add 
    constraint FORU_POS_FILES_POST_ID_F_K0XJQ  foreign key (post_id)
      references forum_posts(post_id);


      
alter table forum_forums add file_attachments_allowed BOOLEAN NOT NULL DEFAULT TRUE;

update forum_forums set file_attachments_allowed = TRUE;



