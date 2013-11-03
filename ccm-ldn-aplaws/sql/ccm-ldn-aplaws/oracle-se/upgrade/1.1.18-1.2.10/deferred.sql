alter table auth_ntlm_cc add 
    constraint auth_ntlm_cc_center_id_f_3elxf foreign key (center_id)
      references applications(application_id) on delete cascade;
alter table auth_ntlm_users add 
    constraint auth_ntl_users_user_id_f_qck7z foreign key (user_id)
      references users(user_id) on delete cascade;

alter table ct_addresses add 
    constraint ct_addre_iso_coun_code_f_o8h8a foreign key (iso_country_code)
      references iso_countries(iso_code);
alter table ct_addresses add 
    constraint ct_addresse_address_id_f__qv8u foreign key (address_id)
      references cms_pages(item_id) on delete cascade;

alter table nav_quick_links add 
    constraint nav_quic_links_link_id_f_svehq foreign key (link_id)
      references acs_objects(object_id) on delete cascade;

alter table cms_links add 
    constraint cms_link_targe_item_id_f_xe__d foreign key (target_item_id)
      references cms_items(item_id);
alter table cms_links add 
    constraint cms_links_link_id_f_1ljfs foreign key (link_id)
      references cms_items(item_id) on delete cascade;
alter table cms_links add 
    constraint cms_links_owner_id_f_31nb8 foreign key (owner_id)
      references cms_items(item_id) on delete cascade;

alter table nav_template_cat_map add 
    constraint nav_tem_cat_map_cat_id_f_qz303 foreign key (category_id)
      references cat_categories(category_id) on delete cascade;
alter table nav_template_cat_map add 
    constraint nav_tem_cat_map_tem_id_f_2rrdi foreign key (template_id)
      references nav_templates(template_id) on delete cascade;

alter table subsite_site add 
    constraint subs_sit_roo_catego_id_f_kwe6m foreign key (root_category_id)
      references cat_categories(category_id);
alter table subsite_site add 
    constraint subsit_sit_fron_pag_id_f_4agqx foreign key (front_page_id)
      references applications(application_id);
alter table subsite_site add 
    constraint subsite_site_site_id_f_rntkc foreign key (site_id)
      references acs_objects(object_id) on delete cascade;
alter table subsite_cc add 
    constraint subsite_cc_center_id_f_g4ccb foreign key (center_id)
      references applications(application_id) on delete cascade;

alter table workspace_portal_map add 
    constraint works_por_map_works_id_f_dccb2 foreign key (workspace_id)
      references workspaces(workspace_id) on delete cascade;
alter table workspace_portal_map add 
    constraint worksp_por_map_port_id_f_6hl2n foreign key (portal_id)
      references portals(portal_id) on delete cascade;

alter table ct_faq add 
    constraint ct_faq_faq_item_id_f_15buq foreign key (faq_item_id)
      references cms_pages(item_id) on delete cascade;
alter table ct_glossary add 
    constraint ct_gloss_glossa_ite_id_f_hfu9_ foreign key (glossary_item_id)
      references cms_pages(item_id) on delete cascade;
alter table ct_file_storage add 
    constraint ct_fil_storage_file_id_f_k8djn foreign key (file_id)
      references cms_files(file_id);
alter table ct_file_storage add 
    constraint ct_fil_storage_item_id_f_zilum foreign key (item_id)
      references cms_pages(item_id) on delete cascade;
alter table ct_organization add 
    constraint ct_organ_organi_ite_id_f_i_ir5 foreign key (organization_item_id)
      references cms_pages(item_id) on delete cascade;


-- custom written constraints
alter table ACS_OBJECT_LIFECYCLE_MAP 
  add constraint acs_object_life_map_obj_id_nn
  check ("OBJECT_ID" IS NOT NULL);

alter table VC_BLOB_OPERATIONS add constraint vc_blob_operations_op_id_nn
  check ("OPERATION_ID" IS NOT NULL);

alter table RSS_FEEDS drop constraint RSS_FEEDS_ACSJ_CK;

alter table CT_NEWS add constraint ct_news_item_id_nn
  check ("ITEM_ID" IS NOT NULL);

alter table CMS_VARIANT_TAGS add constraint cms_variant_tags_tag_nn
  check ("TAG" IS NOT NULL);
alter table CMS_TEXT add constraint cms_text_text_id_nn
  check ("TEXT_ID" IS NOT NULL);
