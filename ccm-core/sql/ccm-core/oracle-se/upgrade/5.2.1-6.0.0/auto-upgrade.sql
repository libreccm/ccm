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
-- $DateTime: 2004/08/16 18:10:38 $


--------------------------------------------------------------------------------
-- These columns went from 'not nullable' to 'nullable'.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These columns went from 'nullable' to 'not nullable'.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These default values for these columns changed.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES modify ( ABSTRACT_P default NULL );

--------------------------------------------------------------------------------
-- These char(1) boolean check constraints have been added.
--------------------------------------------------------------------------------
alter table APPLICATION_TYPES add constraint APPLICAT_TYP_SINGLET_P_C_NHLR1
    check (singleton_p in ('0', '1'));
alter table APPLICATION_TYPES add constraint APPLI_TYP_WOR_APPLIC_P_C_FJ5AF
    check (workspace_application_p in ('0', '1'));
alter table APPLICATION_TYPES add constraint APPL_TYP_HAS_EMB_VIE_P_C_06VIV
    check (has_embedded_view_p in ('0', '1'));
alter table APPLICATION_TYPES add constraint APPL_TYP_HAS_FUL_PAG_V_C_Q8KT_
    check (has_full_page_view_p in ('0', '1'));
alter table EMAIL_ADDRESSES add constraint EMAI_ADDRESS_BOUNCIN_P_C_5R1FZ
    check (bouncing_p in ('0', '1'));
alter table EMAIL_ADDRESSES add constraint EMAI_ADDRESS_VERIFIE_P_C_WF205
    check (verified_p in ('0', '1'));
alter table LUCENE_DOCS add constraint LUCENE_DOCS_IS_DELETED_C_7NC6C
    check (is_deleted in ('0', '1'));
alter table PORTALS add constraint PORTALS_TEMPLATE_P_C_NFFG9
    check (template_p in ('0', '1'));
alter table SITE_NODES add constraint SITE_NODES_DIRECTORY_P_C_N_URI
    check (directory_p in ('0', '1'));
alter table SITE_NODES add constraint SITE_NODES_PATTERN_P_C_NIJIJ
    check (pattern_p in ('0', '1'));

--------------------------------------------------------------------------------
-- These 'not null' check constraints have been dropped.
--------------------------------------------------------------------------------
alter table CW_TASK_GROUP_ASSIGNEES drop constraint GROUP_TASK_ID_NN;
alter table CW_TASK_GROUP_ASSIGNEES modify ( GROUP_ID not null );
alter table CW_TASK_LISTENERS drop constraint LISTEN_TASK_ID_NN;
alter table CW_TASK_LISTENERS modify ( LISTENER_TASK_ID not null );
alter table CW_TASK_DEPENDENCIES drop constraint TASK_DEP_ID_NN;
alter table CW_TASK_DEPENDENCIES modify ( DEPENDENT_TASK_ID not null );
alter table CW_TASK_DEPENDENCIES drop constraint TASK_DEP_TASK_ID_NN;
alter table CW_TASK_DEPENDENCIES modify ( TASK_ID not null );
alter table CW_TASK_GROUP_ASSIGNEES drop constraint TASK_GROUP_TASK_ID_NN;
alter table CW_TASK_GROUP_ASSIGNEES modify ( TASK_ID not null );
alter table CW_TASK_LISTENERS drop constraint TASK_LISTEN_TASK_ID_NN;
alter table CW_TASK_LISTENERS modify ( TASK_ID not null );
alter table CW_TASK_USER_ASSIGNEES drop constraint TASK_USER_TASK_ID_NN;
alter table CW_TASK_USER_ASSIGNEES modify ( TASK_ID not null );
alter table CW_TASK_USER_ASSIGNEES drop constraint USER_TASK_ID_NN;
alter table CW_TASK_USER_ASSIGNEES modify ( USER_ID not null );

--------------------------------------------------------------------------------
-- These foreign key constraints have change their action for 'on delete'.
-- Their names may have changed as well.
--------------------------------------------------------------------------------
alter table ACS_PERMISSIONS drop constraint ACS_PERMISSIO_GRANT_ID_F_VMO0E;
alter table ACS_PERMISSIONS add constraint ACS_PERMISSIO_GRANT_ID_F_VMO0E
    foreign key(GRANTEE_ID) references PARTIES(PARTY_ID);

