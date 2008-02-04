begin;

  alter table ldn_dublin_core_items add
    item_id integer constraint ldn_dub_cor_ite_ite_id_f_f7q6_ references cms_items(item_id);
  update ldn_dublin_core_items
    set item_id = dcm.item_id
    from LDN_DUBLIN_CORE_ITEM_MAP dcm
    where dcm.dublin_id=ldn_dublin_core_items.dublin_id;

  drop table ldn_dublin_core_item_map;

commit;
