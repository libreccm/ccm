@@ ../../../../core/default/versioning/table-vc_objects.sql 

insert into vc_objects (object_id, is_deleted, master_id)
select distinct(object_id), 0, null
from vc_transactions;
 
insert into vc_objects (object_id, is_deleted, master_id)
select distinct(item_id), 0, null
from cms_items where item_id not in (select object_id from vc_objects);

alter table vc_transactions add (master_id        integer
             constraint vc_trans_masters_fk references vc_objects
             on delete cascade);

update vc_transactions set master_id = object_id;

drop index vc_transactions_object_id_idx;
create index vc_transactions_master_id_idx on vc_transactions(master_id);

alter table vc_transactions drop constraint vc_trans_objects_fk;
alter table vc_transactions 
    add constraint 
    vc_trans_objects_fk foreign key (object_id) references vc_objects;

alter table vc_operations add (
    classname varchar2(4000) 
);

update vc_operations vo
set classname = (decode(
    (select 1 from vc_generic_operations vgo
     where vgo.operation_id = vo.operation_id
     union all
     select 2 from vc_clob_operations vco
     where vco.operation_id = vo.operation_id
     union all
     select 3 from vc_blob_operations vbo
     where vbo.operation_id = vo.operation_id),
    1, 'com.arsdigita.versioning.GenericOperation',
    2, 'com.arsdigita.versioning.ClobOperation',
    3, 'com.arsdigita.versioning.BlobOperation'))
where classname is null;

alter table vc_operations modify (
    classname varchar2(4000) constraint vc_operations_classname_nn not null
);

comment on column vc_operations.classname is '
  Java classname of the specific class for the operation
';


-- there are a bunch of indexes that may or may not exist but we try
-- to create later so if they already exist we just drop
declare
  v_exists integer;
begin
    select count(*) into v_exists from user_indexes where lower(index_name) = 'acs_permissions_privilege_idx';
    if (v_exists > 0) then
      execute immediate 'drop index acs_permissions_privilege_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'apm_ptyp_lstnr_mp_lstnr_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index apm_ptyp_lstnr_mp_lstnr_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'bebop_comp_hrchy_comp_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index bebop_comp_hrchy_comp_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'bebop_frm_prcss_lstnr_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index bebop_frm_prcss_lstnr_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'bebop_lstnr_map_lstnr_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index bebop_lstnr_map_lstnr_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cat_catcat_map_rltd_cat_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cat_catcat_map_rltd_cat_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cat_cat_purp_map_purp_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cat_cat_purp_map_purp_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_process_tsk_map_tsk_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_process_tsk_map_tsk_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_task_comments_task_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_task_comments_task_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_task_deps_dpnt_tsk_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_task_deps_dpnt_tsk_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_task_grp_assgns_grp_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_task_grp_assgns_grp_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_task_lsnrs_lsnr_task_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_task_lsnrs_lsnr_task_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cw_task_usr_assgns_usr_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cw_task_usr_assgns_usr_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'g11n_catalogs_locale_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index g11n_catalogs_locale_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'g11n_loc_ch_map_charset_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index g11n_loc_ch_map_charset_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'group_member_map_member_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index group_member_map_member_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'group_subgrp_map_subgrp_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index group_subgrp_map_subgrp_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'messages_object_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index messages_object_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'nt_queue_party_to_idx';
    if (v_exists > 0) then
       execute immediate 'drop index nt_queue_party_to_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'object_context_context_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index object_context_context_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'object_context_map_ctx_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index object_context_map_ctx_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'parameter_priv_base_priv_idx';
    if (v_exists > 0) then
       execute immediate 'drop index parameter_priv_base_priv_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'pl_us_cnties_st_fips_code_idx';
    if (v_exists > 0) then
       execute immediate 'drop index pl_us_cnties_st_fips_code_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'ung_ctx_nlf_mp_impl_ctx_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index ung_ctx_nlf_mp_impl_ctx_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'akit_step_map_step_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index akit_step_map_step_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_artcl_img_map_img_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_artcl_img_map_img_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_itm_tplt_map_tplt_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_itm_tplt_map_tplt_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_item_tplt_map_use_ctx_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_item_tplt_map_use_ctx_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_sec_tplt_map_tplt_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_sec_tplt_map_tplt_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_sec_tplt_map_typ_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_sec_tplt_map_typ_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_sec_tplt_map_use_ctx_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_sec_tplt_map_use_ctx_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_stdlne_pgs_pg_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_stdlne_pgs_pg_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cms_stdlne_pgs_tplt_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cms_stdlne_pgs_tplt_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cont_sec_cont_exp_dgst_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cont_sec_cont_exp_dgst_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cont_sec_typ_map_typ_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cont_sec_typ_map_typ_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'cont_typs_itm_frm_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index cont_typs_itm_frm_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'pub_to_fs_files_server_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index pub_to_fs_files_server_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'pub_to_fs_queue_server_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index pub_to_fs_queue_server_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'sec_lc_def_map_cyc_def_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index sec_lc_def_map_cyc_def_id_idx';
    end if;
    select count(*) into v_exists from user_indexes where lower(index_name) = 'sec_wf_tplt_map_wf_tplt_id_idx';
    if (v_exists > 0) then
       execute immediate 'drop index sec_wf_tplt_map_wf_tplt_id_idx';
    end if;
end;
/
show errors




@@ ../../../../auth/ddl/oracle-se/table-auth_ntlm_cc-auto.sql
@@ ../../../../auth/ddl/oracle-se/table-auth_ntlm_nonces-auto.sql
@@ ../../../../auth/ddl/oracle-se/table-auth_ntlm_users-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-ct_addresses-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-iso_countries-auto.sql
@@ ../../../../content-types/default/insert-iso-countries.sql

drop index FF_CONTENT_ITEM_ASSET_MAP_IDX;

alter table portals rename to portals_old;
alter table portlets rename to portlets_old;
alter index portlets_portal_id_idx rename to portlets_portal_id_idx_old;


-- we are renaming the constraint so that a later upgrade script can find it
-- and rename it appropriately.  
alter index acs_objects_id_pk rename to acs_objects_pk;

-- Stoke on Trent is not using Forums.  The old forums are not feature
-- compatible with the new forums so an upgrade script may not even
-- be possible
drop table FORUM_AREA_USER_MAP;
drop table FORUM_FORUM;
drop table FORUM_MESSAGE_USER_MAP;
drop table FORUM_MSG_MODERATE;
drop table FORUM_PORTLET;
drop table FORUM_AREA;
delete from portlets_old where portlet_type_id = (select portlet_type_id from portlet_types where class_name = 'com.arsdigita.forum.ForumPostingsPortlet');
delete from acs_objects where object_type = 'com.arsdigita.forum.ForumPostingsPortlet';
delete from portlet_types where class_name = 'com.arsdigita.forum.ForumPostingsPortlet';

-- delete the forum site node
declare 
   v_id integer;
begin 
   select node_id into v_id from site_nodes where name = 'forums';
   delete from site_nodes where node_id = v_id;
   delete from acs_objects where object_id = v_id;
end;
/
show errors;