alter table ACS_PERMISSIONS drop constraint ACS_PERMISSIO_OBJEC_ID_F_5SWTM;
alter table ACS_PERMISSIONS add constraint ACS_PERMISSIO_OBJEC_ID_F_5SWTM
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table ACS_PERMISSIONS drop constraint ACS_PERMISSI_PRIVILEGE_F_P76EV;
alter table ACS_PERMISSIONS add constraint ACS_PERMISSI_PRIVILEGE_F_P76EV
    foreign key(PRIVILEGE) references ACS_PRIVILEGES(PRIVILEGE);

alter table ACS_STYLESHEETS drop constraint ACS_STYLESH_STYLESH_ID_F_2FIOK;
alter table ACS_STYLESHEETS add constraint ACS_STYLESH_STYLESH_ID_F_2FIOK
    foreign key(STYLESHEET_ID) references ACS_OBJECTS(OBJECT_ID);

alter table ACS_STYLESHEET_NODE_MAP drop constraint ACS_STY_NOD_MAP_NOD_ID_F_Q55Q3;
alter table ACS_STYLESHEET_NODE_MAP add constraint ACS_STY_NOD_MAP_NOD_ID_F_Q55Q3
    foreign key(NODE_ID) references SITE_NODES(NODE_ID);

alter table ACS_STYLESHEET_NODE_MAP drop constraint ACS_STY_NOD_MAP_STY_ID_F_GUEJ5;
alter table ACS_STYLESHEET_NODE_MAP add constraint ACS_STY_NOD_MAP_STY_ID_F_GUEJ5
    foreign key(STYLESHEET_ID) references ACS_STYLESHEETS(STYLESHEET_ID);

alter table ACS_STYLESHEET_TYPE_MAP drop constraint ACS_STY_TYP_MAP_PAC_TY_F_EMKUA;
alter table ACS_STYLESHEET_TYPE_MAP add constraint ACS_STY_TYP_MAP_PAC_TY_F_EMKUA
    foreign key(PACKAGE_TYPE_ID) references APM_PACKAGE_TYPES(PACKAGE_TYPE_ID);

alter table ACS_STYLESHEET_TYPE_MAP drop constraint ACS_STY_TYP_MAP_STY_ID_F_38X8P;
alter table ACS_STYLESHEET_TYPE_MAP add constraint ACS_STY_TYP_MAP_STY_ID_F_38X8P
    foreign key(STYLESHEET_ID) references ACS_STYLESHEETS(STYLESHEET_ID);

