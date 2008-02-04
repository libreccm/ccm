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
package com.arsdigita.cms.installer.portlet;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.cms.portlet.ContentItemPortlet;
import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.cms.ui.portlet.ContentItemPortletEditor;
import com.arsdigita.cms.ui.portlet.TaskPortletEditor;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;
import org.apache.log4j.Logger;


public class Initializer 
    extends BaseInitializer {

    public static final String TYPES = "types";

    private static Logger s_log = Logger.getLogger
        (Initializer.class.getName());


    private Configuration m_conf = new Configuration();

    public Initializer() throws InitializationException {
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    protected void doStartup() {
        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        
        txn.beginTxn();
        
        //loadContentDirectoryPortlet();
        loadContentItemPortlet();
        //loadContentSectionsPortlet();
        loadTaskPortlet();

        txn.commitTxn();
    }


    protected void doShutdown() {
        /* Empty */
    }
    
    
    private void loadContentDirectoryPortlet() {
        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Content Directory");
        setup.setDescription("Displays the content directory categories");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new ContentDirectoryPortlet(dataObject);
            }
        });
        setup.run();
    }
    
    private void loadContentItemPortlet() {
        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(ContentItemPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Content Item");
        setup.setDescription("Displays the body of a content item");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new ContentItemPortlet(dataObject);
            }
        });
        setup.run();

        new ResourceTypeConfig(ContentItemPortlet.BASE_DATA_OBJECT_TYPE) {
            public ResourceConfigFormSection getCreateFormSection
                (final ResourceType resType, final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                    new ContentItemPortletEditor(resType, parentAppRL);
                
                return config;
            }
            
            public ResourceConfigFormSection getModifyFormSection
                (final RequestLocal application) {
                final ContentItemPortletEditor config =
                    new ContentItemPortletEditor(application);
                
                return config;
            }
        };

    }
    

    private void loadContentSectionsPortlet() {
        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(ContentSectionsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Content Sections");
        setup.setDescription("Displays a list of content sections");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new ContentSectionsPortlet(dataObject);
            }
        });
        setup.run();
    }

    private void loadTaskPortlet() {
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
