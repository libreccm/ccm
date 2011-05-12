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
package com.arsdigita.web;

import com.arsdigita.kernel.PackageType;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.Assert;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.log4j.Category;

/**
 * Automates the creation and setup of <code>ApplicationType</code>s.
 *
 * <pre><blockquote>
 * ApplicationSetup setup = new ApplicationSetup(s_log);
 * setup.setApplicationObjectType(SomeApp.BASE_DATA_OBJECT_TYPE);
 * setup.setKey("some-app");
 * setup.setTitle("Some App");
 * setup.setInstantiator(new ACSObjectInstantiator() {
 *         protected DomainObject doNewInstance(DataObject dataObject) {
 *             return new SomeApp(dataObject);
 *         }
 *     });
 * setup.run();
 * </blockquote></pre>
 *
 * <strong>Important note.</strong> When <code>Application</code>s are used
 * in the context of old-style apps that use <code>SiteNode</code>s and
 * <code>PackageType</code>s, it is important to use the
 * <code>setKey("some-string-key")</code> so that your new applications can
 * interoperate fluidly with the legacy applications.
 * Use of the <code>setKey</code> method ensures that your application will,
 * behind the scenes, use site nodes and package instances, in addition to the
 * <code>Application</code> object, to represent each new mountable application.
 * As a result, legacy code for managing and dispatching applications will work
 * with both your old and your new applications.
 *
 * @see com.arsdigita.web.ApplicationType
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: ApplicationSetup.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ApplicationSetup {

    /** Appender to enable writing to the löog file.                          */
    protected Category m_category;
    protected String m_title = null;
    protected String m_description = null;
    protected String m_typeName = null;
    protected DomainObjectInstantiator m_instantiator = null;

    // Legacy fields.

    protected String m_key = null;
    protected PackageType m_packageType = null;
    protected boolean m_isWorkspaceApplication = true;
    protected boolean m_isSingleton = false;
    protected String m_dispatcherClass = null;

    /**
     * Constructor.
     * 
     * @param category as appender to the log file.
     */
    public ApplicationSetup(Category category) {
        m_category = category;
    }

    /**
     * Sets a key for use in creating a package type and, later, site
     * node objects, as used by older applications, to match the new
     * application type.
     *
     * @param key the <code>String</code> key to use to generate a
     * package type corresponding to the new application type
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Sets the package type to use to represent your application
     * under the older package instance and site node approach.
     *
     * @param packageType a <code>PackageType</code> to use behind the
     * new application type
     */
    public void setPackageType(PackageType packageType) {
        m_packageType = packageType;
    }

    /**
     * Sets the title of the installed application type.
     */
    public void setTitle(String title) {
        m_title = title;
    }

    /**
     * Sets the description of the installed application type.
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     * Sets the object type of the application instances this
     * application type represents.
     */
    public void setApplicationObjectType(String typeName) {
        m_typeName = typeName;
    }

    /**
     * Sets the instantiator used by {@link
     * com.arsdigita.domain.DomainObjectFactory} to resurrect specific
     * domain classes from data objects.
     */
    public void setInstantiator(DomainObjectInstantiator instantiator) {
        m_instantiator = instantiator;
    }

    /**
     * This method is an alternative to {@link
     * #setPortalApplication(boolean)}.  It does the same thing.
     */
    public void setWorkspaceApplication(boolean isWorkspaceApplication) {
        m_isWorkspaceApplication = isWorkspaceApplication;
    }

    /**
     * Marks this applications of this type as ones that do or do not belong to
     * a "portal", a special application type that collects child applications
     * together for presentation and navigation purposes.
     *
     * By default, this value is true.
     */
    public void setPortalApplication(boolean isWorkspaceApplication) {
        m_isWorkspaceApplication = isWorkspaceApplication;
    }

    /**
     * Sets whether there can be more than one application of this
     * type in the system.
     *
     * The default is false.
     */
    public void setSingleton(boolean isSingleton) {
        m_isSingleton = isSingleton;
    }

    /**
     * Sets the dispatcher to use for appliactions of this type.
     */
    public void setDispatcherClass(String dispatcherClass) {
        m_dispatcherClass = dispatcherClass;
    }

    protected void notice(String message) {
        m_category.info("ApplicationType '" + m_title + "' - " + message);
    }

    /**
     * After all the properties are set, validates and installs the
     * specified application type.
     */
    public ApplicationType run() {
        notice("Validating setup...");

        List messages = validate();

        if (messages.size() > 0) {
            Iterator iter = messages.iterator();
            String errors = "Validation of ApplicationSetup data failed:\n";

            while (iter.hasNext()) {
                String msg = (String)iter.next();
                m_category.error(msg);
                errors += msg + "\n";
            }

            throw new RuntimeException(errors);
        }

        notice("Done validating.");

        ApplicationType applicationType = process();
        Assert.exists(applicationType, "applicationType is not null");
        applicationType.save();

        return applicationType;
    }

    protected List validate() {
        final ArrayList messages = new ArrayList();

        if (m_title == null) {
            messages.add("Title is not set.");
        }

        if (m_typeName == null) {
            messages.add("ApplicationObjectType is not set.");
        }

        if (m_instantiator == null) {
            messages.add("Instantiator is not set.");
        }

        return messages;
    }

    protected ApplicationType process() {
        notice("Starting setup...");

        ApplicationType applicationType = null;

        if (ApplicationType.isInstalled(m_typeName)) {
            // When migrating new code, sometimes an ApplicationType
            // exists but its corresponding PackageType does not.
            // This happens, for instance, when someone changes the
            // package key but not the object type in their app setup
            // script.  To treat this case, we need to create the
            // PackageType if it isn't there, even if the
            // ApplicationType exists.

            if (m_key != null && !packageTypeIsInstalled(m_key)) {
                Assert.isTrue(m_packageType == null);

                m_category.warn
                    ("ApplicationType " + m_typeName + " did not have " +
                     "its corresponding PackageType " + m_key + ". Adding " +
                     "one now.");

                PackageType packageType = new PackageType();

                packageType.setKey(m_key);
                packageType.setDisplayName(m_title);
                packageType.setURI("http://arsdigita.com/" + m_key);

                applicationType =
                    ApplicationType.retrieveApplicationTypeForApplication
                    (m_typeName);

                applicationType.setPackageType(packageType);
                applicationType.setDispatcherClass(m_dispatcherClass);

                packageType.save();
                applicationType.save();
            } else {
                applicationType =
                    ApplicationType.retrieveApplicationTypeForApplication
                    (m_typeName);
            }
        } else {
            // And, likewise, sometimes a package type is present, but
            // the application type does not yet exist.
            // ApplicationType's constructor knows how to handle this.

            notice("Not installed.  Installing now...");

            notice("Using the following properties to perform install.");
            notice("  ApplicationObjectType: " + m_typeName);
            notice("  Title: " + m_title);
            notice("  Description: " + m_description);
            notice("  Instantiator: " + m_instantiator);
            notice("  IsWorkspaceApplication: " + m_isWorkspaceApplication);
            notice("  IsSingleton: " + m_isSingleton);
            notice("  Key: " + m_key);
            notice("  PackageType: " + m_packageType);
            notice("  DispatcherClass: " + m_dispatcherClass);

            if (m_key == null && m_packageType == null) {
                // This is a new style / legacy free application
                applicationType = new ApplicationType(m_title, m_typeName);
            } else {
                // This is a legacy application type.

                if (m_key == null) {
                    applicationType = ApplicationType.createApplicationType
                        (m_packageType, m_title, m_typeName);
                } else if (m_packageType == null) {
                    applicationType = ApplicationType.createApplicationType
                        (m_key, m_title, m_typeName);

                    if (m_dispatcherClass != null) {
                        applicationType.setDispatcherClass(m_dispatcherClass);
                    }
                }

                applicationType.setWorkspaceApplication
                    (m_isWorkspaceApplication);
                applicationType.setSingleton(m_isSingleton);
            }

            applicationType.setDescription(m_description);

            notice("Done installing.");
        }

        DomainObjectFactory.registerInstantiator(m_typeName, m_instantiator);

        notice("Done setting up.");

        return applicationType;
    }

    private boolean packageTypeIsInstalled(String key) {
        try {
            PackageType.findByKey(key);
            return true;
        } catch (DataObjectNotFoundException nfe) {
            return false;
        }
    }
}
