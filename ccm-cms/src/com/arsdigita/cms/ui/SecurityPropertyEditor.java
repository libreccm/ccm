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
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertyEditor;
import com.arsdigita.bebop.PropertyEditorModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.ComponentAccess;
import com.arsdigita.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Extends {@link com.arsdigita.bebop.PropertyEditor} to provide
 * access control features. Each link may be associated with a {@link
 * com.arsdigita.toolbox.ui.ComponentAccess} object; if the current
 * does not have sufficient privileges, the link will be hidden.
 * <p>
 * The simple use pattern for this component is as follows:
 *
 * <blockquote><pre><code>
 * SecurityPropertyEditor editor = new SecurityPropertyEditor();
 * editor.setDisplayComponent(new FooComponent());
 * NameEditForm n = new NameEditForm();
 * ComponentAccess ca1 = new ComponentAccess(n);
 * ca1.addAccessCheck(WORKFLOW_ADMIN);
 * ca1.addAccessCheck(CATEGORY_ADMIN);
 * editor.add("name", "Edit Name", ca, n.getCancelButton());
 * AddressEditForm a = new AddressEditForm();
 * ComponentAccess ca2 = new ComponentAccess(a);
 * editor.add("address", "Edit Address", ca2, a.getCancelButton());
 * </code></pre></blockquote>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version Id: SecurityPropertyEditor.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SecurityPropertyEditor extends PropertyEditor {

    private HashMap m_accessChecks;

    /**
     * Construct a new, empty <code>PropertyEditor</code>.  The {@link
     * #setDisplayComponent(Component)} method must be called before this
     * component is locked.
     */
    public SecurityPropertyEditor() {
        this(null);
    }

    /**
     * Construct a new, <code>PropertyEditor</code> with the given
     * display component
     *
     * @param display The display component
     */
    public SecurityPropertyEditor(Component display) {
        super(display);
        m_accessChecks = new HashMap();
        setModelBuilder(new AccessListModelBuilder());
    }

    /**
     * Add a component to the property editor. The component will be completely
     * invisible; it is up to the user to call {@link #showComponent(PageState,
     * String)} to display the component, and to call {@link
     * #showDisplayPane(PageState)} when the component needs to be hidden.
     *
     * @param key  The symbolic key for the component; must be unique
     *    for this <code>PropertyEditor</code>
     * @param ca   The {@link ComponentAccess} object which contains
     *    the child component, along with security restrictions
     */
    public void addComponent(String key, ComponentAccess ca) {
        super.addComponent(key, ca.getComponent());
        m_accessChecks.put(key, ca);
    }

    /**
     * Add a component to the list of links. It is up to the
     * component to correctly call showDisplayPane when it's done.
     *
     * @param key   The symbolic key for the component;  must be unique
     *    for this <code>PropertyEditor</code>
     * @param label The label for the link
     * @param ca    The component access
     */
    public void addComponent(String key, String label, ComponentAccess ca) {
        addComponent(key, ca);
        getLabelsMap().put(key, label);
    }

    /**
     * Specify a new {@link ComponentAccess} for a component which has already
     * been added to the <code>SecurityPropertyEditor</code>.
     *
     * @param key the key under which the component was added
     * @param access the <code>ComponentAccess</code> instance that will
     *   determine when the link for the specified component should be visible
     * @pre access.getComponent() == m_forms.get(key)
     */
    public void setComponentAccess(String key, ComponentAccess access) {
        Assert.isUnlocked(this);
        Component c = getComponent(key);
        Assert.exists(c, "the specified component");
        Assert.isTrue(access.getComponent().equals(c),
                          "The specified component does not match the component that" +
                          " id already in the PropertyEditor");
        m_accessChecks.put(key, access);
    }

    /**
     * Add a form to the set of forms which could be used to edit the
     * properties.
     *
     * @param key The symbolic key for the form;  must be unique
     *    for this <code>PropertyEditor</code>
     * @param label The label for the link
     * @param ca  The form ComponentAccess
     */
    public void add(String key, String label, ComponentAccess ca) {
        Component c = ca.getComponent();
        if (c instanceof Form) {
            Form form = (Form) c;
            m_accessChecks.put(key, ca);
            add(key, label, form);
            addSecurityListener(form);
        } else if (c instanceof FormSection) {
            FormSection section = (FormSection) ca.getComponent();
            m_accessChecks.put(key, ca);
            add(key, label, section);
            addSecurityListener(section);
        } else {
            throw new IllegalArgumentException(
                          "The ComponentAccess object did not contain a form section.");
        }
    }

    /**
     * Add a form to the set of forms which could be used to edit the
     * properties
     *
     * @param key   The symbolic key for the form; must be unique
     *    for this <code>PropertyEditor</code>
     * @param label The label for the link
     * @param ca  The form ComponentAccess
     * @param cancelButton The Cancel button on the form.
     */
    public void add(String key, String label,
                    ComponentAccess ca, Submit cancelButton) {
        add(key, label, ca);
        addCancelListener((FormSection) ca.getComponent(), cancelButton);
    }

    /**
     * Add a submission listener to the form that will hide all components
     * and show the display pane. This method should be used to add
     * submission listeners to forms which are buried deep inside some
     * component, and are not members of this <code>PropertyEditor</code>.
     *
     * @param form The form
     */
    public void addSecurityListener(FormSection form) {
        form.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent e) throws FormProcessException {
                    PageState state = e.getPageState();

                    // Cancel the form if the user does not pass the access checks.
                    SecurityManager sm = Utilities.getSecurityManager(state);
                    String key = (String) getList().getSelectedKey(state);
                    ComponentAccess ca = (ComponentAccess) m_accessChecks.get(key);

                    if (key == null || ca == null) {
                        // no components currently selected and therefore
                        // no access checks to run for visibility
                        // or
                        // there are no access restrictions on the form
                        return;
                    }

                    if ( !ca.canAccess(state, sm) ) {
                        showDisplayPane(state);
                        throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.insufficient_privileges").localize());
                    }
                }
            });
    }

    /**
     * Add all required listeners to the form to ensure that
     * if the form is submitted successfully or cancelled, the display
     * pane will be shown. This method should be used to add
     * listeners to forms which are buried deep inside some
     * component, and are not members of this <code>PropertyEditor</code>.
     *
     * @param form The form
     * @param cancelButton the "Cancel" button on the form
     */
    public void addListeners(FormSection form, Submit cancelButton) {
        addSecurityListener(form);
        super.addListeners(form, cancelButton);
    }

    /**
     * Return the map of keys to access checks
     */
    protected final Map getAccessMap() {
        return m_accessChecks;
    }

    /**
     * Returns an {@link SecurityPropertyEditor.AccessListModel} during each
     * request
     */
    protected static class AccessListModelBuilder extends DefaultModelBuilder {

        public AccessListModelBuilder() {
            super();
        }

        public PropertyEditorModel makeModel(PropertyEditor p, PageState s) {
            return new AccessListModel (
                                        getProperties(p),
                                        ((SecurityPropertyEditor)p).getAccessMap(),
                                        s
                                        );
        }
    }

    /**
     * Performs access checks for each property; skips the properties
     * that the user is not allowed to access
     */
    protected static class AccessListModel extends DefaultModel {

        private Map m_access;
        private PageState m_state;
        private SecurityManager m_manager;

        public AccessListModel(Iterator iter, Map access, PageState state) {
            super(iter);
            m_access = access;
            m_state = state;
            m_manager = Utilities.getSecurityManager(m_state);
        }

        public boolean next() {
            while(super.next()) {
                Object key = getKey();
                ComponentAccess ca = (ComponentAccess)m_access.get(key);

                if(ca == null) // No access restricitons
                    return true;

                if ( ca.canAccess(m_state, m_manager) ) // Access checks out
                    return true;

                // Otherwise, skip the property
            }

            return false;
        }
    }

}
