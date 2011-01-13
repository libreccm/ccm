alter table trm_terms add (unique_id_string varchar(128) default 'UNKNOWN' not null );
alter table trm_terms rename column unique_id to unique_id_old;
alter table trm_terms rename column unique_id_string to unique_id;
update trm_terms set unique_id = unique_id_old;
alter table trm_terms drop constraint trm_ter_domai_uniqu_id_u_6sito cascade drop index; 
alter table trm_terms add constraint trm_ter_domai_uniqu_id_u_6sito unique(domain, unique_id);
alter table trm_terms drop column unique_id_old;