alter table CT_MP_ARTICLES add constraint ct_mp_articles_article_id_nn
  check ("ARTICLE_ID" IS NOT NULL);
alter table CMS_PAGES add constraint cms_pages_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table CMS_FILES add constraint cms_files_file_id_nn
  check ("FILE_ID" IS NOT NULL);

alter table PHASES add constraint phases_phase_id_nn
  check ("PHASE_ID" IS NOT NULL);
alter table PHASE_DEFINITIONS add constraint phase_defn_phase_def_id_nn
  check ("PHASE_DEFINITION_ID" IS NOT NULL);
alter table ACS_STYLESHEETS add constraint acs_stylesheets_style_id_nn
  check ("STYLESHEET_ID" IS NOT NULL);

alter table APM_LISTENERS add constraint apm_listeners_listener_id_nn
  check ("LISTENER_ID" IS NOT NULL);
alter table PUBLISH_TO_FS_QUEUE add constraint publis_to_fs_queue_id_nn
  check ("ID" IS NOT NULL);
alter table CT_SERVICE add constraint ct_service_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table PUBLISH_TO_FS_SERVERS add constraint pub_to_fs_id_nn
  check ("ID" IS NOT NULL);
alter table AUTHORING_STEPS add constraint authoring_steps_step_id_nn
  check ("STEP_ID" IS NOT NULL);
alter table CMS_MIME_STATUS add constraint cms_mime_status_m_stat_id_nn
  check ("MIME_STATUS_ID" IS NOT NULL);
alter table POST_CONVERT_HTML add constraint post_convert_html_query_id_nn
  check ("QUERY_ID" IS NOT NULL);
alter table CT_JOBS add constraint ct_jobs_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table LIFECYCLES add constraint lifecycles_cycle_id_nn
  check ("CYCLE_ID" IS NOT NULL);
alter table CT_PRESS_RELEASES add constraint ct_press_releases_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table CMS_USER_DEFINED_ITEMS add constraint cms_user_def_item_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table LIFECYCLE_DEFINITIONS add constraint life_defn_defn_id_nn
  check ("DEFINITION_ID" IS NOT NULL);
alter table CMS_IMAGES add constraint cms_images_image_id_nn
  check ("IMAGE_ID" IS NOT NULL);
alter table ROLES add constraint roles_role_id_nn
  check ("ROLE_ID" IS NOT NULL);
alter table VC_CLOB_OPERATIONS add constraint vc_clob_oper_oper_id_nn
  check ("OPERATION_ID" IS NOT NULL);
alter table CT_AGENDAS add constraint ct_agendas_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table CT_MINUTES add constraint ct_minutes_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table CMS_ASSETS add constraint cms_assets_asset_id_nn
  check ("ASSET_ID" IS NOT NULL);
alter table CT_ARTICLES add constraint ct_articles_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table SITE_NODES add constraint site_nodes_node_id_nn
  check ("NODE_ID" IS NOT NULL);
alter table AUTHORING_KIT_STEP_MAP add constraint auth_kit_step_map_step_id_nn
  check ("STEP_ID" IS NOT NULL);
alter table AUTHORING_KIT_STEP_MAP add constraint auth_kit_step_map_kit_id_nn
  check ("KIT_ID" IS NOT NULL);
alter table APM_PACKAGE_TYPES add constraint apm_pack_types_pack_type_id_nn
  check ("PACKAGE_TYPE_ID" IS NOT NULL);
alter table CMS_ARTICLE_IMAGE_MAP drop constraint CMS_ARTICLE_IMAGE_MAP_ID_NN;
alter table CMS_ARTICLE_IMAGE_MAP add constraint CMS_ARTICLE_IMAGE_MAP_ID_NN
  check ("MAP_ID" IS NOT NULL);
alter table CT_LEGAL_NOTICES add constraint ct_legal_notices_item_id_nn
  check ("ITEM_ID" IS NOT NULL);
alter table CT_MP_SECTIONS add constraint ct_mp_sections_section_id_nn
  check ("SECTION_ID" IS NOT NULL);

alter table portlets modify (portlet_id integer null);
alter table ss_answers drop constraint ss_answers_lid_nn;
alter table ss_answers drop constraint ss_answers_rid_nn;
alter table ss_answers drop constraint ss_answers_wid_nn;