alter table APM_PACKAGES drop constraint APM_PACKAGE_PACKAGE_ID_F_46MAY;
alter table APM_PACKAGES add constraint APM_PACKAGE_PACKAGE_ID_F_46MAY
    foreign key(PACKAGE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table APM_PACKAGES drop constraint APM_PACKA_PACKA_TYP_ID_F_ADR4W;
alter table APM_PACKAGES add constraint APM_PACKA_PACKA_TYP_ID_F_ADR4W
    foreign key(PACKAGE_TYPE_ID) references APM_PACKAGE_TYPES(PACKAGE_TYPE_ID);

alter table APM_PACKAGE_TYPE_LISTENER_MAP drop constraint APM_PAC_TYP_LIS_MAP_LI_F_I78GW;
alter table APM_PACKAGE_TYPE_LISTENER_MAP add constraint APM_PAC_TYP_LIS_MAP_LI_F_I78GW
    foreign key(LISTENER_ID) references APM_LISTENERS(LISTENER_ID);

alter table APM_PACKAGE_TYPE_LISTENER_MAP drop constraint APM_PAC_TYP_LIS_MAP_PA_F_0_QFW;
alter table APM_PACKAGE_TYPE_LISTENER_MAP add constraint APM_PAC_TYP_LIS_MAP_PA_F_0_QFW
    foreign key(PACKAGE_TYPE_ID) references APM_PACKAGE_TYPES(PACKAGE_TYPE_ID);

alter table APPLICATIONS drop constraint APPLICATI_APPLICATI_ID_F_A35G2;
alter table APPLICATIONS add constraint APPLICATI_APPLICATI_ID_F_A35G2
    foreign key(APPLICATION_ID) references ACS_OBJECTS(OBJECT_ID);

alter table APPLICATION_TYPE_PRIVILEGE_MAP drop constraint APPL_TYP_PRI_MAP_APP_T_F_KGRFJ;
alter table APPLICATION_TYPE_PRIVILEGE_MAP add constraint APPL_TYP_PRI_MAP_APP_T_F_KGRFJ
    foreign key(APPLICATION_TYPE_ID) references APPLICATION_TYPES(APPLICATION_TYPE_ID);

alter table APPLICATION_TYPE_PRIVILEGE_MAP drop constraint APPL_TYP_PRI_MAP_PRIVI_F_S3PWB;
alter table APPLICATION_TYPE_PRIVILEGE_MAP add constraint APPL_TYP_PRI_MAP_PRIVI_F_S3PWB
    foreign key(PRIVILEGE) references ACS_PRIVILEGES(PRIVILEGE);

alter table ACS_AUDITING drop constraint AUDITED_CREATION_USER_FK;
alter table ACS_AUDITING add constraint AUDITED_CREATION_USER_FK
    foreign key(CREATION_USER) references USERS(USER_ID);

alter table ACS_AUDITING drop constraint AUDITED_MODIFYING_USER_FK;
alter table ACS_AUDITING add constraint AUDITED_MODIFYING_USER_FK
    foreign key(MODIFYING_USER) references USERS(USER_ID);

alter table BEBOP_FORM_PROCESS_LISTENERS drop constraint BEBOP_FORM_PROCESS_LSTNR_FS_FK;
alter table BEBOP_FORM_PROCESS_LISTENERS add constraint BEBOP_FORM_PROCESS_LSTNR_FS_FK
    foreign key(FORM_SECTION_ID) references BEBOP_FORM_SECTIONS(FORM_SECTION_ID);

alter table BEBOP_FORM_PROCESS_LISTENERS drop constraint BEBOP_FORM_PROCESS_LSTNR_LI_FK;
alter table BEBOP_FORM_PROCESS_LISTENERS add constraint BEBOP_FORM_PROCESS_LSTNR_LI_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table BEBOP_META_OBJECT drop constraint BEBOP_META_OBJ_OBJECT_ID_FK;
alter table BEBOP_META_OBJECT add constraint BEBOP_META_OBJ_OBJECT_ID_FK
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table BEBOP_OBJECT_TYPE drop constraint BEBOP_OBJECT_TYPE_TYPE_ID_FK;
alter table BEBOP_OBJECT_TYPE add constraint BEBOP_OBJECT_TYPE_TYPE_ID_FK
    foreign key(TYPE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table BEBOP_PROCESS_LISTENERS drop constraint BEBOP_PROCESS_LISTENERS_FK;
alter table BEBOP_PROCESS_LISTENERS add constraint BEBOP_PROCESS_LISTENERS_FK
    foreign key(LISTENER_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CAT_CATEGORIES drop constraint CAT_CATEGORIES_FK;
alter table CAT_CATEGORIES add constraint CAT_CATEGORI_CATEGO_ID_F__XTWR
    foreign key(CATEGORY_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CAT_CATEGORY_CATEGORY_MAP drop constraint CAT_CAT_MAP_CATEGORY_ID_FK;
alter table CAT_CATEGORY_CATEGORY_MAP add constraint CAT_CAT_MAP_CATEGORY_ID_FK
    foreign key(RELATED_CATEGORY_ID) references CAT_CATEGORIES(CATEGORY_ID);

alter table CAT_CATEGORY_CATEGORY_MAP drop constraint CAT_CAT_MAP_PARENT_ID_FK;
alter table CAT_CATEGORY_CATEGORY_MAP add constraint CAT_CAT_MAP_PARENT_ID_FK
    foreign key(CATEGORY_ID) references CAT_CATEGORIES(CATEGORY_ID);

alter table CAT_CATEGORY_PURPOSE_MAP drop constraint CAT_CAT_PUR_MAP_CAT_ID_FK;
alter table CAT_CATEGORY_PURPOSE_MAP add constraint CAT_CAT_PUR_MAP_CAT_ID_FK
    foreign key(CATEGORY_ID) references CAT_CATEGORIES(CATEGORY_ID);

alter table CAT_OBJECT_CATEGORY_MAP drop constraint CAT_OBJ_CAT_MAP_CAT_ID_FK;
alter table CAT_OBJECT_CATEGORY_MAP add constraint CAT_OBJ_CAT_MAP_CAT_ID_FK
    foreign key(CATEGORY_ID) references CAT_CATEGORIES(CATEGORY_ID);

alter table CAT_OBJECT_CATEGORY_MAP drop constraint CAT_OBJ_MAP_OBJECT_ID_FK;
alter table CAT_OBJECT_CATEGORY_MAP add constraint CAT_OBJ_MAP_OBJECT_ID_FK
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CAT_CATEGORY_PURPOSE_MAP drop constraint CAT_OBJ_MAP_PURPOSE_ID_FK;
alter table CAT_CATEGORY_PURPOSE_MAP add constraint CAT_OBJ_MAP_PURPOSE_ID_FK
    foreign key(PURPOSE_ID) references CAT_PURPOSES(PURPOSE_ID);

alter table CAT_PURPOSES drop constraint CAT_PURPOSES_PURPOSE_ID_FK;
alter table CAT_PURPOSES add constraint CAT_PURPOSES_PURPOSE_ID_FK
    foreign key(PURPOSE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table CAT_ROOT_CAT_OBJECT_MAP drop constraint CAT_ROO_CAT_OBJ_MAP_CA_F_JQVMD;
alter table CAT_ROOT_CAT_OBJECT_MAP add constraint CAT_ROO_CAT_OBJ_MAP_CA_F_JQVMD
    foreign key(CATEGORY_ID) references CAT_CATEGORIES(CATEGORY_ID);

alter table CAT_ROOT_CAT_OBJECT_MAP drop constraint CAT_ROO_CAT_OBJ_MAP_OB_F_ANFMX;
alter table CAT_ROOT_CAT_OBJECT_MAP add constraint CAT_ROO_CAT_OBJ_MAP_OB_F_ANFMX
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table FORMS_DD_SELECT drop constraint FORMS_DDS_WIDGET_ID_FK;
alter table FORMS_DD_SELECT add constraint FORMS_DDS_WIDGET_ID_FK
    foreign key(WIDGET_ID) references BEBOP_WIDGETS(WIDGET_ID);

alter table FORMS_DATAQUERY drop constraint FORMS_DQ_QUERY_ID_FK;
alter table FORMS_DATAQUERY add constraint FORMS_DQ_QUERY_ID_FK
    foreign key(QUERY_ID) references ACS_OBJECTS(OBJECT_ID);

alter table FORMS_LSTNR_CONF_EMAIL drop constraint FORMS_LSTNR_CONF_EMAIL_FK;
alter table FORMS_LSTNR_CONF_EMAIL add constraint FORMS_LSTNR_CONF_EMAIL_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table FORMS_LSTNR_CONF_REDIRECT drop constraint FORMS_LSTNR_CONF_REDIRECT_FK;
alter table FORMS_LSTNR_CONF_REDIRECT add constraint FORMS_LSTNR_CONF_REDIRECT_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table FORMS_LSTNR_SIMPLE_EMAIL drop constraint FORMS_LSTNR_SIMPLE_EMAIL_FK;
alter table FORMS_LSTNR_SIMPLE_EMAIL add constraint FORMS_LSTNR_SIMPLE_EMAIL_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table FORMS_LSTNR_TMPL_EMAIL drop constraint FORMS_LSTNR_TMPL_EMAIL_FK;
alter table FORMS_LSTNR_TMPL_EMAIL add constraint FORMS_LSTNR_TMPL_EMAIL_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table FORMS_LSTNR_XML_EMAIL drop constraint FORMS_LSTNR_XML_EMAIL_FK;
alter table FORMS_LSTNR_XML_EMAIL add constraint FORMS_LSTNR_XML_EMAIL_FK
    foreign key(LISTENER_ID) references BEBOP_PROCESS_LISTENERS(LISTENER_ID);

alter table FORMS_WIDGET_LABEL drop constraint FORMS_WGT_LABEL_LABEL_ID_FK;
alter table FORMS_WIDGET_LABEL add constraint FORMS_WGT_LABEL_LABEL_ID_FK
    foreign key(LABEL_ID) references BEBOP_WIDGETS(WIDGET_ID);

alter table GROUPS drop constraint GROUPS_GROUP_ID_F_L4TVR;
alter table GROUPS add constraint GROUPS_GROUP_ID_F_L4TVR
    foreign key(GROUP_ID) references PARTIES(PARTY_ID);

alter table CW_TASK_GROUP_ASSIGNEES drop constraint GROUP_TASK_ID_FK;
alter table CW_TASK_GROUP_ASSIGNEES add constraint CW_TAS_GRO_ASSI_GRO_ID_F_OR5KJ
    foreign key(GROUP_ID) references GROUPS(GROUP_ID);

alter table GROUP_MEMBER_MAP drop constraint GROU_MEMBE_MAP_GROU_ID_F_D7LHM;
alter table GROUP_MEMBER_MAP add constraint GROU_MEMBE_MAP_GROU_ID_F_D7LHM
    foreign key(GROUP_ID) references GROUPS(GROUP_ID);

alter table GROUP_MEMBER_MAP drop constraint GROU_MEMB_MAP_MEMBE_ID_F_BS3U_;
alter table GROUP_MEMBER_MAP add constraint GROU_MEMB_MAP_MEMBE_ID_F_BS3U_
    foreign key(MEMBER_ID) references USERS(USER_ID);

alter table GROUP_SUBGROUP_MAP drop constraint GROU_SUBGRO_MAP_GRO_ID_F_TODNR;
alter table GROUP_SUBGROUP_MAP add constraint GROU_SUBGRO_MAP_GRO_ID_F_TODNR
    foreign key(GROUP_ID) references GROUPS(GROUP_ID);

alter table GROUP_SUBGROUP_MAP drop constraint GROU_SUBG_MAP_SUBGR_ID_F_1JO4E;
alter table GROUP_SUBGROUP_MAP add constraint GROU_SUBG_MAP_SUBGR_ID_F_1JO4E
    foreign key(SUBGROUP_ID) references GROUPS(GROUP_ID);

alter table CW_TASK_LISTENERS drop constraint LISTEN_TASK_ID_FK;
alter table CW_TASK_LISTENERS add constraint CW_TAS_LIST_LIS_TAS_ID_F_X1N02
    foreign key(LISTENER_TASK_ID) references CW_TASKS(TASK_ID);

alter table MESSAGES drop constraint MESSAGES_MESSAGE_ID_FK;
alter table MESSAGES add constraint MESSAGES_MESSAGE_ID_FK
    foreign key(MESSAGE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table MESSAGE_PARTS drop constraint MESSAGE_PARTS_MESSAGE_ID_FK;
alter table MESSAGE_PARTS add constraint MESSAGE_PARTS_MESSAGE_ID_FK
    foreign key(MESSAGE_ID) references MESSAGES(MESSAGE_ID);

alter table OBJECT_CONTEXT drop constraint OBJEC_CONTEX_CONTEX_ID_F_CRDH1;
alter table OBJECT_CONTEXT add constraint OBJEC_CONTEX_CONTEX_ID_F_CRDH1
    foreign key(CONTEXT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table OBJECT_CONTEXT drop constraint OBJEC_CONTEX_OBJECT_ID_F_MBUXE;
alter table OBJECT_CONTEXT add constraint OBJEC_CONTEX_OBJECT_ID_F_MBUXE
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table OBJECT_CONTAINER_MAP drop constraint OBJE_CONTAI_MAP_OBJ_ID_F_GUADS;
alter table OBJECT_CONTAINER_MAP add constraint OBJE_CONTAI_MAP_OBJ_ID_F_GUADS
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table OBJECT_CONTAINER_MAP drop constraint OBJE_CONT_MAP_CONTA_ID_F_V66B1;
alter table OBJECT_CONTAINER_MAP add constraint OBJE_CONT_MAP_CONTA_ID_F_V66B1
    foreign key(CONTAINER_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PARTIES drop constraint PARTIES_PARTY_ID_F_J4K1I;
alter table PARTIES add constraint PARTIES_PARTY_ID_F_J4K1I
    foreign key(PARTY_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PARTY_EMAIL_MAP drop constraint PART_EMAI_MAP_PARTY_ID_F_7_00_;
alter table PARTY_EMAIL_MAP add constraint PART_EMAI_MAP_PARTY_ID_F_7_00_
    foreign key(PARTY_ID) references PARTIES(PARTY_ID);

alter table PERSISTENCE_DYNAMIC_OT drop constraint PERSIST_DYNAMIC_OT_PDL_ID_FK;
alter table PERSISTENCE_DYNAMIC_OT add constraint PERSIST_DYNAMIC_OT_PDL_ID_FK
    foreign key(PDL_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PERSISTENCE_DYNAMIC_ASSOC drop constraint PERS_DYN_ASSOC_PDL_ID_FK;
alter table PERSISTENCE_DYNAMIC_ASSOC add constraint PERS_DYN_ASSOC_PDL_ID_FK
    foreign key(PDL_ID) references ACS_OBJECTS(OBJECT_ID);

alter table PORTALS drop constraint PORTALS_PORTAL_ID_F_KBX1T;
alter table PORTALS add constraint PORTALS_PORTAL_ID_F_KBX1T
    foreign key(PORTAL_ID) references APPLICATIONS(APPLICATION_ID);

alter table PORTLETS drop constraint PORTLETS_PORTLET_ID_F_ERF4O;
alter table PORTLETS add constraint PORTLETS_PORTLET_ID_F_ERF4O
    foreign key(PORTLET_ID) references APPLICATIONS(APPLICATION_ID);

alter table PREFERENCES drop constraint PREFERENCES_PARENT_FK;
alter table PREFERENCES add constraint PREFERENCES_PARENT_FK
    foreign key(PARENT_ID) references PREFERENCES(PREFERENCE_ID);

alter table CW_PROCESSES drop constraint PROCESSES_OBJECT_FK;
alter table CW_PROCESSES add constraint PROCESSES_OBJECT_FK
    foreign key(OBJECT_ID) references ACS_OBJECTS(OBJECT_ID);

alter table ROLES drop constraint ROLE_IMPLICIT_GROUP_ID_F_O6G0P;
alter table ROLES add constraint ROLE_IMPLICIT_GROUP_ID_F_O6G0P
    foreign key(IMPLICIT_GROUP_ID) references GROUPS(GROUP_ID);

alter table SITE_NODES drop constraint SITE_NODES_NODE_ID_F_N1M2Y;
alter table SITE_NODES add constraint SITE_NODES_NODE_ID_F_N1M2Y
    foreign key(NODE_ID) references ACS_OBJECTS(OBJECT_ID);

alter table SITE_NODES drop constraint SITE_NODES_PARENT_ID_F_SACAV;
alter table SITE_NODES add constraint SITE_NODES_PARENT_ID_F_SACAV
    foreign key(PARENT_ID) references SITE_NODES(NODE_ID);

alter table CW_TASK_COMMENTS drop constraint TASK_COMMENTS_TASK_ID_FK;
alter table CW_TASK_COMMENTS add constraint TASK_COMMENTS_TASK_ID_FK
    foreign key(TASK_ID) references CW_TASKS(TASK_ID);

alter table CW_TASK_DEPENDENCIES drop constraint TASK_DEF_ID_FK;
alter table CW_TASK_DEPENDENCIES add constraint CW_TAS_DEPE_DEP_TAS_ID_F_BN0M5
    foreign key(DEPENDENT_TASK_ID) references CW_TASKS(TASK_ID);

alter table CW_TASK_DEPENDENCIES drop constraint TASK_DEP_TASK_ID_FK;
alter table CW_TASK_DEPENDENCIES add constraint CW_TAS_DEPENDEN_TAS_ID_F_B1UOZ
    foreign key(TASK_ID) references CW_TASKS(TASK_ID);

alter table CW_TASK_GROUP_ASSIGNEES drop constraint TASK_GROUP_TASK_ID_FK;
alter table CW_TASK_GROUP_ASSIGNEES add constraint CW_TAS_GRO_ASSI_TAS_ID_F_MHI2K
    foreign key(TASK_ID) references CW_USER_TASKS(TASK_ID);

alter table CW_TASK_LISTENERS drop constraint TASK_LISTEN_TASK_ID_FK;
alter table CW_TASK_LISTENERS add constraint CW_TAS_LISTENER_TAS_ID_F_S2FJ9
    foreign key(TASK_ID) references CW_TASKS(TASK_ID);

alter table USERS drop constraint USERS_USER_ID_F_T_LSO;
alter table USERS add constraint USERS_USER_ID_F_T_LSO
    foreign key(USER_ID) references PARTIES(PARTY_ID);

alter table USER_AUTHENTICATION drop constraint USER_AUTHENTICA_AUT_ID_F_0BGPJ;
alter table USER_AUTHENTICATION add constraint USER_AUTHENTICA_AUT_ID_F_0BGPJ
    foreign key(AUTH_ID) references PARTIES(PARTY_ID);

alter table CW_USER_TASKS drop constraint USER_TASKS_TASK_ID_FK;
alter table CW_USER_TASKS add constraint USER_TASKS_TASK_ID_FK
    foreign key(TASK_ID) references CW_TASKS(TASK_ID);

alter table CW_TASK_USER_ASSIGNEES drop constraint USER_TASK_ID_FK;
alter table CW_TASK_USER_ASSIGNEES add constraint CW_TAS_USE_ASSI_USE_ID_F_W856_
    foreign key(USER_ID) references USERS(USER_ID);


--------------------------------------------------------------------------------
-- These constraints have changed their name.
--------------------------------------------------------------------------------

declare
  version varchar2(4000);
  compatibility varchar2(4000);
begin
  DBMS_UTILITY.DB_VERSION (version, compatibility);
  if (compatibility >= '9.2.0.0.0') then
    -- The following ddl will only work on Oracle 9.2 or greater
    execute immediate 'alter table CAT_CATEGORIES rename constraint CAT_CATEGORIES_PK to CAT_CATEGORI_CATEGO_ID_P_YEPRQ';

  end if;
end;
/
show errors;

--------------------------------------------------------------------------------
-- These indexes have changed their name.
--------------------------------------------------------------------------------
alter index CAT_CATEGORIES_PK rename to CAT_CATEGORI_CATEGO_ID_P_YEPRQ;
alter index TASK_DEPENDENCIES_PK rename to CW_TAS_DEP_DEP_TAS_ID__P_HDZWS;
alter index TASK_USER_ASSIGNEES_PK rename to CW_TAS_USE_ASS_TAS_ID__P_VSDYQ;

--------------------------------------------------------------------------------
-- These indexes have been added.
--------------------------------------------------------------------------------
create index GROUP_MEMBER_MAP_GROUP_ID_IDX on GROUP_MEMBER_MAP(GROUP_ID);
create index GROUP_SUBGROUP_MAP_GRP_ID_IDX on GROUP_SUBGROUP_MAP(GROUP_ID);
create index VCX_OBJ_CHANGES_TXN_ID_IDX on VCX_OBJ_CHANGES(TXN_ID);
create index VCX_OPERATIONS_CHANGE_ID_IDX on VCX_OPERATIONS(CHANGE_ID);
create index VCX_OPERATIONS_CLASS_ID_IDX on VCX_OPERATIONS(CLASS_ID);
create index VCX_OPERATIONS_EVNT_TYP_ID_IDX on VCX_OPERATIONS(EVENT_TYPE_ID);
create index VCX_TAGS_TXN_ID_IDX on VCX_TAGS(TXN_ID);
create index VCX_TXNS_MODIFYING_USER_IDX on VCX_TXNS(MODIFYING_USER);
