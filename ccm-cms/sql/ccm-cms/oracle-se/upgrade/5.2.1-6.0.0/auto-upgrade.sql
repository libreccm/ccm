--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: auto-upgrade.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


--------------------------------------------------------------------------------
-- These columns went from 'not nullable' to 'nullable'.
--------------------------------------------------------------------------------
alter table CMS_ITEMS modify ( ANCESTORS null );
alter table PUBLISH_TO_FS_QUEUE modify ( ITEM_ID null );

--------------------------------------------------------------------------------
-- These columns went from 'nullable' to 'not nullable'.
--------------------------------------------------------------------------------
alter table CMS_RESOURCES modify ( TYPE not null );
alter table PUBLISH_TO_FS_QUEUE modify ( ITEM_TYPE not null );

--------------------------------------------------------------------------------
-- These default values for these columns changed.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES modify ( ABSTRACT_P default NULL );
alter table CMS_MIME_TYPES modify ( JAVA_CLASS default NULL );
alter table CMS_MIME_TYPES modify ( OBJECT_TYPE default NULL );

--------------------------------------------------------------------------------
-- These char(1) boolean check constraints have been added.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These 'not null' check constraints have been dropped.
--------------------------------------------------------------------------------
alter table ACS_OBJECT_LIFECYCLE_MAP drop constraint ACS_OBJ_CYCLE_MAP_ITEM_ID_NN;
alter table ACS_OBJECT_LIFECYCLE_MAP modify ( ITEM_ID not null );
alter table CMS_RESOURCES drop constraint CMS_RESOURCES_SOURCE_NIL;
alter table CMS_RESOURCES modify ( CLASS not null );
alter table PUBLISH_TO_FS_FILES drop constraint PUBLISH_TO_FS_FILES_DRAFT_NIL;
alter table PUBLISH_TO_FS_FILES modify ( DRAFT_ID not null );
alter table PUBLISH_TO_FS_FILES drop constraint PUBLISH_TO_FS_FILES_ITM_NN;
alter table PUBLISH_TO_FS_FILES modify ( ITEM_ID not null );
alter table PUBLISH_TO_FS_FILES drop constraint PUBLISH_TO_FS_FILES_NAME_NIL;
alter table PUBLISH_TO_FS_FILES modify ( FILE_NAME not null );

--------------------------------------------------------------------------------
-- These foreign key constraints have change their action for 'on delete'.
-- Their names may have changed as well.
--------------------------------------------------------------------------------
alter table ACS_OBJECT_LIFECYCLE_MAP drop constraint ACS_OBJ_LIF_MAP_CYC_ID_F_HME4H;
alter table ACS_OBJECT_LIFECYCLE_MAP add constraint ACS_OBJ_LIF_MAP_CYC_ID_F_HME4H
    foreign key(CYCLE_ID) references LIFECYCLES(CYCLE_ID);