alter table VC_TRANSACTIONS drop constraint VC_TRANS_OBJECTS_FK;
alter table VC_TRANSACTIONS
  add constraint VC_TRANS_OBJECTS_FK
      foreign key (OBJECT_ID) references VC_OBJECTS(object_id) 
      on delete CASCADE ;

create index VC_TRANSACTIONS_TSTAMP_IDX on VC_TRANSACTIONS(TIMESTAMP);

alter table SITE_NODES drop constraint SITE_NODES_PARENT_ID_FK;
alter table SITE_NODES
  add constraint SITE_NODES_PARENT_ID_F_SACAV
      foreign key (PARENT_ID) references SITE_NODES on delete CASCADE ;

alter table PARTIES drop constraint PARTIES_PARTY_ID_FK;
alter table PARTIES
  add constraint PARTIES_PARTY_ID_F_J4K1I
      foreign key (PARTY_ID) references ACS_OBJECTS (object_id) 
      on delete CASCADE ;

alter table NT_REQUESTS drop constraint NT_REQUESTS_MESSAGE_FK;
alter table NT_REQUESTS
  add constraint NT_REQUESTS_MESSAGE_FK
      foreign key (MESSAGE_ID) references MESSAGES;

alter table USERS drop constraint USERS_USER_ID_FK;
alter table USERS
  add constraint USERS_USER_ID_F_T_LSO
      foreign key (USER_ID) references PARTIES (party_id) on delete CASCADE ;

alter table WORKSPACES drop constraint WORKSPACE_PARTY_ID_FK;
alter table WORKSPACES
  add constraint WORKSPACES_PARTY_ID_F_JOTDD
      foreign key (PARTY_ID) references PARTIES (party_id) ;
alter table WORKSPACES drop constraint WORKSPACE_WORKSPACE_ID_FK;
alter table WORKSPACES
  add constraint WORKSPACE_WORKSPACE_ID_F_DTED3
      foreign key (WORKSPACE_ID) references APPLICATIONS (application_id) 
      on delete CASCADE ;
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_SECTION_ID_FK;
alter table CMS_CATEGORY_TEMPLATE_MAP
  add constraint CMS_CAT_TEM_MAP_SEC_ID_F_3P3QY
      foreign key (SECTION_ID) references CONTENT_SECTIONS (section_id);
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_TEMPLATE_ID_FK;
alter table CMS_CATEGORY_TEMPLATE_MAP
  add constraint CMS_CAT_TEM_MAP_TEM_ID_F_RDNZA
      foreign key (TEMPLATE_ID) references CMS_TEMPLATES (template_id);
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_CATEGORY_ID_FK;
alter table CMS_CATEGORY_TEMPLATE_MAP
  add constraint CMS_CAT_TEM_MAP_CAT_ID_F_FA56U
      foreign key (CATEGORY_ID) references CAT_CATEGORIES (category_id);
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_TYPE_ID_FK;
alter table CMS_CATEGORY_TEMPLATE_MAP
  add constraint CMS_CAT_TEM_MAP_TYP_ID_F_LS_QA
      foreign key (TYPE_ID) references CONTENT_TYPES (type_id);
alter table ACS_PERMISSIONS drop constraint ACS_PERM_CREATION_USER_FK;
alter table ACS_PERMISSIONS
  add constraint ACS_PERMISS_CREAT_USER_F_HIYN9
      foreign key (CREATION_USER) references USERS (user_id) ;
alter table APM_PACKAGES drop constraint APM_PACKAGES_PACKAGE_ID_FK;
alter table APM_PACKAGES
  add constraint APM_PACKAGE_PACKAGE_ID_F_46MAY
      foreign key (PACKAGE_ID) references ACS_OBJECTS (object_id) on delete CASCADE ;
alter table CT_MP_SECTIONS drop constraint CT_MP_SECTIONS_ARTICLE_ID_FK;
alter table CT_MP_SECTIONS
  add constraint CT_MP_SECTIO_ARTICL_ID_F_NTNSJ
      foreign key (ARTICLE_ID) references CT_MP_ARTICLES (article_id) on delete CASCADE ;
alter table ACS_STYLESHEETS drop constraint ACS_STYLESHEET_ID_FK;
alter table ACS_STYLESHEETS
  add constraint ACS_STYLESH_STYLESH_ID_F_2FIOK
      foreign key (STYLESHEET_ID) references ACS_OBJECTS (object_id) on delete CASCADE ;
