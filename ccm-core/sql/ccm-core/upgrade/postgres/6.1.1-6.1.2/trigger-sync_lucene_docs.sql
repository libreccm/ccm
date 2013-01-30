
create or replace function sync_lucene_docs_fn () returns trigger as '
begin
  update lucene_docs set
      is_deleted = ''t'',
      dirty = 2147483647,
      timestamp = current_date
      where document_id = old.object_id ;
  return null;
end;' language 'plpgsql';

create trigger sync_lucene_docs_tr
  after delete on acs_objects
  for each row execute procedure
  sync_lucene_docs_fn();

