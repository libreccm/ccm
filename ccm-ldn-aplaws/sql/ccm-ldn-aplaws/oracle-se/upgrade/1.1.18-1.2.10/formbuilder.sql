insert into cms_form_item (select * from ldn_frm_form_item);
insert into forms_dd_select (select * from ldn_frm_dd_select);
insert into forms_lstnr_conf_email (select * from ldn_frm_lstnr_conf_email);
insert into forms_lstnr_conf_redirect (select * from ldn_frm_lstnr_conf_redirect);
insert into forms_lstnr_simple_email (select * from ldn_frm_lstnr_simple_email);
insert into forms_lstnr_tmpl_email (select * from ldn_frm_lstnr_tmpl_email);
insert into forms_lstnr_xml_email (select * from ldn_frm_lstnr_xml_email);
insert into forms_widget_label (select * from ldn_frm_widget_label);
insert into bebop_object_type (select * from ldn_frm_object_type);
insert into forms_dataquery (select * from ldn_frm_dataquery);
insert into bebop_meta_object (select * from ldn_frm_meta_object);
insert into cms_form_section_item (select * from ldn_frm_section_item);

-- Items
update acs_objects
set
    object_type = 'com.arsdigita.cms.formbuilder.FormItem',
    default_domain_class = 'com.arsdigita.cms.formbuilder.FormItem'
where
    object_type = 'com.arsdigita.london.cms.forms.FormItem';

update content_types 
set classname = 'com.arsdigita.cms.formbuilder.FormItem',
    object_type = 'com.arsdigita.cms.formbuilder.FormItem'
where object_type = 'com.arsdigita.london.cms.forms.FormItem';

update acs_objects
set
    object_type = 'com.arsdigita.cms.formbuilder.FormSectionItem',
    default_domain_class = 'com.arsdigita.cms.formbuilder.FormSectionItem'
where
    object_type = 'com.arsdigita.london.cms.forms.FormSectionItem';

update content_types 
set classname = 'com.arsdigita.cms.formbuilder.FormSectionItem',
    object_type = 'com.arsdigita.cms.formbuilder.FormSectionItem'
where object_type = 'com.arsdigita.london.cms.forms.FormSectionItem';

-- Actions

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.actions.ConfirmEmailListener',
    default_domain_class =
        'com.arsdigita.formbuilder.actions.ConfirmEmailListener'
where
    object_type = 'com.arsdigita.london.forms.ConfirmEmailListener';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.actions.ConfirmRedirectListener',
    default_domain_class =
        'com.arsdigita.formbuilder.actions.ConfirmRedirectListener'
where
    object_type = 'com.arsdigita.london.forms.ConfirmRedirectListener';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.actions.SimpleEmailListener',
    default_domain_class =
        'com.arsdigita.formbuilder.actions.SimpleEmailListener'
where
    object_type = 'com.arsdigita.london.forms.SimpleEmailListener';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.actions.TemplateEmailListener',
    default_domain_class =
        'com.arsdigita.formbuilder.actions.TemplateEmailListener'
where
    object_type = 'com.arsdigita.london.forms.TemplateEmailListener';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.actions.XMLEmailListener',
    default_domain_class =
        'com.arsdigita.formbuilder.actions.XMLEmailListener'
where
    object_type = 'com.arsdigita.london.forms.XMLEmailListener';

-- Metadata
update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.ObjectType',
    default_domain_class = 'com.arsdigita.formbuilder.BebopObjectType'
where
    object_type = 'com.arsdigita.london.forms.ObjectType';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.MetaObject',
    default_domain_class =
        'com.arsdigita.formbuilder.MetaObject'
where
    object_type = 'com.arsdigita.london.forms.MetaObject';

-- Objects
update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.PersistentDataQuery',
    default_domain_class =
        'com.arsdigita.formbuilder.PersistentDataQuery'
where
    object_type = 'com.arsdigita.london.forms.PersistentDataQuery';

update acs_objects
set
    object_type = 'com.arsdigita.formbuilder.WidgetLabel',
    default_domain_class =
        'com.arsdigita.formbuilder.WidgetLabel'
where
    object_type = 'com.arsdigita.london.forms.WidgetLabel';

-- Remove legacy orphaned objects
delete from acs_objects
where object_id in (
    select item_id
    from cms_form_item
    where form_id is null
);

delete from acs_objects
where object_id in (
    select item_id
    from cms_form_section_item
    where form_section_id is null
);

