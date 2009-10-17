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
package com.arsdigita.portal;

import com.arsdigita.kernel.ResourceType;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.Assert;
import org.apache.log4j.Category;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 * <p>This class is a convenience class for easily initializing a Portlet.</p>
 * <p>
 * The usage pattern for this class is:
 * <ul>
 * <li> Create a PortletSetup class.</li>
 * <li> Use setters to initialize values.</li>
 * <li> Call the run method ( setup.run() ).</li>
 * </ul>
 * </p>
 * <p>
 * Necessary values that are uninitialized when run() is called throw an 
 * exception. </p>
 * 
 * @author Justin Ross
 * @version $Id: PortletSetup.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PortletSetup {

    // public static final String versionId =
    //     "$Id: PortletSetup.java 287 2005-02-22 00:29:02Z sskracic $" +
    //     "by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    protected String m_profile;
    protected String m_key = null;
    protected String m_typeName = null;
    protected Category m_category;
    protected String m_title = null;
    protected String m_description = null;
    protected ApplicationType m_provider;
    protected String m_stylesheet = null;
    protected DomainObjectInstantiator m_instantiator = null;    


    public PortletSetup(Category category) {
        m_category = category;

    }

    public void setPortletObjectType(String portletObjectType) {
        m_typeName = portletObjectType;
    }

    public void setProfile(String profile) {
        m_profile = profile;
    }

    public void setKey(String key) {
        m_key = key;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public void setStylesheet(String stylesheet) {
        m_stylesheet = stylesheet;
    }

    public void setInstantiator(DomainObjectInstantiator instantiator) {
        m_instantiator = instantiator;
    }


    public void setProviderApplicationType(ApplicationType provider) {
        m_provider = provider;
    }

    public void setProviderApplicationType(String applicationObjectType) {
        ApplicationType provider =
            ApplicationType.retrieveApplicationTypeForApplication
            (applicationObjectType);

        Assert.assertNotNull(provider, "provider");

        setProviderApplicationType(provider);
    }

    protected void notice(String message) {
        m_category.info("PortletType '" + m_title + "' - " + message);
    }

    public PortletType run() {
        notice("Validating setup...");

        List messages = validate();

        if (messages.size() > 0) {
            Iterator iter = messages.iterator();

            while (iter.hasNext()) {
                m_category.error((String)iter.next());
            }

            return null;
        }

        notice("Done validating.");

        PortletType portletType = process();

        if (portletType != null) {
            portletType.save();
        }
        return portletType;
    }



    protected List validate() {
        ArrayList messages = new ArrayList();

        if (m_title == null)
            messages.add("Title is not set.");
        if (m_typeName == null)
            messages.add("PortletObjectType is not set.");
        if (m_instantiator == null)
            messages.add("Instantiator is not set.");
        if (m_profile == null)
            messages.add("Profile is not set.");

        return messages;
    }

    protected PortletType process() {
        notice("Starting setup...");

        PortletType portletType = null;

        if (!ResourceType.isInstalled(m_typeName)) {
            notice("Not installed.  Installing now...");

            notice("Using the following properties to perform install.");
            notice("  PortletObjectType: " + m_typeName);
            notice("  Title: " + m_title);
            notice("  Description: " + m_description);
            notice("  Profile: " + m_profile);
            notice("  Instantiator: " + m_instantiator);
            notice("  ProviderApplicationType: " + m_provider);
            notice("  Key: " + m_key);
            notice("  StyleSheet: " + m_stylesheet);

            portletType = PortletType.createPortletType
                (m_title, m_profile, m_typeName);

            portletType.setDescription(m_description);

            if (m_provider != null) {
                portletType.setProviderApplicationType(m_provider);
            }
            
            notice("Done installing.");
        } else {
            portletType = PortletType.retrievePortletTypeForPortlet(m_typeName);
        }

        DomainObjectFactory.registerInstantiator(m_typeName, m_instantiator);

        notice("Done setting up.");

        return portletType;
    }
}