alter table AUTHORING_KITS drop constraint AUTHORING_KITS_KIT_ID_FK;
alter table AUTHORING_KITS add constraint AUTHORING_KITS_KIT_ID_FK
    foreign key(KIT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table AUTHORING_KITS drop constraint AUTHORING_KITS_TYPE_ID_FK;
alter table AUTHORING_KITS add constraint AUTHORING_KITS_TYPE_ID_FK
    foreign key(TYPE_ID) references CONTENT_TYPES(TYPE_ID);

alter table AUTHORING_STEPS drop constraint AUTHORIN_STEPS_STEP_ID_F_TM6XL;
alter table AUTHORING_STEPS add constraint AUTHORIN_STEPS_STEP_ID_F_TM6XL
    foreign key(STEP_ID) references ACS_OBJECTS(OBJECT_ID);

alter table AUTHORING_KIT_STEP_MAP drop constraint AUTH_KIT_STE_MAP_KIT_I_F_1MUR9;
alter table AUTHORING_KIT_STEP_MAP add constraint AUTH_KIT_STE_MAP_KIT_I_F_1MUR9
    foreign key(KIT_ID) references AUTHORING_KITS(KIT_ID);

alter table AUTHORING_KIT_STEP_MAP drop constraint AUTH_KIT_STE_MAP_STE_I_F_Z4LXS;
alter table AUTHORING_KIT_STEP_MAP add constraint AUTH_KIT_STE_MAP_STE_I_F_Z4LXS
    foreign key(STEP_ID) references AUTHORING_STEPS(STEP_ID);

alter table CMS_ARTICLE_IMAGE_MAP drop constraint CAIM_ARTICLE_ID_FK;
alter table CMS_ARTICLE_IMAGE_MAP add constraint CAIM_ARTICLE_ID_FK
    foreign key(ARTICLE_ID) references CMS_ARTICLES(ARTICLE_ID);

alter table CMS_ARTICLE_IMAGE_MAP drop constraint CAIM_IMAGE_ID_FK;
alter table CMS_ARTICLE_IMAGE_MAP add constraint CAIM_IMAGE_ID_FK
    foreign key(IMAGE_ID) references CMS_IMAGES(IMAGE_ID);

alter table CMS_ARTICLES drop constraint CMS_ARTICLES_ARTICLE_ID_FK;
alter table CMS_ARTICLES add constraint CMS_ARTICLE_ARTICLE_ID_F_EKQK1
    foreign key(ARTICLE_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CMS_ARTICLE_IMAGE_MAP drop constraint CMS_ARTICLE_IMAGE_MAP_ID_FK;
alter table CMS_ARTICLE_IMAGE_MAP add constraint CMS_ARTICLE_IMAGE_MAP_ID_FK
    foreign key(MAP_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_ASSETS drop constraint CMS_ASSETS_ASSET_ID_F_MLSFS;
alter table CMS_ASSETS add constraint CMS_ASSETS_ASSET_ID_F_MLSFS
    foreign key(ASSET_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_ASSETS drop constraint CMS_ASSETS_MIME_TYPE_F_CYIOG;
alter table CMS_ASSETS add constraint CMS_ASSETS_MIME_TYPE_F_CYIOG
    foreign key(MIME_TYPE) references CMS_MIME_TYPES(MIME_TYPE);

alter table CMS_FILES drop constraint CMS_FILES_FILE_ID_F_OYUIZ;
alter table CMS_FILES add constraint CMS_FILES_FILE_ID_F_OYUIZ
    foreign key(FILE_ID) references CMS_ASSETS(ASSET_ID);

alter table CMS_FOLDERS drop constraint CMS_FOLDERS_FOLDER_ID_FK;
alter table CMS_FOLDERS add constraint CMS_FOLDERS_FOLDER_ID_F_8P2GE
    foreign key(FOLDER_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_FOLDERS drop constraint CMS_FOLDERS_INDEX_ID_FK;
alter table CMS_FOLDERS add constraint CMS_FOLDERS_INDEX_ID_F_B8P_0
    foreign key(INDEX_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_FORM_ITEM drop constraint CMS_FORM_ITEM_FK;
alter table CMS_FORM_ITEM add constraint CMS_FORM_ITEM_FK
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table CMS_FORM_SECTION_ITEM drop constraint CMS_FORM_SECTION_ITEM_ID_FK;
alter table CMS_FORM_SECTION_ITEM add constraint CMS_FORM_SECTION_ITEM_ID_FK
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table CMS_IMAGES drop constraint CMS_IMAGES_IMAGE_ID_F_70GZ8;
alter table CMS_IMAGES add constraint CMS_IMAGES_IMAGE_ID_F_70GZ8
    foreign key(IMAGE_ID) references CMS_ASSETS(ASSET_ID);

alter table CMS_ITEMS drop constraint CMS_ITEMS_ITEM_ID_FK;
alter table CMS_ITEMS add constraint CMS_ITEMS_ITEM_ID_FK
    foreign key(ITEM_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CMS_ITEM_TEMPLATE_MAP drop constraint CMS_ITM_ITEM_ID_FK;
alter table CMS_ITEM_TEMPLATE_MAP add constraint CMS_ITM_ITEM_ID_FK
    foreign key(ITEM_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_ITEM_TEMPLATE_MAP drop constraint CMS_ITM_TEMPLATE_ID_FK;
alter table CMS_ITEM_TEMPLATE_MAP add constraint CMS_ITM_TEMPLATE_ID_FK
    foreign key(TEMPLATE_ID) references CMS_TEMPLATES(TEMPLATE_ID);

alter table CMS_PAGES drop constraint CMS_PAGES_ITEM_ID_F_GYFQX;
alter table CMS_PAGES add constraint CMS_PAGES_ITEM_ID_F_GYFQX
    foreign key(ITEM_ID) references CMS_ITEMS(ITEM_ID);

alter table CMS_RESOURCES drop constraint CMS_RESOURCES_TYPE_ID_FK;
alter table CMS_RESOURCES add constraint CMS_RESOURCES_TYPE_F_IC7I1
    foreign key(TYPE) references CMS_RESOURCE_TYPES(TYPE);

alter table CMS_SECTION_TEMPLATE_MAP drop constraint CMS_STM_SECTION_ID_FK;
alter table CMS_SECTION_TEMPLATE_MAP add constraint CMS_STM_SECTION_ID_FK
    foreign key(SECTION_ID) references CONTENT_SECTIONS(SECTION_ID);

alter table CMS_SECTION_TEMPLATE_MAP drop constraint CMS_STM_TEMPLATE_ID_FK;
alter table CMS_SECTION_TEMPLATE_MAP add constraint CMS_STM_TEMPLATE_ID_FK
    foreign key(TEMPLATE_ID) references CMS_TEMPLATES(TEMPLATE_ID);

alter table CMS_SECTION_TEMPLATE_MAP drop constraint CMS_STM_TYPE_ID_FK;
alter table CMS_SECTION_TEMPLATE_MAP add constraint CMS_STM_TYPE_ID_FK
    foreign key(TYPE_ID) references CONTENT_TYPES(TYPE_ID);

alter table CMS_TASKS drop constraint CMS_TASKS_TASK_ID_FK;
alter table CMS_TASKS add constraint CMS_TASKS_TASK_ID_FK
    foreign key(TASK_ID) references CW_TASKS(TASK_ID);

alter table CMS_TEMPLATES drop constraint CMS_TEMPLATES_TEMPL_ID_FK;
alter table CMS_TEMPLATES add constraint CMS_TEMPLATES_TEMPL_ID_FK
    foreign key(TEMPLATE_ID) references CMS_TEXT(TEXT_ID);

alter table CMS_TEXT_PAGES drop constraint CMS_TEXT_PAGES_ITEM_ID_FK;
alter table CMS_TEXT_PAGES add constraint CMS_TEXT_PAGES_ITEM_ID_F_KFOX7
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table CMS_TEXT_PAGES drop constraint CMS_TEXT_PAGES_TEXT_ID_FK;
alter table CMS_TEXT_PAGES add constraint CMS_TEXT_PAGES_TEXT_ID_F_URI55
    foreign key(TEXT_ID) references CMS_TEXT(TEXT_ID);

alter table CMS_TEXT drop constraint CMS_TEXT_TEXT_ID_F_FWOJQ;
alter table CMS_TEXT add constraint CMS_TEXT_TEXT_ID_F_FWOJQ
    foreign key(TEXT_ID) references CMS_ASSETS(ASSET_ID);

alter table CMS_TOP_LEVEL_PAGES drop constraint CMS_TOP_LEV_PAG_PAG_ID_F_A6BHW;
alter table CMS_TOP_LEVEL_PAGES add constraint CMS_TOP_LEV_PAG_PAG_ID_F_A6BHW
    foreign key(PAGE_ID) references CMS_PAGES(ITEM_ID);

alter table CMS_USER_DEFINED_ITEMS drop constraint CMS_USE_DEF_ITE_ITE_ID_F_B1YXO;
alter table CMS_USER_DEFINED_ITEMS add constraint CMS_USE_DEF_ITE_ITE_ID_F_B1YXO
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table CONTENT_TYPES drop constraint CONTENT_TYPES_TYPE_ID_FK;
alter table CONTENT_TYPES add constraint CONTENT_TYPES_TYPE_ID_FK
    foreign key(TYPE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CONTENT_SECTIONS drop constraint CSECTIONS_SECTION_ID_FK;
alter table CONTENT_SECTIONS add constraint CSECTIONS_SECTION_ID_FK
    foreign key(SECTION_ID) references APPLICATIONS(APPLICATION_ID);

alter table CONTENT_SECTION_TYPE_MAP drop constraint CSTM_SECTION_ID_FK;
alter table CONTENT_SECTION_TYPE_MAP add constraint CONT_SEC_TYP_MAP_SEC_I_F_F_TNL
    foreign key(SECTION_ID) references CONTENT_SECTIONS(SECTION_ID);

alter table CONTENT_SECTION_TYPE_MAP drop constraint CSTM_TYPE_ID_FK;
alter table CONTENT_SECTION_TYPE_MAP add constraint CONT_SEC_TYP_MAP_TYP_I_F_Z6U9R
    foreign key(TYPE_ID) references CONTENT_TYPES(TYPE_ID);

alter table CT_AGENDAS drop constraint CT_AGENDAS_ITEM_ID_F_410HQ;
alter table CT_AGENDAS add constraint CT_AGENDAS_ITEM_ID_F_410HQ
    foreign key(ITEM_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CT_ARTICLES drop constraint CT_ARTICLES_ITEM_ID_F_6OFN1;
alter table CT_ARTICLES add constraint CT_ARTICLES_ITEM_ID_F_6OFN1
    foreign key(ITEM_ID) references CMS_ARTICLES(ARTICLE_ID);

alter table CT_EVENTS drop constraint CT_EVENTS_ITEM_ID_F_V7KJV;
alter table CT_EVENTS add constraint CT_EVENTS_ITEM_ID_F_V7KJV
    foreign key(ITEM_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CT_JOBS drop constraint CT_JOBS_ITEM_ID_F_ZRU4K;
alter table CT_JOBS add constraint CT_JOBS_ITEM_ID_F_ZRU4K
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table CT_LEGAL_NOTICES drop constraint CT_LEGA_NOTICE_ITEM_ID_F_B3KKQ;
alter table CT_LEGAL_NOTICES add constraint CT_LEGA_NOTICE_ITEM_ID_F_B3KKQ
    foreign key(ITEM_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CT_MINUTES drop constraint CT_MINUTES_ITEM_ID_F_8UHJ5;
alter table CT_MINUTES add constraint CT_MINUTES_ITEM_ID_F_8UHJ5
    foreign key(ITEM_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CT_MP_ARTICLES drop constraint CT_MP_ARTICL_ARTICL_ID_F_MZ8KI;
alter table CT_MP_ARTICLES add constraint CT_MP_ARTICL_ARTICL_ID_F_MZ8KI
    foreign key(ARTICLE_ID) references CMS_PAGES(ITEM_ID);

alter table CT_MP_SECTIONS drop constraint CT_MP_SECTIO_ARTICL_ID_F_NTNSJ;
alter table CT_MP_SECTIONS add constraint CT_MP_SECTIO_ARTICL_ID_F_NTNSJ
    foreign key(ARTICLE_ID) references CT_MP_ARTICLES(ARTICLE_ID);

alter table CT_MP_SECTIONS drop constraint CT_MP_SECTIO_SECTIO_ID_F_BX3AB;
alter table CT_MP_SECTIONS add constraint CT_MP_SECTIO_SECTIO_ID_F_BX3AB
    foreign key(SECTION_ID) references CMS_PAGES(ITEM_ID);

alter table CT_NEWS drop constraint CT_NEWS_ITEM_ID_F_MDUH5;
alter table CT_NEWS add constraint CT_NEWS_ITEM_ID_F_MDUH5
    foreign key(ITEM_ID) references CMS_ARTICLES(ARTICLE_ID);

alter table CT_PRESS_RELEASES drop constraint CT_PRES_RELEASE_ITE_ID_F_77VPR;
alter table CT_PRESS_RELEASES add constraint CT_PRES_RELEASE_ITE_ID_F_77VPR
    foreign key(ITEM_ID) references CMS_TEXT_PAGES(ITEM_ID);

alter table CT_SERVICE drop constraint CT_SERVICE_ITEM_ID_F_GZGD8;
alter table CT_SERVICE add constraint CT_SERVICE_ITEM_ID_F_GZGD8
    foreign key(ITEM_ID) references CMS_PAGES(ITEM_ID);

alter table LIFECYCLES drop constraint LIFECYCLES_CYCLE_ID_F_HYNPN;
alter table LIFECYCLES add constraint LIFECYCLES_CYCLE_ID_F_HYNPN
    foreign key(CYCLE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table LIFECYCLES drop constraint LIFECYCLE_DEFINITIO_ID_F_52O2C;
alter table LIFECYCLES add constraint LIFECYCLE_DEFINITIO_ID_F_52O2C
    foreign key(DEFINITION_ID) references LIFECYCLE_DEFINITIONS(DEFINITION_ID);

alter table LIFECYCLE_DEFINITIONS drop constraint LIFE_DEFINIT_DEFINI_ID_F_OHXSM;
alter table LIFECYCLE_DEFINITIONS add constraint LIFE_DEFINIT_DEFINI_ID_F_OHXSM
    foreign key(DEFINITION_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PHASES drop constraint PHASES_CYCLE_ID_F_PXRXC;
alter table PHASES add constraint PHASES_CYCLE_ID_F_PXRXC
    foreign key(CYCLE_ID) references LIFECYCLES(CYCLE_ID);

alter table PHASES drop constraint PHASES_DEFINITION_ID_F_LMB4Y;
alter table PHASES add constraint PHASES_DEFINITION_ID_F_LMB4Y
    foreign key(DEFINITION_ID) references PHASE_DEFINITIONS(PHASE_DEFINITION_ID);

alter table PHASES drop constraint PHASES_PHASE_ID_F_KDKQU;
alter table PHASES add constraint PHASES_PHASE_ID_F_KDKQU
    foreign key(PHASE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PHASE_DEFINITIONS drop constraint PHAS_DEFIN_CYC_DEFI_ID_F_Z5QHS;
alter table PHASE_DEFINITIONS add constraint PHAS_DEFIN_CYC_DEFI_ID_F_Z5QHS
    foreign key(CYCLE_DEFINITION_ID) references LIFECYCLE_DEFINITIONS(DEFINITION_ID);

alter table PHASE_DEFINITIONS drop constraint PHAS_DEFIN_PHA_DEFI_ID_F_OZ08Y;
alter table PHASE_DEFINITIONS add constraint PHAS_DEFIN_PHA_DEFI_ID_F_OZ08Y
    foreign key(PHASE_DEFINITION_ID) references ACS_OBJECTS(OBJECT_ID);

alter table SECTION_LIFECYCLE_DEF_MAP drop constraint SECT_LIF_DEF_MAP_CYC_D_F_8XA1H;
alter table SECTION_LIFECYCLE_DEF_MAP add constraint SECT_LIF_DEF_MAP_CYC_D_F_8XA1H
    foreign key(CYCLE_DEFINITION_ID) references LIFECYCLE_DEFINITIONS(DEFINITION_ID);

alter table SECTION_LIFECYCLE_DEF_MAP drop constraint SECT_LIF_DEF_MAP_SEC_I_F_7SI65;
alter table SECTION_LIFECYCLE_DEF_MAP add constraint SECT_LIF_DEF_MAP_SEC_I_F_7SI65
    foreign key(SECTION_ID) references CONTENT_SECTIONS(SECTION_ID);

alter table SECTION_WORKFLOW_TEMPLATE_MAP drop constraint SECT_WOR_TEM_MAP_SEC_I_F_9DEKW;
alter table SECTION_WORKFLOW_TEMPLATE_MAP add constraint SECT_WOR_TEM_MAP_SEC_I_F_9DEKW
    foreign key(SECTION_ID) references CONTENT_SECTIONS(SECTION_ID);

alter table SECTION_WORKFLOW_TEMPLATE_MAP drop constraint SECT_WOR_TEM_MAP_WF_TE_F_NE89I;
alter table SECTION_WORKFLOW_TEMPLATE_MAP add constraint SECT_WOR_TEM_MAP_WF_TE_F_NE89I
    foreign key(WF_TEMPLATE_ID) references CW_PROCESS_DEFINITIONS(PROCESS_DEF_ID);


--------------------------------------------------------------------------------
-- These constraints have changed their name.
--------------------------------------------------------------------------------
alter table CMS_IMAGE_MIME_TYPES drop constraint CMS_IMAGE_MIME_TYPES_FK;
alter table CMS_IMAGE_MIME_TYPES add constraint CMS_IMA_MIM_TYP_MIM_TY_F_S0ZSX
    foreign key(MIME_TYPE) references CMS_MIME_TYPES(MIME_TYPE);

alter table CMS_TEXT_MIME_TYPES drop constraint CMS_TEXT_MIME_TYPES_FK;
alter table CMS_TEXT_MIME_TYPES add constraint CMS_TEX_MIM_TYP_MIM_TY_F__TUBF
    foreign key(MIME_TYPE) references CMS_MIME_TYPES(MIME_TYPE);


declare
  version varchar2(4000);
  compatibility varchar2(4000);
begin
  DBMS_UTILITY.DB_VERSION (version, compatibility);
  if (compatibility >= '9.2.0.0.0') then
    -- The following ddl will only work on Oracle 9.2 or greater
    execute immediate 'alter table CMS_ARTICLES rename constraint CMS_ARTICLES_PK to CMS_ARTICLE_ARTICLE_ID_P_S67NQ';
    execute immediate 'alter table CMS_FOLDERS rename constraint CMS_FOLDERS_PK to CMS_FOLDERS_FOLDER_ID_P_OGLQK';
    execute immediate 'alter table CMS_IMAGE_MIME_TYPES rename constraint CMS_IMAGE_MIME_TYPES_PK to CMS_IMA_MIM_TYP_MIM_TY_P_9JRGN';
    execute immediate 'alter table CMS_MIME_EXTENSIONS rename constraint CMS_MIME_EXTENSIONS_PK to CMS_MIM_EXT_FIL_EXTENS_P_PNYHK';
    execute immediate 'alter table CMS_MIME_TYPES rename constraint CMS_MIME_TYPES_PK to CMS_MIM_TYPE_MIME_TYPE_P_KL0DS';
    execute immediate 'alter table CMS_RESOURCES rename constraint CMS_RESOURCES_PK to CMS_RESOURC_RESOURC_ID_P_034XH';
    execute immediate 'alter table CMS_RESOURCE_TYPES rename constraint CMS_RESOURCE_TYPES_PK to CMS_RESOURC_TYPES_TYPE_P_EO30H';
    execute immediate 'alter table CMS_TEXT_MIME_TYPES rename constraint CMS_TEXT_MIME_TYPES_PK to CMS_TEX_MIM_TYP_MIM_TY_P_3QBEC';
    execute immediate 'alter table CMS_TEXT_PAGES rename constraint CMS_TEXT_PAGES_PK to CMS_TEXT_PAGES_ITEM_ID_P_7TNKY';
    execute immediate 'alter table PUBLISH_TO_FS_FILES rename constraint PUBLISH_TO_FS_FILES_PK to PUBLISH_TO_FS_FILES_ID_P_J7XJ1';
    execute immediate 'alter table PUBLISH_TO_FS_FILES rename constraint PUBLISH_TO_FS_FILES_UN to PUBL_TO_FS_FIL_FIL_NAM_U_3ZKGD';

  end if;
end;
/
show errors;

--------------------------------------------------------------------------------
-- These indexes have changed their name.
--------------------------------------------------------------------------------
alter index CMS_ARTICLES_PK rename to CMS_ARTICLE_ARTICLE_ID_P_S67NQ;
alter index CMS_FOLDERS_PK rename to CMS_FOLDERS_FOLDER_ID_P_OGLQK;
alter index CMS_IMAGE_MIME_TYPES_PK rename to CMS_IMA_MIM_TYP_MIM_TY_P_9JRGN;
alter index CMS_MIME_EXTENSIONS_PK rename to CMS_MIM_EXT_FIL_EXTENS_P_PNYHK;
alter index CMS_MIME_TYPES_PK rename to CMS_MIM_TYPE_MIME_TYPE_P_KL0DS;
alter index CMS_RESOURCES_PK rename to CMS_RESOURC_RESOURC_ID_P_034XH;
alter index CMS_RESOURCE_TYPES_PK rename to CMS_RESOURC_TYPES_TYPE_P_EO30H;
alter index CMS_TEXT_MIME_TYPES_PK rename to CMS_TEX_MIM_TYP_MIM_TY_P_3QBEC;
alter index CMS_TEXT_PAGES_PK rename to CMS_TEXT_PAGES_ITEM_ID_P_7TNKY;
alter index PUBLISH_TO_FS_FILES_PK rename to PUBLISH_TO_FS_FILES_ID_P_J7XJ1;
alter index PUBLISH_TO_FS_FILES_UN rename to PUBL_TO_FS_FIL_FIL_NAM_U_3ZKGD;

--------------------------------------------------------------------------------
-- These indexes have been added.
--------------------------------------------------------------------------------
create index ACS_OBJECT_CYCL_MAP_ITM_IDX on ACS_OBJECT_LIFECYCLE_MAP(ITEM_ID);
create index CMS_ARTCL_IMAG_MAP_ART_ID_IDX on CMS_ARTICLE_IMAGE_MAP(ARTICLE_ID);
create index CMS_CTGRY_TMPL_MAP_SCTN_ID_IDX on CMS_CATEGORY_TEMPLATE_MAP(SECTION_ID);
create index CMS_CTGRY_TMPL_MAP_TMPL_ID_IDX on CMS_CATEGORY_TEMPLATE_MAP(TEMPLATE_ID);
create index CMS_CTGRY_TMPL_MAP_TYPE_ID_IDX on CMS_CATEGORY_TEMPLATE_MAP(TYPE_ID);
create index CMS_FRM_SCTN_WRPR_FRM_SCTN_IDX on CMS_FORM_SECTION_WRAPPER(FORM_SECTION_ID);
create index CMS_ITEMS_MASTER_ID_IDX on CMS_ITEMS(MASTER_ID);
create index CONTENT_SCTN_TYP_MAP_SCTN_IDX on CONTENT_SECTION_TYPE_MAP(SECTION_ID);
create index CT_CNT_GRP_IT_MAP_GRP_ID_IDX on CT_CONTENT_GROUP_ITEM_MAP(GROUP_ID);
create index CT_CNT_GRP_IT_MAP_RLTD_ITM_IDX on CT_CONTENT_GROUP_ITEM_MAP(RELATED_ITEM_ID);
create index CT_ITM_FILE_ATTCHMNTS_OWNR_IDX on CT_ITEM_FILE_ATTACHMENTS(OWNER_ID);
create index CW_TSK_GRP_ASSIGN_TASK_ID_IDX on CW_TASK_GROUP_ASSIGNEES(TASK_ID);
create index PORTLET_CONTENT_ITM_ITM_ID_IDX on PORTLET_CONTENT_ITEM(ITEM_ID);
create index PUBLISH_TO_FS_FILES_HST_ID_IDX on PUBLISH_TO_FS_FILES(HOST_ID);
create index PUBLISH_TO_FS_QUEUE_HST_ID_IDX on PUBLISH_TO_FS_QUEUE(HOST_ID);
create index SCTN_WRKFLW_TMPLT_MAP_SCTN_IDX on SECTION_WORKFLOW_TEMPLATE_MAP(SECTION_ID);
