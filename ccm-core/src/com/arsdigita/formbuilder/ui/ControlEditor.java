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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.bebop.BaseLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.WidgetLabel;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.formbuilder.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * This class provides a basic UI component for editing the controls on a
 * persistent form.
 *
 * It is designed to be dropped into any page without requiring any significant
 * additional infrasructure
 */
public class ControlEditor extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(ControlEditor.class);

    private ParameterSingleSelectionModel m_control;

    private SingleSelectionModel m_form;

    private NewControl m_new_control;
    private NewSection m_new_section;
    private BoxPanel m_view_form;
    private ControlProperties m_control_props;
    private MoveControl m_move_control;

    /**
     * Constructor creates a new control editor widget, for editing the form
     * specified in the single selection model. The key returned by the single
     * selection model should be an instance of the {@link java.math.BigDecimal}
     * class.
     *
     * @param app  the application type
     * @param form the form to edit
     */
    public ControlEditor(String app,
                         SingleSelectionModel form) {
        this(app, form, false);
    }

    /**
     * Constructor creates a new control editor widget, for editing the form
     * specified in the single selection model. The key returned by the single
     * selection model should be an instance of the {@link java.math.BigDecimal}
     * class.
     *
     * This constructor also allows the programmer to turn on the use of form
     * sections, although they must also call the setFormSectionModelBuilder
     * method to populate the list box.
     *
     * @param app             the application type
     * @param form            the form to edit
     * @param wantFormSeciton whether to display list box for adding form
     *                        sections.
     */
    public ControlEditor(String app,
                         SingleSelectionModel form,
                         boolean wantFormSection) {
        m_form = form;

        m_new_control = new NewControl(app);
        m_new_section = new NewSection(form);

        // NOTE: as of version 6.6 (including earlier versions) the help
        // system doesn't work at all. Config should return null constant.
        String helpURL = FormBuilderUtil.getConfig().getControlsHelpLink();
        if (helpURL != null) {
            add(new Link(new Label(GlobalizationUtil.globalize(
                "formbuilder.ui.help")), helpURL));
            add(new Label("")); // spacer
        }

        m_control = new ParameterSingleSelectionModel(new BigDecimalParameter(
            "control"));

        m_view_form = new BoxPanel(BoxPanel.VERTICAL);

        addEditableComponent(m_view_form, m_new_control);

        if (wantFormSection) {
            addEditableComponent(m_view_form, m_new_section);
        }

        m_view_form.add(new FormItemControlsForm("view_form"));

        m_control_props = new ControlProperties(m_form,
                                                m_new_control.getSelection(),
                                                m_control,
                                                app);

        m_move_control = new MoveControl(m_form,
                                         m_control);

        m_new_section.addProcessListener(new NewSectionProcessListener());
        m_new_control.addProcessListener(new NewControlProcessListener());
        m_control_props.addCompletionListener(
            new ControlPropsCompletionListener());
        m_move_control.addActionListener(new MoveControlActionListener());

        add(m_view_form);
        addEditableComponent(this, m_control_props);
        addEditableComponent(this, m_move_control);
    }

    protected void addEditableComponent(Container container,
                                        Component child) {
        container.add(child);
    }

    protected PersistentComponent getFormSection(PageState state,
                                                 BigDecimal sectionID) {
        OID componentOID = new OID(PersistentComponent.BASE_DATA_OBJECT_TYPE,
                                   sectionID);
        return (PersistentComponent) DomainObjectFactory.newInstance(
            componentOID);
    }

    // XXX PrintListener will change to ListModel when (if)
    // optiongroups finally become model driven
    /**
     * Sets the form section model builder for populating the drop down list of
     * form sections. The model builder is actually a PrintListener, which
     * should add new Options to the select box
     *
     * @param l the print listener for populating the list
     */
    public void setFormSectionModelBuilder(PrintListener l) {
        m_new_section.setFormSectionModelBuilder(l);
    }

    public void respond(PageState state)
        throws javax.servlet.ServletException {
        super.respond(state);

        String name = state.getControlEventName();
        String value = state.getControlEventValue();

        if (name.equals("edit")) {
            m_control.setSelectedKey(state, new BigDecimal(value));
            m_view_form.setVisible(state, false);
            m_control_props.setVisible(state, true);
        } else if (name.equals("delete")) {
            m_control.setSelectedKey(state, new BigDecimal(value));

            OID formOID = new OID(PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                                  m_form.getSelectedKey(state));
            PersistentFormSection fs
                                  = (PersistentFormSection) DomainObjectFactory
                .newInstance(formOID);

            OID controlOID = new OID(PersistentComponent.BASE_DATA_OBJECT_TYPE,
                                     m_control.getSelectedKey(state));
            PersistentComponent c = (PersistentComponent) DomainObjectFactory
                .newInstance(controlOID);

            fs.removeComponent(c);

            try {
                PersistentWidget w = (PersistentWidget) c;
                WidgetLabel label = WidgetLabel.findByWidget(w);
                if (null != label) {
                    fs.removeComponent(label);
                    label.delete();
                }
            } catch (ClassCastException ex) {
                // Nada
            }

            if (!(c instanceof PersistentFormSection)) {
                c.delete();
            }
            m_control.setSelectedKey(state, null);
        } else if (name.equals("move")) {
            m_control.setSelectedKey(state, new BigDecimal(value));
            m_view_form.setVisible(state, false);
            m_move_control.setVisible(state, true);
        }

        state.clearControlEvent();

        try {
            throw new RedirectSignal(state.stateAsURL(), true);
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    protected SingleSelectionModel getFormModel() {
        return m_form;
    }

    public void register(Page page) {
        super.register(page);
        page.addComponentStateParam(this,
                                    m_control.getStateParameter());

        page.setVisibleDefault(m_view_form, true);
        page.setVisibleDefault(m_control_props, false);
        page.setVisibleDefault(m_move_control, false);
    }

    private class NewSectionProcessListener implements FormProcessListener {

        public void process(FormSectionEvent e)
            throws FormProcessException {

            s_log.debug("NewSectionProcessListener().process()");

            PageState state = e.getPageState();

            OID formOID = new OID(PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                                  m_form.getSelectedKey(state));
            PersistentFormSection form
                                  = (PersistentFormSection) DomainObjectFactory
                .newInstance(formOID);

            BigDecimal id = m_new_section.getSelectedSection(state);
            PersistentComponent section = getFormSection(state, id);
            form.addComponent(section);
        }

    }

    private class NewControlProcessListener implements FormProcessListener {

        public void process(FormSectionEvent e)
            throws FormProcessException {

            s_log.debug("NewControlProcessListener.process()");

            m_control.setSelectedKey(e.getPageState(), null);
            m_view_form.setVisible(e.getPageState(), false);
            m_control_props.setVisible(e.getPageState(), true);
        }

    }

    private class ControlPropsCompletionListener implements
        FormCompletionListener {

        public void complete(FormSectionEvent e)
            throws FormProcessException {

            s_log.debug("ControlPropsCompletionListener.complete()");

            m_control.setSelectedKey(e.getPageState(), null);
            m_view_form.setVisible(e.getPageState(), true);
            m_control_props.setVisible(e.getPageState(), false);
        }

    }

    private class MoveControlActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            s_log.debug("MoveControlActionListener.actionPerformed()");

            m_control.setSelectedKey(e.getPageState(), null);
            m_view_form.setVisible(e.getPageState(), true);
            m_move_control.setVisible(e.getPageState(), false);
        }

    }

    /**
     * Allows subclasses to control when to add the edit/move/delete links
     */
    protected boolean addItemEditObserver(PageState state) {
        return true;
    }

    private class FormItemControlsForm extends MetaForm {

        public FormItemControlsForm(String name) {
            super(name);
        }

        public Form buildForm(PageState state) {
            OID formOID = new OID(PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                                  m_form.getSelectedKey(state));
            PersistentFormSection section
                                  = (PersistentFormSection) DomainObjectFactory
                .newInstance(formOID);

            if (addItemEditObserver(state)) {
                section.setComponentAddObserver(new ItemEditAddObserver(
                    ControlEditor.this, state));
                section.setFormContainer(new ColumnPanel(3));
            } else {
                section.setFormContainer(new ColumnPanel(2));
            }

            Form f = null;
            if (section instanceof PersistentForm) {
                f = (Form) section.createComponent();
            } else {
                f = new Form("view_form", new ColumnPanel(1));
                f.add((FormSection) section.createComponent());
            }

            f.addInitListener(new PlaceholdersInitListener());

            // Make the controls readonly
            Traversal t = new Traversal() {

                public void act(Component c) {
                    try {
                        Widget widget = (Widget) c;
                        widget.setDisabled();
                        widget.setReadOnly();
                    } catch (ClassCastException ex) {
                        // Nada
                    }
                }

            };
            t.preorder(f);
            return f;
        }

        private class ItemEditAddObserver extends BaseEditAddObserver {

            Component m_handler;
            PageState m_state;

            public ItemEditAddObserver(Component handler,
                                       PageState state) {
                m_handler = handler;
                m_state = state;
            }

            /**
             *
             * @param dest
             * @param component
             *
             * @return
             *
             * @deprecated
             */
            protected BaseLink createLink(String dest,
                                          PersistentComponent component) {

                GlobalizedMessage label;
                if (dest == "delete") {
                    label = GlobalizationUtil.globalize("formbuilder.ui.delete");
                } else if (dest == "move") {
                    label = GlobalizationUtil.globalize("formbuilder.ui.move");
                } else {
                    label = GlobalizationUtil.globalize("formbuilder.ui.edit");
                }

                return new CallbackLink(m_handler,
                                        //   "[" + dest + "]",
                                        label,
                                        dest,
                                        component.getID().toString());
            }

            /**
             *
             */
            private class CallbackLink extends ControlLink {

                Component m_handler;
                String m_action;
                String m_component;

                public CallbackLink(Component handler,
                                    String label,
                                    String action,
                                    String component) {
                    super(label);

                    m_handler = handler;
                    m_action = action;
                    m_component = component;
                }

                public CallbackLink(Component handler,
                                    GlobalizedMessage label,
                                    String action,
                                    String component) {
                    super(new Label(label));

                    m_handler = handler;
                    m_action = action;
                    m_component = component;
                }

                public void setControlEvent(PageState state) {
                    state.setControlEvent(m_handler, m_action, m_component);
                }

            }

        }

    }

}
