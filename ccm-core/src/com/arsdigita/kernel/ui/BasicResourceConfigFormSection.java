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
package com.arsdigita.kernel.ui;

import com.arsdigita.web.Application;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ui.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

import java.math.BigDecimal;

import com.arsdigita.util.Assert;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;

public class BasicResourceConfigFormSection
        extends ResourceConfigFormSection implements Globalized {
    private BigDecimal m_resourceTypeID;
    private RequestLocal m_parentResourceRL;

    final private RequestLocal m_resourceRL;

    private TextField m_titleEntry;
    private TextArea m_descriptionEntry;

    /**
     * Constructs form section for application creation
     **/
    public BasicResourceConfigFormSection(ResourceType resType,
                                          RequestLocal parentResourceRL) {
        Assert.exists(resType, "resType may not be null");
        Assert.exists(parentResourceRL, "parentResourceRL may not be null");
        m_resourceRL = null;

        setup();

        m_parentResourceRL = parentResourceRL;
        m_resourceTypeID = resType.getID();
        m_titleEntry.setDefaultValue(resType.getTitle());
    }

    /**
     * Constructs form section for application modification
     **/
    public BasicResourceConfigFormSection(RequestLocal resource) {
        Assert.exists(resource);
        m_resourceRL = resource;

        setup();

        addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    Resource app = (Resource) m_resourceRL.get(ps);
                    initialize(ps, app);
                }
            });
    }

    private void setup() {
        // Title entry field
        add(new Label(new GlobalizedMessage("kernel.title", BUNDLE_NAME)));
        m_titleEntry = new TextField("title");
        m_titleEntry.addValidationListener(
            new NotEmptyValidationListener("Title is required")
        );
        m_titleEntry.setSize(20);
        add(m_titleEntry);

        // Description entry field
        add(new Label(new GlobalizedMessage("kernel.description", BUNDLE_NAME)));
        m_descriptionEntry = new TextArea("desc");
        m_descriptionEntry.setRows(5);
        m_descriptionEntry.setCols(40);
        add(m_descriptionEntry);
    }

    public String getTitle(PageState ps) {
        return (String)m_titleEntry.getValue(ps);
    }

    public String getDescription(PageState ps) {
        return (String)m_descriptionEntry.getValue(ps);
    }

    private void initialize(PageState ps, Resource app) {
        m_titleEntry.setValue(ps, app.getTitle());
        m_descriptionEntry.setValue(ps, app.getDescription());
    }

    public Resource createResource(PageState ps) {
        Assert.exists(m_resourceTypeID,
                             "BigDecimal m_resourceTypeID");
        Assert.exists(m_parentResourceRL,
                             "RequestLocal m_parentResourceRL");

        ResourceType at =
            ResourceType.retrieveResourceType(m_resourceTypeID);

        String title = getTitle(ps);

        String description = getDescription(ps);
        Resource parent = getParent(ps);

        String resourceObjectTypeName = at.getResourceObjectType();
        ObjectType resourceObjectType =
            SessionManager.getMetadataRoot().getObjectType
            (resourceObjectTypeName);
        Resource result;

        if (resourceObjectType.isSubtypeOf
            (Application.BASE_DATA_OBJECT_TYPE)) {
            String urlName = getUrlName(ps);

            result = Application.createApplication
                (resourceObjectTypeName, urlName, title, (Application) parent);
        } else {
            result = Resource.createResource(at, title, parent);
        }

        result.setDescription(description);

        return result;
    }

    public Resource getParent(PageState ps) {
        Resource parent = (Resource)m_parentResourceRL.get(ps);
        return parent;
    }

    public String getUrlName(PageState ps) {
        String title = getTitle(ps);

        char[] chars = title.toCharArray();
        int i;
        StringBuffer urlNameBuffer = new StringBuffer();
        for (i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                urlNameBuffer.append(c);
            } else if (c == ' ') {
                urlNameBuffer.append('-');
            }
        }

        String urlName = urlNameBuffer.toString();
        return urlName;
    }

    public void modifyResource(PageState ps) {
        Resource resource = (Resource) m_resourceRL.get(ps);
        resource.setTitle(getTitle(ps));
        resource.setDescription(getDescription(ps));
    }
}
