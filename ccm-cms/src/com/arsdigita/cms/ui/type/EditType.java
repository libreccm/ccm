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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.TooManyListenersException;


/**
 * This class contains a form component to edit a content type
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #14 $ $Date: 2004/08/17 $
 */
public class EditType extends CMSForm
    implements FormInitListener, FormProcessListener {

    public static final String versionId = "$Id: EditType.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";
    private static final Logger s_log = Logger.getLogger(EditType.class);

    private final SingleSelectionModel m_types;

    // Form widgets
    private Hidden m_id;
    private TextField m_label;
    private TextArea m_description;
    private SingleSelect m_lcSelect;
    private SingleSelect m_wfSelect;
    private Submit m_submit;
    private Submit m_cancel;

    /**
     * @param m The content type selection model. This tells the form which
     *   content type is selected.
     */
    public EditType(SingleSelectionModel m) {
        super("EditContentType");

        m_types = m;

        m_id = new Hidden(new BigDecimalParameter("id"));
        m_id.addValidationListener(new NotNullValidationListener());
        add(m_id);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.label")));
        m_label = new TextField(new StringParameter("label"));
        m_label.addValidationListener(new NotNullValidationListener());
        m_label.setSize(40);
        m_label.setMaxLength(1000);
        add(m_label);

        add(new Label(GlobalizationUtil.globalize("cms.ui.description")));
        m_description = new TextArea(new StringParameter("description"));
        m_description.addValidationListener(
                                            new StringLengthValidationListener(4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.lifecycle")));
        m_lcSelect = new SingleSelect(new BigDecimalParameter("lifecycle"));
        try {
            m_lcSelect.addPrintListener(new SelectLifecyclePrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e.getMessage(),e);
        }
        add(m_lcSelect);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.workflow")));
        m_wfSelect = new SingleSelect(new BigDecimalParameter("workflow"));
        try {
            m_wfSelect.addPrintListener(new SelectWorkflowPrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e.getMessage(),e);
        }
        add(m_wfSelect);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Save");
        s.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        addInitListener(this);
        addSubmissionListener(new TypeSecurityListener());
        addProcessListener(this);
    }

    /**
     * Returns true if the "cancel" button was submitted.
     *
     * @param state The page state
     * @return True if the form was cancelled, false otherwise
     */
    public boolean isCancelled(PageState state) {
        return m_cancel.isSelected(state);
    }

    /**
     * Returns the "cancel" button on the form
     * @return the cancel button on the form
     */
    public Submit getCancelButton() {
        return m_cancel;
    }

    /**
     * Populates the form with the content type properties.
     */
    public void init(FormSectionEvent event) {
        FormData data = event.getFormData();
        PageState state = event.getPageState();

        ContentSection section = CMS.getContext().getContentSection();

        ContentType type = getContentType(state);
        BigDecimal id = type.getID();
        String label =  type.getLabel();
        String description =  type.getDescription();

        data.put(m_id.getName(), id);
        data.put(m_label.getName(), label);
        data.put(m_description.getName(), description);

        LifecycleDefinition cycle =
            ContentTypeLifecycleDefinition.getLifecycleDefinition(section, type);
        if ( cycle != null ) {
            data.put(m_lcSelect.getName(), cycle.getID());
        }

        WorkflowTemplate template =
            ContentTypeWorkflowTemplate.getWorkflowTemplate(section, type);
        if ( template != null ) {
            data.put(m_wfSelect.getName(), template.getID());
        }
    }


    /**
     * Fetches the currently selected content type from the single selection
     * model.
     */
    private ContentType getContentType(PageState state) {
        String key = m_types.getSelectedKey(state).toString();
        try {
            BigDecimal typeID = new BigDecimal(key);
            return new ContentType(typeID);

        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("Content Type ID#" + key +
                                       " not found", ex);
        }
    }


    /**
     * Edits the content type.
     */
    public void process(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();

        // Get the current content section.
        ContentSection section = CMS.getContext().getContentSection();

        // Read form variables.
        BigDecimal key = (BigDecimal) data.get(m_id.getName());
        String label = (String) data.get(m_label.getName());
        String description = (String) data.get(m_description.getName());
        BigDecimal lifecycleId = (BigDecimal) data.get(m_lcSelect.getName());
        BigDecimal workflowId = (BigDecimal) data.get(m_wfSelect.getName());

        ContentType type = null;
        try {
            type = new ContentType(key);
        } catch (DataObjectNotFoundException e) {
            s_log.error("Can't find ContentType with key " + key, e);

            throw new FormProcessException(
                                           "Failed to edit the content type: " + key + " " + e.getMessage(), e);
        }

        type.setLabel(label);
        type.setDescription(description);
        type.save();

        // Handle default lifecycle.
        setDefaultLifecycle(lifecycleId, section, type);
        setDefaultWorkflow(workflowId, section, type);

        //Utilities.refreshItemUI(state);
    }


    /**
     * Sets the default lifecycle definition for a content section/type.
     */
    private void setDefaultLifecycle(BigDecimal lcId, ContentSection section,
                                     ContentType type) {
        try {
            if ( lcId != null ) {
                LifecycleDefinition lifecycle = new LifecycleDefinition(lcId);
                ContentTypeLifecycleDefinition.
                    updateLifecycleDefinition(section, type, lifecycle);
            } else {
                // Remove the association.
                ContentTypeLifecycleDefinition.
                    removeLifecycleDefinition(section, type);
            }
        } catch (DataObjectNotFoundException e) {
            // Do nothing because the lifecycle definition does not exist.
        }
    }

    /**
     * Sets the default workflow template for a content section/type.
     */
    private void setDefaultWorkflow(BigDecimal wfId, ContentSection section,
                                    ContentType type) {
        try {
            if ( wfId != null ) {
                // Set default workflow definition association.
                WorkflowTemplate template = new WorkflowTemplate(new OID(WorkflowTemplate.BASE_DATA_OBJECT_TYPE, wfId));
                ContentTypeWorkflowTemplate.
                    updateWorkflowTemplate(section, type, template);
            } else {
                // Remove the association.
                ContentTypeWorkflowTemplate.removeWorkflowTemplate(section, type);
            }
        } catch (DataObjectNotFoundException e) {
            // Do nothing because the workflow definition does not exist.
        }
    }



    /**
     * Print listener to generate the select widget for the list of
     * lifecyle definitions.
     */
    private class SelectLifecyclePrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            t.addOption(new Option("","-- select --"));

            LifecycleDefinitionCollection cycles = section.getLifecycleDefinitions();
            while ( cycles.next() ) {
                LifecycleDefinition cycle = cycles.getLifecycleDefinition();
                t.addOption(new Option(cycle.getID().toString(), cycle.getLabel()));
            }
        }
    }


    /**
     * Print listener to generate the select widget for the list of
     * workflow templates.
     */
    private class SelectWorkflowPrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            t.addOption(new Option("","-- select --"));

            TaskCollection templates = section.getWorkflowTemplates();
            while ( templates.next() ) {
                WorkflowTemplate template =
                    (WorkflowTemplate) templates.getDomainObject();
                t.addOption(new Option(template.getID().toString(),
                                       template.getLabel()));
            }

        }
    }

}
