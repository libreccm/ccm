alter table ca_file_attachments add file_order integer;
-- update draft items first
update ca_file_attachments set
    file_order = file_id
    where file_id in (select master_id from cms_items);
-- now copy that ordering to the published items, so
-- that we get no inconsistencies in already published pages.
update ca_file_attachments cx set
    file_order = (select c2.file_order from ca_file_attachments c2
                  where c2.file_id = (select ci.master_id from cms_items ci
                      where ci.item_id = cx.file_id))
    where
    file_id in (select ci2.item_id from cms_items ci2 where ci2.master_id is not null);
commit;

