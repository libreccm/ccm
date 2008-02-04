
begin;
  alter table cms_form_item add remote_action boolean;

  alter table cms_form_item add remote_url varchar(700);

  alter table cms_form_item drop constraint CMS_FORM_ITEM_FRM_FK;

  alter table cms_form_item add constraint CMS_FORM_ITEM_FORM_ID_F_FRZIF
      foreign key (FORM_ID) references BEBOP_FORM_SECTIONS(FORM_SECTION_ID);

  alter table cms_form_item drop constraint cms_form_item_fk;

  ALTER TABLE cms_form_item ADD CONSTRAINT cms_form_item_item_id_f_gao21
      FOREIGN KEY (item_id) REFERENCES cms_pages(item_id);

create or replace function tmp_rename_pk_constraint_cfi () returns boolean as '

declare
    row                    record;
    v_con_count            integer;
    v_con_restore          varchar[];
begin
    -- Application
    -- find all that foreign key constraints that depend on cms_form_item_pk

    v_con_count := 0;
    for row in
        select tables.relname, cols.attname, cons.conname
           from
              pg_class tables,
              pg_attribute cols,
              pg_constraint cons
           where
              cons.confrelid = (select oid from pg_class where relname=''cms_form_item'')
              and cons.conrelid = tables.oid
              and cols.attrelid = tables.oid
              and cons.conkey[1] = cols.attnum
    loop
        execute ''ALTER TABLE "'' || row.relname || ''" DROP CONSTRAINT "'' || row.conname || ''"'';
        v_con_count := v_con_count + 1;
        v_con_restore[v_con_count] :=
            ''ALTER TABLE "'' || row.relname || ''" ADD CONSTRAINT "'' || row.conname ||
            ''" FOREIGN KEY ("'' || row.attname || ''") REFERENCES cms_form_item'';
    end loop;

    execute ''ALTER TABLE cms_form_item DROP CONSTRAINT cms_form_item_pk'';
    execute ''ALTER TABLE cms_form_item ADD CONSTRAINT cms_form_item_item_id_p_d370e PRIMARY KEY (item_id)'';

    for i in 1..v_con_count loop
        execute v_con_restore[i];
    end loop;

    return true;
end;
' language 'plpgsql';

select tmp_rename_pk_constraint_cfi();

drop function tmp_rename_pk_constraint_cfi();

commit;
