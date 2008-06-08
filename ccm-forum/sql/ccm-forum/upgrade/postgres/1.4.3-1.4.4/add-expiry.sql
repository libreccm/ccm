begin;
    alter table forum_forums add lifecycle_definition_id INTEGER;
    alter table forum_forums add expire_after NUMERIC;
    alter table forum_forums add
        constraint foru_for_life_defin_id_f_ugal3 foreign key (lifecycle_definition_id)
          references lifecycle_definitions(definition_id);
commit;

