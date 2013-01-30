
begin;

alter table lucene_docs add column content_section varchar(300);

commit;
