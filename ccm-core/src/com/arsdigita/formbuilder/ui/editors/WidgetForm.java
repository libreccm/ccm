/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui.editors;

import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.parameters.PersistentParameterListener;
import com.arsdigita.formbuilder.ui.PropertiesForm;
import com.arsdigita.formbuilder.util.GlobalizationUtil;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public abstract class WidgetForm extends PropertiesForm {

    private static final Logger s_log = Logger.getLogger(WidgetForm.class);

    private SingleSelectionModel m_form;
    private SingleSelectionModel m_action;

    protected final static String NAME = "name";
    protected final static String DESCRIPTION = "description";

    protected Widget m_name;
    protected Widget m_description;
    protected OptionGroup m_required;

    private RequestLocal m_widget = new RequestLocal();

    private Label m_script = new Label(String.format(
        "<script language=\"javascript\" src=\"%s/javascript/manipulate-input.js\"></script>",
        Web.getWebappContextPath()), 
        false);

    public WidgetForm(String name,
                      SingleSelectionModel form,
                      SingleSelectionModel action) {
        super(name);

        m_form = form;
        m_action = action;

        addInitListener(new WidgetFormInitListener());
        addProcessListener(new WidgetFormProcessListener());
    }

    public SingleSelectionModel getSelection() {
        return m_form;
    }

    protected abstract PersistentWidget getWidget();

    protected abstract PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException;

    protected PersistentWidget getWidget(PageState state) {
        if (m_widget.get(state) == null) {
            BigDecimal action = (BigDecimal) m_action.getSelectedKey(state);

            PersistentWidget widget = null;
            if (action == null) {
                widget = getWidget();
                m_action.setSelectedKey(state, widget.setID());
            } else {
                try {
                    widget = getWidget(action);
                } catch (DataObjectNotFoundException ex) {
                    throw new com.arsdigita.util.UncheckedWrapperException(
                        "cannot find persistent widget " + action, ex);
                }
            }
            m_widget.set(state, widget);
        }
        return (PersistentWidget) m_widget.get(state);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        if (showName()) {
            addName(section);
        }
        if (showDescription()) {
            addDescription(section);
        }
        if (includeRequiredRadioGroup()) {
            addRequiredRadioGroup(section);
        }
    }

    /**
     * Add the name field to the form. Override this method to alter the creation of the name field.
     */
    protected void addName(FormSection section) {
        TextField name = new TextField(new StringParameter(NAME));
        name.addValidationListener(new URLTokenValidationListener());
        name.addValidationListener(new NotNullValidationListener());
        name.addValidationListener(new StringInRangeValidationListener(1, 40));
        name.setSize(30);
        section.add(new Label(GlobalizationUtil.globalize(
            "formbuilder.ui.editors.name")), ColumnPanel.RIGHT);
        section.add(name);

        m_name = name;
    }

    /**
     * Add the name field to the form. Override this method to alter the creation of the name field.
     */
    protected void addDescription(FormSection section) {
        TextArea description = new TextArea(new StringParameter(DESCRIPTION));
        description.setCols(50);
        description.setRows(5);
        description.addValidationListener(new StringInRangeValidationListener(0, 200));
        section.add(new Label(GlobalizationUtil.globalize(
            "formbuilder.ui.editors.description")),
                    ColumnPanel.RIGHT);
        section.add(description);

        m_description = description;
    }

    protected void addRequiredRadioGroup(FormSection section) {
        m_required = new RadioGroup(new BooleanParameter("required"));
        section.add(new Label(GlobalizationUtil.globalize(
            "formbuilder.ui.editors.answer_required")),
                    ColumnPanel.RIGHT);
        section.add(m_required);

        m_required.addOption(new Option(Boolean.TRUE.toString(), "Yes"));
        m_required.addOption(new Option(Boolean.FALSE.toString(), "No"));
        m_required.setOptionSelected(Boolean.FALSE.toString());
    }

    /**
     * If this is called then the value of the widget that is used for the passed in model is used
     * to auto-generate the value of the "name" value. For instance, if you want the label to
     * dictate the "name" then you would pass in the label ParameterModel and add the following to
     * the labelWidget:
     *
     * labelWidget.setOnFocus("if (this.form." + NAME + ".value == '') { " + " defaulting = true;
     * this.form." + NAME + ".value = urlize(this.value); }"); labelWidget.setOnKeyUp( "if
     * (defaulting) { this.form." + NAME + ".value = urlize(this.value) }" );
     *
     *
     * This can only be called after calling addWidgets()
     */
    protected void automaticallySetName(ParameterModel model) {
        Assert.exists(m_name);
        m_name.setOnFocus("defaulting = false");
        m_name.setOnBlur(
            "if (this.value == '') " + "{ defaulting = true; this.value = urlize(this.form." + model
            .getName() + ".value) }"
        );
    }

    /**
     * This determines whether or not the "required value" radio group is part of the form. This
     * returns true and should be overridden by fields where it does not make sense to ask. For
     * instance, when the widget is a hidden field then asking if it is required or not does not
     * make any logical sense so those widgets should return false.
     *
     * This will always return the same value for a given widget no matter what state the widget is
     * in.
     */
    protected boolean includeRequiredRadioGroup() {
        return true;
    }

    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }

    /**
     * Should the HTML name be on the form? Can be overridden by sub classes.
     */
    protected boolean showName() {

        return true;
    }

    /**
     * Should the description be on the form? Can be overridden by sub classes.
     */
    protected boolean showDescription() {

        return true;
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget widget)
        throws FormProcessException {

        PageState state = e.getPageState();

        if (widget == null) {
            if (showName()) {
                m_name.setValue(state, "");
            }

            if (showDescription()) {
                m_description.setValue(state, "");
            }
        } else {
            if (showName()) {
                m_name.setValue(state, widget.getParameterName());
            }

            if (showDescription()) {
                m_description.setValue(state, widget.getDescription());
            }

            /*
             Leaving this around just in case it turns out it does something useful

             //Get the associated listeners and set the value for the radio button accordingly
             if(includeRequiredRadioGroup()) {
             Collection col = widget.getValidationListeners();
             Iterator iterator;
             if(!col.isEmpty()) {
             iterator = col.iterator();
             while(iterator.hasNext()) {
             PersistentParameterListener listener =
             (PersistentParameterListener)iterator.next();
             s_log.debug(listener.getClassName());

             if(((String)listener.getClassName()).equals("com.arsdigita.bebop.parameters.NotEmptyValidationListener")) {
             m_required.setValue(state,Boolean.TRUE.toString());
             }
             }
             }
             }
             */
            //m_name.setVisible(state, false);
            if (includeRequiredRadioGroup()) {
                m_required.setValue(state, new Boolean(widget.isRequired()));
            }
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget widget)
        throws FormProcessException {

        FormData data = e.getFormData();
        PageState pageState = e.getPageState();

        String name = getName(pageState, data);
        widget.setParameterName(name);

        if (showDescription()) {
            String description = (String) data.get(DESCRIPTION);
            widget.setDescription(description);
        }

        // Clear current validation listeners
        if (!widget.isNew()) {
            widget.clearValidationListeners();
        }

        if (includeRequiredRadioGroup()) {
            boolean required = ((Boolean) m_required.getValue(pageState)).booleanValue();

            widget.setRequired(required);
            if (required) {
                // Answer is required
                String listenerClassName
                       = "com.arsdigita.bebop.parameters.NotEmptyValidationListener";
                PersistentParameterListener listener = new PersistentParameterListener(
                    listenerClassName);
                widget.addValidationListener(listener);
            } else {
                // Answer is not required
                // We need not add any listener
            }
        }
    }

    protected String getName(PageState pageState, FormData formData) {
        return (String) formData.get(NAME);
    }

    protected void addToForm(FormSectionEvent e,
                             PersistentWidget widget)
        throws FormProcessException {

        BigDecimal form_id = (BigDecimal) m_form.getSelectedKey(e.getPageState());

        PersistentFormSection form = null;
        try {
            form = new PersistentFormSection(form_id);
        } catch (DataObjectNotFoundException ex) {
            throw new FormProcessException("cannot find form", ex);
        }

        form.addComponent(widget);
        form.save();
    }

    private class WidgetFormInitListener implements FormInitListener {

        public void init(FormSectionEvent e)
            throws FormProcessException {

            PageState state = e.getPageState();

            BigDecimal action = (BigDecimal) m_action.getSelectedKey(state);

            if (action == null) {
                initWidgets(e, null);
            } else {
                PersistentWidget widget = null;
                try {
                    widget = getWidget(action);
                } catch (DataObjectNotFoundException ex) {
                    throw new FormProcessException("cannot find persistent widget "
                                                       + action, ex);
                }
                initWidgets(e, widget);
            }
        }

    }

    private class WidgetFormProcessListener implements FormProcessListener {

        public void process(FormSectionEvent e)
            throws FormProcessException {

            PageState state = e.getPageState();

            if (isCancelled(state)) {
                return;
            }

            BigDecimal action = (BigDecimal) m_action.getSelectedKey(state);

            PersistentWidget widget = getWidget(state);

            processWidgets(e, widget);
            widget.save();

            if (action == null) {
                addToForm(e, widget);
            }
        }

    }

}
