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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringIsLettersOrDigitsValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * This is the base form class for all ElementAddForms used in adding elements
 * to a user-defined content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #19 $ $Date: 2004/08/17 $
 */
public abstract class ElementAddForm extends CMSForm {

    private final static Logger s_log = Logger.getLogger(ElementAddForm.class);

    protected final ACSObjectSelectionModel m_types;

    //form components: id, label, and description are added to the form;
    //                 m_buttons are added to the form by child classes

    protected Hidden m_id;
    protected TextField m_label;
    protected TextField m_description;
    protected SimpleContainer m_buttons;
    protected Submit m_submit;
    protected Submit m_cancel;

    /**
     * Constructor. Creates a new form that lets user add an element
     * to a content type
     *
     * @param formName  The name of the form
     * @param titleLabel  The form title to be displayed at the very top
     * @param m  The <code>ACSObjectSelectionModel</code> indicating the current
     *    content type
     */
    public ElementAddForm(String formName, String titleLabel,
                          ACSObjectSelectionModel m) {
        super(formName);

        m_types = m;

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        Label heading = new Label(titleLabel);
        heading.setFontWeight(Label.BOLD);
        add(heading, ColumnPanel.FULL_WIDTH);

        add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH);
        add(new Label(GlobalizationUtil.globalize("cms.ui.name")));
        m_label = new TextField(new TrimmedStringParameter("label"));
        m_label.addValidationListener(new NotNullValidationListener());
        m_label.addValidationListener(new StringIsLettersOrDigitsValidationListener());
        m_label.setSize(40);
        m_label.setMaxLength(1000);
        add(m_label);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.title")));
        m_description = new TextField(new StringParameter("description"));
        m_description.addValidationListener(new StringLengthValidationListener(4000));
        m_description.setSize(40);
        add(m_description);

        //a container with the pair of submit buttons
        m_buttons = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Add Element");
        m_buttons.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        m_buttons.add(m_cancel);

        //add the listeners
        addProcessListener(new ProcessListener());
        addInitListener(new InitListener());
        addSubmissionListener(new TypeSecurityListener());
        addSubmissionListener(new SubmissionListener());
        addValidationListener(new ValidationListener());
    }

    protected ACSObjectSelectionModel getSelectionModel(){
        return m_types;
    }

    protected Hidden getHiddenID(){
        return m_id;
    }

    protected TextField getLabelField(){
        return m_label;
    }

    protected TextField getDescriptionField(){
        return m_description;
    }

    protected SimpleContainer getButtons(){
        return m_buttons;
    }

    protected Submit getSubmit(){
        return m_submit;
    }

    protected Submit getCancel(){
        return m_cancel;
    }

    /**
     * Adds an attribute to the passed in dynamic object type. Used by the
     * internal {@link FormProcessListener#process(FormSectionEvent)}.
     *
     * @see FormProcessListener#process(FormSectionEvent)
     **/
    protected abstract void addAttribute(DynamicObjectType dot,
                                         String label,
                                         PageState state)
        throws FormProcessException;

    /**
     * Adds a persistent form component (or components) to the passed in
     * persistent form. Used by the internal {@link
     * FormProcessListener#process(FormSectionEvent)}.
     *
     * @see FormProcessListener#process(FormSectionEvent)
     **/
    protected abstract void addFormComponent(PersistentForm pForm,
                                             String label,
                                             PageState state)
        throws FormProcessException;


    /**
     * if this form is cancelled
     *
     * @return  True if form is cancelled, false otherwise
     */
    public boolean isCancelled(PageState s) {
        return m_cancel.isSelected(s);
    }

    /**
     * Subclasses should override this method if they wish do perform
     * initialization actions in addition to those performed by
     * {@link ElementAddForm}.
     **/
    protected void doInit(FormSectionEvent event) {}

    /**
     * Subclasses should override this method if they wish do perform validation
     * actions in addition to those already performed by {@link ElementAddForm}.
     **/
    protected void doValidate(FormSectionEvent event)
        throws FormProcessException {}

    /**
     * Fetches the currently selected content type from the single selection
     * model.
     *
     * @param state  The page state
     * @return  The current content type
     */
    protected ContentType getContentType(PageState state) {

        ContentType t = (ContentType)m_types.getSelectedObject(state);
        Assert.exists(t, "content type");
        return t;
    }

    /**
     * Fetches the associated object type of a user defined content type
     *
     * @param type The udct
     * @return  The associated dynamic object type
     */
    protected DynamicObjectType getDOT(ContentType type) {

        DynamicObjectType dot = new DynamicObjectType
            (type.getAssociatedObjectType());
        return dot;

    }

    private DynamicObjectType getDOT(PageState state) {
        return getDOT(getContentType(state));
    }

    /**
     * drops the bebop page to refresh authoring ui
     *
     * @param state The page state
     */
    protected void refreshAuthoring(PageState state) {

        com.arsdigita.cms.dispatcher.Utilities.refreshItemUI(state);
    }

    /**
     * adds an label and description field for an attribute
     * to the persistent form used to author items of this udct
     *
     * @param state The page state
     * @param pForm The persistent form
     * @param label Label of the element
     * @param description Description of the element
     */
    protected void addLabelDescript(PersistentForm pForm,
                                    String label,
                                    String description) {

        PersistentLabel lLabel = PersistentLabel.create(label + ":");
        lLabel.save();
        pForm.addComponent(lLabel);

        PersistentLabel dLabel = null;
        if (description!=null) {
            dLabel = PersistentLabel.create(description);
        } else {
            dLabel = PersistentLabel.create("");
        }
        dLabel.save();
        pForm.addComponent(dLabel);
        pForm.save();

    }


    private final class ValidationListener implements FormValidationListener {

        public void validate(FormSectionEvent event)
            throws FormProcessException {

            PageState state = event.getPageState();
            String label = (String) m_label.getValue(state);

            DynamicObjectType dot = getDOT(state);

            if ( dot.hasProperty(label) ) {
                throw new FormProcessException
                    (GlobalizationUtil.globalize("cms.ui.property_already_exist" + label.toLowerCase()));

            }
            doValidate(event);
        }
    }

    private final class InitListener implements FormInitListener {

        /**
         * Form init listener Does form initializing common to all child
         * classes, if any
         *
         * @param e  The form section event
         * @see #doInit(FormSectionEvent)
         */
        public void init(FormSectionEvent event) {
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            try {
                if (m_id.getValue(state) == null) {
                    m_id.setValue(state, Sequences.getNextValue());
                }
            } catch (SQLException ex) {
                s_log.error("Could not generate Sequence ID", ex);
                data.addError("Could not generate Sequence ID " + ex.getMessage());
            }
            doInit(event);
        }
    }

    private final class SubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent e) throws FormProcessException {
            if (m_cancel.isSelected(e.getPageState())) {
                throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.cancelled"));
            }
        }
    }

    private final class ProcessListener implements FormProcessListener {

        /**
         * @see #addAttribute(DynamicObjectType, String, PageState)
         * @see #addFormComponent(PersistentForm, String, PageState)
         */
        public void process(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();

            String label = (String) m_label.getValue(state);
            String description = (String) m_description.getValue(state);
            ContentType type = getContentType(state);
            DynamicObjectType dot = getDOT(type);
            addAttribute(dot, label, state);
            dot.save();

            //get the form associated with this content type
            PersistentForm pForm = type.getItemForm();

            //add label and description to the form
            addLabelDescript(pForm, label, description);
            addFormComponent(pForm, label, state);
            pForm.save();
            refreshAuthoring(state);
        }
    }
}
