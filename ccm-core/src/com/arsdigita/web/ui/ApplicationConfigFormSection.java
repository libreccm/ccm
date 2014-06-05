/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web.ui;

import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;

import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;

import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * An implementation of ResourceConfigFormSection to be
 * used for creating / editing Application instances.
 * For simple apps, can be used as is. If an app has any
 * custom properties, this can be subclassed to add 
 * futher form fields.
 */
public class ApplicationConfigFormSection extends ResourceConfigFormSection {

    private RequestLocal m_parentResource;
    private RequestLocal m_currentResource;
    private ApplicationType m_applicationType;
    private TextField m_url;
    private TextField m_title;
    private TextArea m_desc;
    private boolean m_createApplicationGroup = false;

    public ApplicationConfigFormSection(ResourceType resType,
                                        RequestLocal parentAppRL,
                                        boolean createApplicationGroup) {
        this(resType, parentAppRL);
        m_createApplicationGroup = createApplicationGroup;
    }

    public ApplicationConfigFormSection(ResourceType resType,
                                        RequestLocal parentAppRL) {
        m_applicationType = (ApplicationType) resType;
        m_parentResource = parentAppRL;
        m_applicationType.disconnect();
        setup();
    }

    public ApplicationConfigFormSection(RequestLocal application) {
        m_currentResource = application;

        setup();
    }

    private void setup() {
        addInitListener(new FormInitListener() {
            @Override
            public void init(FormSectionEvent e)
                    throws FormProcessException {
                PageState state = e.getPageState();

                if (m_currentResource != null) {
                    Application application =
                                (Application) m_currentResource.get(state);
                    initWidgets(state, application);
                } else {
                    initWidgets(state, null);
                }
            }

        });
        addValidationListener(new FormValidationListener() {
            @Override
            public void validate(FormSectionEvent e)
                    throws FormProcessException {
                PageState state = e.getPageState();

                if (m_currentResource != null) {
                    Application application =
                                (Application) m_currentResource.get(state);
                    validateWidgets(state, application);
                } else {
                    validateWidgets(state, null);
                }
            }

        });

        addWidgets();
    }

    /**
     * Adds basic form widgets for URL, title
     * and description properties. Override this
     * method to add further widget.
     */
    protected void addWidgets() {
        m_url = new TextField(new StringParameter("url"));
        m_url.setSize(35);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.addValidationListener(new StringInRangeValidationListener(1, 100));

        m_title = new TextField(new StringParameter("title"));
        m_title.setSize(35);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new StringInRangeValidationListener(1, 200));

        m_desc = new TextArea(new StringParameter("desc"));
        m_desc.setRows(5);
        m_desc.setCols(35);
        m_desc.addValidationListener(new StringInRangeValidationListener(0, 4000));

        add(new Label("URL:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_url);
        add(new Label("Title:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_title);
        add(new Label("Description:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_desc);
    }

    /**
     * Initialize the form fields
     * @param state
     * @param application the application being edited, if any
     * @throws com.arsdigita.bebop.FormProcessException
     */
    protected void initWidgets(PageState state,
                               Application application)
            throws FormProcessException {

        if (application != null) {
            String path = application.getPath();
            String url = path.substring(path.lastIndexOf("/") + 1);

            m_url.setValue(state, url);
            m_title.setValue(state, application.getTitle());
            m_desc.setValue(state, application.getDescription());
        } else {
            m_url.setValue(state, null);
            m_title.setValue(state, m_applicationType.getTitle());
            m_desc.setValue(state, m_applicationType.getDescription());
        }
    }

    /**
     * Validates the form fields
     * @param application the application being edited
     */
    protected void validateWidgets(PageState state,
                                   Application application)
            throws FormProcessException {

        String url = (String) m_url.getValue(state);

        // Change this part
        if (url.indexOf("/") != -1) {
            throw new FormProcessException("The url cannot contain '/'");
        }
        // amended cg - prevent null pointer exception when 
        // saving edit of child application        
        Application parent;
        if (m_parentResource != null) {

            parent = (Application) m_parentResource.get(state);
        } else {
            parent = application.getParentApplication();
        }

        String path;
        if (parent != null) {
            path = parent.getPath() + "/" + url;
        } else {
            path = url;
        }
        if (Application.isInstalled(Application.BASE_DATA_OBJECT_TYPE,
                                    url)) {
            throw new FormProcessException(
                    "An application already exists with that name");
        }

    }

    public Resource createResource(PageState state) {
        Application parent = (Application) m_parentResource.get(state);
        Application application = Application.createApplication(
                m_applicationType,
                (String) m_url.getValue(state),
                (String) m_title.getValue(state),
                parent,
                m_createApplicationGroup);

        try {
            processWidgets(state, application);
        } catch (FormProcessException ex) {
            throw new UncheckedWrapperException("cannot create resource", ex);
        }

        return application;
    }

    public void modifyResource(PageState state) {
        Application application = (Application) m_currentResource.get(state);

        try {
            processWidgets(state, application);
        } catch (FormProcessException ex) {
            throw new UncheckedWrapperException("cannot create resource", ex);
        }
    }

    /**
     * Processes the form submission
     * @param application the application being edited, or newly created
     */
    protected void processWidgets(PageState state,
                                  Application application)
            throws FormProcessException {
        application.setTitle((String) m_title.getValue(state));
        application.setDescription((String) m_desc.getValue(state));
    }

}
