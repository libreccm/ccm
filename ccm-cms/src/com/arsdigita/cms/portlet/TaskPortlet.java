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
package com.arsdigita.cms.portlet;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.cms.ui.portlet.TaskPortletEditor;
import com.arsdigita.cms.ui.portlet.TaskPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;

import org.apache.log4j.Logger;

/**
 * A portlet that displays a list of user tasks. This portlet
 * displays the creation date and title of each listed task.
 *
 * Author: Jim Parsons
 */
public class TaskPortlet extends Portlet {

    /** Private logger instance for this class. */
    private static final Logger s_log = Logger.getLogger(TaskPortlet.class);


    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.cms.portlet.TaskPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public TaskPortlet(DataObject dataObject) {
        super(dataObject);
    }
    
    public int getMaxNumTasks() {
        return ((Integer)get("numTasks")).intValue();
    }

    public void setMaxNumTasks(int taskCount) {
        set("numTasks", new Integer(taskCount));
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new TaskPortletRenderer(this);
    }


    /**
     * Load the portlet type into database. Part of the non-recurring loader
     * step as part of the installation.
     *
     * Usage: in loader.run(final ScriptContext ctx):
     *        TaskPortlet.loadPortletType();
     */
    public static void loadPortletType()
    {
        PortletType type = PortletType
                .createPortletType("Task Portlet",
                                   PortletType.WIDE_PROFILE,
                                   TaskPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a Task List");
        s_log.info("Loading portlet type " + type);
    }

    /**
     * Reccurring initialization of the portlet each time ccm starts. 
     *
     * It registers an object instantiator with the portal infrastructure.
     * Usage: in initialize.init(domainInitEvent):
     *        TaskPortlet.initPortlet();
     */
    public static void initPortlet()
    {
        DomainObjectFactory.registerInstantiator(BASE_DATA_OBJECT_TYPE,
                                                 new ACSObjectInstantiator()
        {
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new TaskPortlet(dataObject);
            }
        });

        new ResourceTypeConfig(BASE_DATA_OBJECT_TYPE)
        {
            public ResourceConfigFormSection getCreateFormSection(
                    final ResourceType resType,
                    final RequestLocal parentAppRL)
            {
                return new TaskPortletEditor(resType, parentAppRL);
            }

            public ResourceConfigFormSection getModifyFormSection(
                    final RequestLocal application)
            {
                return new TaskPortletEditor(application);
            }
        };

    }





//  Alternative way to load and initialize a portlet using class PortletSetup.
//  It performs the loading step as well as the registerInstantiator step in
//  one class and can be used by the loader as well as the initializer. Was
//  especially useful in the old initializer system where the loading step and
//  the recurring initialization step were not separated.
//
    public void loadTaskPortlet() {
        
        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(TaskPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Task Portlet");
        setup.setDescription("Displays a Task List");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new TaskPortlet(dataObject);
            }
        });
        setup.run();

        new ResourceTypeConfig(TaskPortlet.BASE_DATA_OBJECT_TYPE) {
            public ResourceConfigFormSection getCreateFormSection
                (final ResourceType resType, final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                    new TaskPortletEditor(resType, parentAppRL);

                return config;
            }

            public ResourceConfigFormSection getModifyFormSection
                (final RequestLocal application) {
                final TaskPortletEditor config =
                    new TaskPortletEditor(application);

                return config;
            }
        };

    }

}