delete from bebop_component_hierarchy
where component_id in (
    select object_id
    from acs_objects
    where object_type = 'com.arsdigita.formbuilder.FormSection'
    and not exists (
        select 1
        from bebop_form_sections
        where form_section_id = object_id
    )
);

delete from bebop_components
where component_id in (
    select object_id
    from acs_objects
    where object_type = 'com.arsdigita.formbuilder.FormSection'
    and not exists (
        select 1
        from bebop_form_sections
        where form_section_id = object_id
    )
);

delete from acs_objects
where object_type = 'com.arsdigita.formbuilder.FormSection'
and not exists (
    select 1
    from bebop_form_sections
    where form_section_id = object_id
);

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormActions' 
where component = 'com.arsdigita.london.cms.forms.ui.FormActions';

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormControls' 
where component = 'com.arsdigita.london.cms.forms.ui.FormControls';

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormProperties' 
where component = 'com.arsdigita.london.cms.forms.ui.FormProperties';

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormSectionActions' 
where component = 'com.arsdigita.london.cms.forms.ui.FormSectionActions';

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormSectionControls' 
where component = 'com.arsdigita.london.cms.forms.ui.FormSectionControls';

update authoring_steps 
set component = 'com.arsdigita.cms.ui.formbuilder.FormSectionProperties' 
where component = 'com.arsdigita.london.cms.forms.ui.FormSectionProperties';


update bebop_meta_object 
set class_name = 'com.arsdigita.london.forms.ui.editors.DataDrivenSelectForm'
where class_name = 'com.arsdigita.formbuilder.ui.editors.DataDrivenSelectForm';

update bebop_meta_object 
set class_name = 'com.arsdigita.london.forms.ui.editors.DataDrivenSelectForm'
where class_name = 'com.arsdigita.formbuilder.ui.editors.DataDrivenSelectForm';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.HiddenIDGenerator'
where class_name = 'com.arsdigita.london.forms.HiddenIDGenerator';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.DataDrivenSelect'
where class_name = 'com.arsdigita.london.forms.DataDrivenSelect';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.ConfirmEmailListener'
where class_name = 'com.arsdigita.london.forms.ConfirmEmailListener';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.ConfirmRedirectListener'
where class_name = 'com.arsdigita.london.forms.ConfirmRedirectListener';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.SimpleEmailListener'
where class_name = 'com.arsdigita.london.forms.SimpleEmailListener';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.SimpleEmailListener'
where class_name = 'com.arsdigita.london.forms.SimpleEmailListener';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.TemplateEmailListener'
where class_name = 'com.arsdigita.london.forms.TemplateEmailListener';

update bebop_meta_object 
set class_name = 'com.arsdigita.formbuilder.actions.XMLEmailListener'
where class_name = 'com.arsdigita.london.forms.XMLEmailListener';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.CheckboxGroupEditor'
where props_form = 'com.arsdigita.london.forms.ui.editors.CheckboxGroupEditor';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.CheckboxGroupEditor'
where props_form = 'com.arsdigita.london.forms.ui.editors.CheckboxGroupEditor';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.ConfirmEmailForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.ConfirmEmailForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.ConfirmRedirectForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.ConfirmRedirectForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.DataDrivenSelectForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.DataDrivenSelectForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.HiddenForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.HiddenForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.DateForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.DateForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.HiddenIDGeneratorForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.HiddenIDGeneratorForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.HiddenIDGeneratorForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.HiddenIDGeneratorForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.MultipleSelectEditor'
where props_form = 'com.arsdigita.london.forms.ui.editors.MultipleSelectEditor';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.PasswordForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.PasswordForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.RadioGroupEditor'
where props_form = 'com.arsdigita.london.forms.ui.editors.RadioGroupEditor';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.SingleSelectEditor'
where props_form = 'com.arsdigita.london.forms.ui.editors.SingleSelectEditor';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.SubmitForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.SubmitForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.SimpleEmailForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.SimpleEmailForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.TemplateEmailForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.TemplateEmailForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.TextAreaForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.TextAreaForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.TextFieldForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.TextFieldForm';

update bebop_meta_object 
set props_form = 'com.arsdigita.formbuilder.ui.editors.XMLEmailForm'
where props_form = 'com.arsdigita.london.forms.ui.editors.XMLEmailForm';
