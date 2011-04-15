/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portal.apportlet;

import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;
import com.arsdigita.domain.DomainObjectFactory;
import org.apache.log4j.Category;
import java.util.List;
import java.util.Iterator;

/**
 * <p>This class is a convenience class for easily initializing an
 * Application Portlet type (ie full screen portlet) wrapping
 * {@link PortletType} class.</p>
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
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */
public class AppPortletSetup extends PortletSetup {

    protected boolean m_isPortalApplication = true;
    protected boolean m_isSingleton = false;


    /**
     * Main Constructor, param category is an appender class and used to be able
     * to appand messages into the log file.
     * @param category
     */
    public AppPortletSetup(Category category) {
        super(category);

    }

    /**
     * Determine whether it is a PortalApplication.
     * Parameter is deprecated and not used for new, legacy free application
     * types! Should be ommitted in this case.
     * @param isPortalApplication
     */
    public void setPortalApplication(boolean isPortalApplication) {
        m_isPortalApplication = isPortalApplication;
    }

    /**
     * Determine whether it is a singelton application, ie only one instantiation
     * is apllowed.
     * Parameter is deprecated and not used for new, legacy free application
     * types! Should be ommitted in this case.
     * @param isPortalApplication
     */
    public void setSingleton(boolean isSingleton) {
        m_isSingleton = isSingleton;
    }

    @Override
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

    protected PortletType process() {
        notice("Starting setup...");

        AppPortletType portletType = null;

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
            notice("  IsPortalApplication: " + m_isPortalApplication);

            portletType = AppPortletType.createAppPortletType
                (m_title, m_profile, m_typeName);

            portletType.setDescription(m_description);

            if (m_provider != null) {
                portletType.setProviderApplicationType(m_provider);
            }

            portletType.setPortalApplication(m_isPortalApplication);

            notice("Done installing.");
        } else {
            portletType = (AppPortletType)
                AppPortletType.retrieveAppPortletTypeForAppPortlet(m_typeName);
        }

        DomainObjectFactory.registerInstantiator(m_typeName, m_instantiator);

        notice("Done setting up.");

        return portletType;
    }
}
