/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormValidationException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TooManyListenersException;

/**
 * This class contains a form component to create a new content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @version $Revision: #21 $ $Date: 2004/08/17 $
 */
public class CreateType extends CMSForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener, FormValidationListener {

    private static final String DEFAULT_UDITEM_TYPE = ContentPage.BASE_DATA_OBJECT_TYPE;
    private static final String CATEGORIZATION_COMPONENT =
            "com.arsdigita.cms.ui.authoring.ItemCategoryStep";
    private final static Logger s_log =
            Logger.getLogger(CreateType.class.getName());
    //private static final ObjectType TEST_TYPE = SessionManager.getMetadataRoot().getObjectType("com.arsdigita.kernel.Party");
    private static final String CREATION_COMPONENT = "com.arsdigita.cms.ui.authoring.PageCreateDynamic";
    private Hidden m_id;
    private TextField m_name;
    private TextField m_label;
    private TextArea m_description;
    private SingleSelect m_parentTypeSelect;
    private SingleSelect m_lifecycleSelect;
    private SingleSelect m_workflowSelect;
    private Submit m_submit;
    private Submit m_cancel;
    private SingleSelectionModel m_types = null;
    DynamicObjectType dot;

    public CreateType() {
        this(null);
    }

    public CreateType(SingleSelectionModel m) {
        super("NewContentItemDefinition");
        if (m != null) {
            m_types = m;
        }

        m_id = new Hidden(new BigDecimalParameter("id"));
        m_id.addValidationListener(new NotNullValidationListener());
        add(m_id);

        Label heading = new Label(GlobalizationUtil.globalize("cms.ui.type.add"));
        heading.setFontWeight(Label.BOLD);
        add(heading, ColumnPanel.FULL_WIDTH);

        add(new Label(GlobalizationUtil.globalize("cms.ui.name")));
        m_name = new TextField(new StringParameter("name"));
        m_name.addValidationListener(new NotEmptyValidationListener());
        m_name.setSize(40);
        m_name.setMaxLength(1000);
        add(m_name);
        add(new Label(GlobalizationUtil.globalize("cms.ui.type.label")));
        m_label = new TextField(new StringParameter("label"));

        m_label.addValidationListener(new StringInRangeValidationListener(1, 1000));
        m_label.setSize(40);
        m_label.setMaxLength(1000);
        add(m_label);
        add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH);

        add(new Label(GlobalizationUtil.globalize("cms.ui.description")));
        m_description = new TextArea(new StringParameter("description"));
        m_description.addValidationListener(new StringLengthValidationListener(4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.parent")));
        m_parentTypeSelect = new SingleSelect(new BigDecimalParameter("parentType"));
        try {
            m_parentTypeSelect.addPrintListener(new ParentTypeSelectPrintListener());
        } catch (TooManyListenersException e) {
            s_log.error("Too many listeners", e);
            throw new UncheckedWrapperException(e);
        }
        add(m_parentTypeSelect);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.lifecycle")));
        m_lifecycleSelect = new SingleSelect(new BigDecimalParameter("lifecycle"));
        try {
            m_lifecycleSelect.addPrintListener(new LifecycleSelectPrintListener());
        } catch (TooManyListenersException e) {
            s_log.error("Too many listeners", e);
            throw new UncheckedWrapperException(e);
        }
        add(m_lifecycleSelect);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.workflow")));
        m_workflowSelect = new SingleSelect(new BigDecimalParameter("workflow"));
        try {
            m_workflowSelect.addPrintListener(new WorkflowSelectPrintListener());
        } catch (TooManyListenersException e) {
            s_log.error("Too many listeners", e);
            throw new UncheckedWrapperException(e);
        }
        add(m_workflowSelect);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Create Content Type");
        s.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        //add the listeners
        addProcessListener(this);
        addInitListener(this);
        addSubmissionListener(new TypeSecurityListener());
        addSubmissionListener(this);
        addValidationListener(this);
    }

    // if this form is cancelled
    @Override
    public boolean isCancelled(PageState s) {
        return m_cancel.isSelected(s);
    }

    /*
     *  form validation for verifying unique content type names for
     * the particular content section, and if the new type name is
     * legal dynamic object type name
     */
    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        String typeLabel = (String) m_label.getValue(s);
        String typeName = (String) m_name.getValue(s);

        ContentSection section = CMS.getContext().getContentSection();
        ContentTypeCollection contentTypes = section.getContentTypes();

        boolean dupe = false;

        while (contentTypes.next() && dupe == false) {
            if (contentTypes.getContentType().getName().compareTo(typeLabel) == 0) {
                dupe = true;
            }
        }

        if (dupe == true) {
            throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.type.name_not_unique"));
        } else {

            for (int i = 0; i < typeName.length(); i++) {
                char c = typeName.charAt(i);
                if (Character.isWhitespace(c)) {
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.type.name_has_whitespace"));
                } else if (!Character.isLetterOrDigit(c)) {
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.type.name_not_alphanumeric"));
                }
            }

            return;
        }
    }

