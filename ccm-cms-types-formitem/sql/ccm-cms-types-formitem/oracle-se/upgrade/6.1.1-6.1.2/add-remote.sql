
alter table cms_form_item add remote_action char(1);
alter table cms_form_item add constraint cms_form_item_remt_act_ck check (remote_action in ('0', '1'));

alter table cms_form_item add remote_url varchar2(700);

alter table cms_form_item drop constraint CMS_FORM_ITEM_FRM_FK;

alter table cms_form_item add constraint CMS_FORM_ITEM_FORM_ID_F_FRZIF
foreign key (FORM_ID) references BEBOP_FORM_SECTIONS(FORM_SECTION_ID);

alter table cms_form_item drop constraint cms_form_item_fk;

ALTER TABLE cms_form_item ADD CONSTRAINT cms_form_item_item_id_f_gao21
    FOREIGN KEY (item_id) REFERENCES cms_pages(item_id);

alter table cms_form_item rename constraint cms_form_item_pk to cms_form_item_item_id_p_d370e;

