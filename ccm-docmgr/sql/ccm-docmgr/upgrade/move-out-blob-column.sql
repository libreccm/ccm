------------------------------------------
-- This upgrade moves the content column from docs_resources
-- to its own table called docs_blobjects, then deletes the column.
------------------------------------------

------------------------------------------
-- 1. Create new docs_blobjects table
------------------------------------------
create table docs_blobjects (
    content_id INTEGER not null
        constraint docs_blobjec_conten_id_p_7c9oS
          primary key,
        -- referential constraint for content_id deferred due to circular dependencies
    resource_id INTEGER not null,
        -- referential constraint for resource_id deferred due to circular dependencies
    content BLOB
);

------------------------------------------
-- 2.  Add constraints
-----------------------------------------
alter table docs_blobjects add
    constraint docs_blobjec_resour_id_f__xiNa foreign key (resource_id)
      references docs_resources(resource_id);

------------------------------------------
-- 3. Insert data from docs_resources into
--    docs_blobjects
------------------------------------------ 
insert into docs_blobjects (content_id, content,resource_id)
  select acs_object_id_seq.nextval, content, resource_id from docs_resources;

------------------------------------------
-- 4. Drop content column from docs_resources
------------------------------------------
alter table docs_resources
drop (content);