alter table GROUP_SUBGROUP_MAP drop constraint GSM_SUBGROUP_ID_FK;
alter table GROUP_SUBGROUP_MAP
  add constraint GROU_SUBG_MAP_SUBGR_ID_F_1JO4E
      foreign key (SUBGROUP_ID) references GROUPS (group_id) on delete CASCADE ;
alter table GROUP_SUBGROUP_MAP drop constraint GSM_CIRCULARITY_CK;
alter table GROUP_SUBGROUP_MAP add constraint GSM_CIRCULARITY_CK
  check ( group_id != subgroup_id);
alter table GROUP_SUBGROUP_MAP drop constraint GSM_GROUP_ID_FK;
alter table GROUP_SUBGROUP_MAP
  add constraint GROU_SUBGRO_MAP_GRO_ID_F_TODNR
      foreign key (GROUP_ID) references GROUPS (group_id) on delete CASCADE ;
alter table GROUP_MEMBER_MAP drop constraint GMM_GROUP_ID_FK;
alter table GROUP_MEMBER_MAP
  add constraint GROU_MEMBE_MAP_GROU_ID_F_D7LHM
      foreign key (GROUP_ID) references GROUPS (group_id) on delete CASCADE ;
alter table GROUP_MEMBER_MAP drop constraint GMM_MEMBER_ID_FK;
alter table GROUP_MEMBER_MAP
  add constraint GROU_MEMB_MAP_MEMBE_ID_F_BS3U_
      foreign key (MEMBER_ID) references USERS (user_id) on delete CASCADE ;

alter table ROLES drop constraint GROUP_ROLES_GROUP_ID_FK;
alter table ROLES
  add constraint ROLES_GROUP_ID_F_DOYEU
      foreign key (GROUP_ID) references GROUPS (group_id) ;
alter table GROUPS drop constraint GROUPS_GROUP_ID_FK;
alter table GROUPS
  add constraint GROUPS_GROUP_ID_F_L4TVR
      foreign key (GROUP_ID) references PARTIES (party_id) on delete CASCADE ;
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_IS_DEF_BOOL;
alter table CMS_CATEGORY_TEMPLATE_MAP drop constraint CMS_CTM_USE_CTX_FK;
drop index CAT_CAT_CAT_MAP_CAT_IDX;
drop index CAT_CAT_CAT_MAP_ALT_UN;

alter table section_lifecycle_def_map drop constraint section_lifecycle_def_map_pk;
alter table section_lifecycle_def_map add constraint sect_lif_def_map_cyc_d_p_5lrl6 primary key(cycle_definition_id, section_id);
alter table ff_content_item_asset_map drop constraint ff_content_item_asset_map_pk;

--alter table forum_portlet drop constraint FORUM_PORTLET_ID_FK;
--alter table forum_portlet add constraint FORUM_PORTLET_ID_FK foreign key (portlet_id) references portlets(portlet_id);

-- some redundant constraints
declare
    cursor constraints is 
        select constraint_name from user_constraints 
        where lower(table_name) = 'cat_category_purpose_map' 
        and constraint_type = 'C';
begin
    for con in constraints loop
        begin
           execute immediate 'alter table cat_category_purpose_map drop constraint ' || con.constraint_name;
        end;
    end loop;
end;
/
show errors

declare
    cursor constraints is 
        select constraint_name from user_cons_columns
        where constraint_name in (select constraint_name from user_constraints 
        where lower(table_name) = 'party_email_map' 
        and constraint_type = 'C')
        and lower(column_name) = 'party_id';
begin
    for con in constraints loop
        begin
           execute immediate 'alter table party_email_map drop constraint ' || con.constraint_name;
        end;
    end loop;
end;
/
show errors


drop index LDN_DUB_CORE_ITEM_MAP_DUB_IDX;

-- drop some extra indexes
drop index CAT_CATEGORIES_NAME_IDX;
drop index SS_ANSWERS_WID_IDX;
drop index SS_ANSWERS_RESP_IDX;
drop index SS_ANSWERS_LABEL_IDX;
drop index SS_SURVEY_RESPONSES_USER_IDX;
drop index SS_SURVEY_RESPONSES_SURV_IDX;
drop index PORTLET_CONTENT_ITEM_IT_IDX;
drop index SS_SURVEYS_PCK_IDX;
drop index SS_SURVEYS_FORM_IDX;


-- refresh some triggers
@@ ../../../../core/default/kernel/trigger-acs_parties.sql