    /**
     * Processes the form
     * creates a new dynamic object type whose supertype is
     * UserDefinedContentItem, and set that to be the associated
     * object type of the new ContentType
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        ContentSection section = CMS.getContext().getContentSection();

        BigDecimal key = (BigDecimal) m_id.getValue(state);
        String name = (String) m_name.getValue(state);
        String label = (String) m_label.getValue(state);
        String description = (String) m_description.getValue(state);
        BigDecimal parentTypeID = (BigDecimal) m_parentTypeSelect.getValue(state);
        BigDecimal lifecycleID = (BigDecimal) m_lifecycleSelect.getValue(state);
        BigDecimal workflowID = (BigDecimal) m_workflowSelect.getValue(state);

        ObjectType parentObjectType = null;
        ContentType parentContentType = null;
        String parentContentClassname = null;
        try {
            if (parentTypeID != null) {
                if (parentTypeID.intValue() != -1) {
                    parentContentType = new ContentType(parentTypeID);
                }
            }
        } catch (DataObjectNotFoundException ex) {
            // Ignore -- use content page if type isn't found
        }

        if (parentContentType != null) {
            parentContentClassname = parentContentType.getClassName();
            parentObjectType =
                    SessionManager.getMetadataRoot().getObjectType(parentContentType.getAssociatedObjectType());
        } else {
            try {
                parentContentType = ContentType.findByAssociatedObjectType(DEFAULT_UDITEM_TYPE);
                parentContentClassname = parentContentType.getClassName();
            } catch (DataObjectNotFoundException ex) {
                // If parent content type isn't found, don't add
                //widgets and use "com.arsdigita.cms.ContentPage" as
                //classname
                parentContentClassname = "com.arsdigita.cms.ContentPage";
            }
            parentObjectType = SessionManager.getMetadataRoot().getObjectType(DEFAULT_UDITEM_TYPE);
        }


        String qname = parentObjectType.getModel().getName() + "." + name;
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        if (root.getObjectType(qname) != null || root.hasTable(name)) {
            throw new FormValidationException(m_name, (String) GlobalizationUtil.globalize("cms.ui.type.duplicate_type",
                    new Object[]{name}).localize());
        }

        //create a new dynamic object type with
        //name=label and supertype = UserDefinedContentItem
        dot = new DynamicObjectType(name, parentObjectType);
        dot.save();

        ContentType contentType;
        boolean isNew = false;

        //check if the object already exists for double click protection
        try {
            contentType = new ContentType(key);
        } catch (DataObjectNotFoundException ex) {
            contentType = new ContentType(SessionManager.getSession().create(new OID(ContentType.BASE_DATA_OBJECT_TYPE, key)));
            isNew = true;
        }

        contentType.setName(label);
        contentType.setDescription(description);
        contentType.setClassName(parentContentClassname);
        contentType.setAssociatedObjectType(dot.getObjectType().getQualifiedName());
        //the persistent form containing the persistent widgets used to
        //create and edit content items of this type
        String formName = name.concat("ItemForm");
        PersistentForm pForm = PersistentForm.create(formName);
        pForm.save();
        contentType.setItemFormID(pForm.getID());
        contentType.save();

        if (isNew) {
            updateContentTypeAssociation(section, contentType, parentContentType);
        }

        //associate a default lifecycle
        setDefaultLifecycle(lifecycleID, section, contentType);

        //associate a default workflow
        setDefaultWorkflow(workflowID, section, contentType);

        //drop the page to refresh content center, admin and item ui
        Utilities.refreshItemUI(state);

        // Select the new content type.
        if (m_types != null) {
            m_types.setSelectedKey(state, key.toString());
        }
    }

    /**
     * Form init listener
     * creates id for new type
     */
    public void init(FormSectionEvent e) {
        FormData data = e.getFormData();
        PageState state = e.getPageState();
        BigDecimal id;

        try {
            if (m_id.getValue(state) == null) {
                id = Sequences.getNextValue();
                m_id.setValue(state, id);
            }
        } catch (SQLException s) {
            s_log.error("Error generating sequence ID" + s);
            data.addError("Could not generate Sequence ID " + s.getMessage());
        }
    }

