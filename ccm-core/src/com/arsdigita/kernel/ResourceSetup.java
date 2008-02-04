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
package com.arsdigita.kernel;

import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectFactory;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * <p>Automates the creation and setup of ResourceTypes.</p>
 *
 * @author Jim Parsons &lt;<a href="mailto:jparsons@redhat.com">jparsons@redhat.com</a>&gt;
 */
public class ResourceSetup {

    protected Logger m_logger;
    protected String m_title = null;
    protected String m_description = null;
    protected String m_typeName = null;
    protected DomainObjectInstantiator m_instantiator = null;

    public ResourceSetup(Logger logger) {
        m_logger = logger;
    }



    public void setTitle(String title) {
        m_title = title;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public void setResourceObjectType(String typeName) {
        m_typeName = typeName;
    }

    public void setInstantiator(DomainObjectInstantiator instantiator) {
        m_instantiator = instantiator;
    }

    protected void notice(String message) {
        m_logger.info("ResourceType '" + m_title + "' - " + message);
    }

    public ResourceType run() {
        notice("Validating setup...");

        List messages = validate();

        if (messages.size() > 0) {
            Iterator iter = messages.iterator();

            while (iter.hasNext()) {
                m_logger.error((String)iter.next());
            }

            return null;
        }

        notice("Done validating.");

        ResourceType resourceType = process();

        if (resourceType != null) {
            resourceType.save();
        }

        return resourceType;
    }

    protected List validate() {
        ArrayList messages = new ArrayList();

        if (m_title == null)
            messages.add("Title is not set.");
        if (m_typeName == null)
            messages.add("ResourceObjectType is not set.");
        if (m_instantiator == null)
            messages.add("Instantiator is not set.");

        return messages;
    }

    protected ResourceType process() {
        notice("Starting Resource setup...");

        ResourceType resourceType = null;

        if (!ResourceType.isInstalled(m_typeName)) {

            notice("Not installed.  Installing now...");

            notice("Using the following properties to perform install.");
            notice("  ResourceObjectType: " + m_typeName);
            notice("  Title: " + m_title);
            notice("  Description: " + m_description);
            notice("  Instantiator: " + m_instantiator);

            resourceType = ResourceType.createResourceType(m_title,m_typeName);

            resourceType.setDescription(m_description);

            notice("Done installing.");
        }

        else {

        resourceType = ResourceType.retrieveResourceTypeForResource(m_typeName);

        }

        DomainObjectFactory.registerInstantiator(m_typeName, m_instantiator);

        notice("Done setting up Resource.");

        return resourceType;


    }

}
