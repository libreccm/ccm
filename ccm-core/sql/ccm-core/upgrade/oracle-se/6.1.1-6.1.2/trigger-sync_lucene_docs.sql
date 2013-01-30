
create or replace trigger sync_lucene_docs_tr
  after delete
  on acs_objects
  for each row
begin
  update lucene_docs set
      is_deleted = '1',
      dirty = 2147483647,
      timestamp = sysdate
    where document_id = :old.object_id ;
end;
/
show errors