    /**
     * Form submission listener
     * return true if this form is cancelled, false otherwise
     */
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (m_cancel.isSelected(e.getPageState())) {
            throw new FormProcessException("cancelled");
        }
    }

    /**
     * registers the new type to this content section
     * and creates authoring kit for the content type.
     * With no parent type sent, the parent type authoring kit steps won't be added
     */
    protected void updateContentTypeAssociation(ContentSection section,
            ContentType type) {
        updateContentTypeAssociation(section, type, null);
    }

    /**
     * registers the new type to this content section
     * and creates authoring kit for the content type
     */
    protected void updateContentTypeAssociation(ContentSection section,
            ContentType type,
            ContentType parentType) {
        section.addContentType(type);
        section.save();

        //creates an authoring kit for this type and
        //set the main creation and edit component
        AuthoringKit kit = type.createAuthoringKit(CREATION_COMPONENT);
        kit.save();
        int stepOrdering = 1;
        boolean hasCategoryStep = false;
        if (parentType != null) {
            AuthoringKit superTypeKit = parentType.getAuthoringKit();
            AuthoringStepCollection superTypeSteps = superTypeKit.getSteps();
            while (superTypeSteps.next()) {
                AuthoringStep step = superTypeSteps.getAuthoringStep();
                if (CATEGORIZATION_COMPONENT.equals(step.getComponent())) {
                    hasCategoryStep = true;
                }
                kit.createStep(step.getLabel(),
                        step.getDescription(),
                        step.getComponent(),
                        new BigDecimal(stepOrdering));
                stepOrdering++;
            }
        }
        if (stepOrdering == 1) {
            kit.createStep(type.getName() + " Basic Properties",
                    type.getAssociatedObjectType(),
                    "com.arsdigita.cms.ui.authoring.PageEditDynamic",
                    new BigDecimal(stepOrdering));
        } else {
            kit.createStep(type.getName() + " Basic Properties",
                    type.getAssociatedObjectType(),
                    "com.arsdigita.cms.ui.authoring.SecondaryPageEditDynamic",
                    new BigDecimal(stepOrdering));
        }
        stepOrdering++;
        if (!hasCategoryStep) {
            kit.createStep("Categories",
                    "",
                    CATEGORIZATION_COMPONENT,
                    new BigDecimal(stepOrdering));
        }
        kit.save();

    }

    public Object getObjectKey(PageState s) {
        BigDecimal id = (BigDecimal) m_id.getValue(s);
        return id;
    }

    /**
     * Print listener: generates the SingleSelect options for parentType
     */
    private class ParentTypeSelectPrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();
            t.clearOptions();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            t.addOption(new Option("-1", "-- select --"));

            ContentTypeCollection contentTypes = section.getCreatableContentTypes(true);
            contentTypes.addOrder(ContentType.LABEL);
            while (contentTypes.next()) {
                ContentType type = contentTypes.getContentType();
                Label label = new Label(type.getName());
                if (type.isHidden()) {
                    label.setFontWeight(Label.ITALIC);
                }
                t.addOption(new Option(type.getID().toString(), label));
            }
        }
    }

    /**
     * Print listener: generates the SingleSelect options for default lifecycle
     */
    private class LifecycleSelectPrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();
            t.clearOptions();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            t.addOption(new Option("-1", "-- select --"));

            LifecycleDefinitionCollection cycles = section.getLifecycleDefinitions();
            while (cycles.next()) {
                LifecycleDefinition cycle = cycles.getLifecycleDefinition();
                t.addOption(new Option(cycle.getID().toString(), cycle.getLabel()));
            }
        }
    }

    /**
     * Print listener: generates the SingleSelect options for default workflow
     */
    private class WorkflowSelectPrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();
            t.clearOptions();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            t.addOption(new Option("-1", "-- select --"));

            TaskCollection templates = section.getWorkflowTemplates();
            while (templates.next()) {
                WorkflowTemplate template =
                        (WorkflowTemplate) templates.getDomainObject();
                t.addOption(new Option(template.getID().toString(),
                        template.getLabel()));
            }

        }
    }

    private void setDefaultLifecycle(BigDecimal lifecycleID,
            ContentSection section,
            ContentType contentType) {

        //associate a default lifecycle
        try {
            if (lifecycleID != null) {
                if (lifecycleID.intValue() != -1) {
                    LifecycleDefinition lifecycle = new LifecycleDefinition(lifecycleID);
                    ContentTypeLifecycleDefinition.updateLifecycleDefinition(section,
                            contentType, lifecycle);
                } else {
                    //remove the association
                    ContentTypeLifecycleDefinition.removeLifecycleDefinition(section, contentType);
                }
            }
        } catch (DataObjectNotFoundException ex) {
            //just ignore this since the lifecycle definition does not exist
            // no association
        }
    }

    private void setDefaultWorkflow(BigDecimal workflowID,
            ContentSection section,
            ContentType contentType) {

        //associate a default workflow
        try {
            if (workflowID != null) {
                if (workflowID.intValue() != -1) {
                    // Set default workflow definition association.
                    WorkflowTemplate template = new WorkflowTemplate(new OID(WorkflowTemplate.BASE_DATA_OBJECT_TYPE, workflowID));
                    ContentTypeWorkflowTemplate.updateWorkflowTemplate(section, contentType, template);
                } else {
                    // Remove the association.
                    ContentTypeWorkflowTemplate.removeWorkflowTemplate(section, contentType);
                }
            }
        } catch (DataObjectNotFoundException ex) {
            // Do nothing because the workflow definition does not exist.
        }

    }
}
