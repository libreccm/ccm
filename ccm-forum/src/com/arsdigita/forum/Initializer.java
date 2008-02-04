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
package com.arsdigita.forum;

import com.arsdigita.db.DbHelper;
import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.domain.xml.TraversalHandler;

import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.DomainInitEvent;

import com.arsdigita.xml.XML;

import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Application;
import com.arsdigita.messaging.ThreadedMessage;

import com.arsdigita.forum.portlet.RecentPostingsPortlet;
import com.arsdigita.forum.ui.portlet.RecentPostingsPortletEditor;
import com.arsdigita.web.ui.ApplicationConfigFormSection;

import org.apache.log4j.Logger;

/**
 * The forum initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Initializer extends CompoundInitializer {
    public final static String versionId =
        "$Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-forum.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    public void init(DomainInitEvent e) {
        super.init(e);
        
        e.getFactory().registerInstantiator(
            Forum.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Forum(dataObject);
                }
            });
        e.getFactory().registerInstantiator(
            "com.arsdigita.forum.Inbox",
            new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Forum(dataObject);
                }
            });

        e.getFactory().registerInstantiator(
            RecentPostingsPortlet.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new RecentPostingsPortlet(dataObject);
                }
            });

        e.getFactory().registerInstantiator(
            ForumSubscription.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    if (((Boolean)dataObject.get("isModerationAlert"))
                        .booleanValue()) {
                        s_log.debug("This is a mod alert");
                        return new ModerationAlert(dataObject);
                    } else {
                        s_log.debug("This is a subscription");
                        if (dataObject.get("digest") == null) {
                            return new ForumSubscription(dataObject);
                        } else {
                            return new DailySubscription(dataObject);
                        }
                    }
                }
            });
        
        XML.parse(Forum.getConfig().getTraversalAdapters(),
                  new TraversalHandler());
        
        URLFinder messageFinder = new URLFinder() {
                public String find(OID oid, String context) 
                    throws NoValidURLException {

                    return find(oid);
                }
                public String find(OID oid) throws NoValidURLException {
                    DataObject dobj = SessionManager.getSession().retrieve(oid);
                    
                    if (dobj == null) {
                        throw new NoValidURLException("No such data object " + oid);
                    }

                    Application app = Application.retrieveApplication(dobj);

                    if (app == null) {
                        throw new NoValidURLException
                            ("Could not find application instance for " + dobj);
                    }

                    try {
                        ThreadedMessage message = new ThreadedMessage(oid);
                        
                        String url = app.getPath() +
                            "/thread.jsp?threadID=" +
                            message.getThread().getID().toString();
                        
                        return url;
                    } catch(DataObjectNotFoundException e) {
                        throw new NoValidURLException
                            ("Could not find application instance for " + dobj);
                    }
                }
            };
        URLService.registerFinder(
            ThreadedMessage.BASE_DATA_OBJECT_TYPE, messageFinder);

        new ResourceTypeConfig(RecentPostingsPortlet.BASE_DATA_OBJECT_TYPE) {
            public ResourceConfigFormSection getCreateFormSection
                (final ResourceType resType, final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                    new RecentPostingsPortletEditor(resType, parentAppRL);
                
                return config;
            }
            
            public ResourceConfigFormSection getModifyFormSection
                (final RequestLocal application) {
                final RecentPostingsPortletEditor config =
                    new RecentPostingsPortletEditor(application);
                
                return config;
            }
        };

        new ResourceTypeConfig(Forum.BASE_DATA_OBJECT_TYPE) {
            public ResourceConfigFormSection getCreateFormSection
                (final ResourceType resType, final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                    new ApplicationConfigFormSection(resType, parentAppRL);
                
                return config;
            }
            
            public ResourceConfigFormSection getModifyFormSection
                (final RequestLocal application) {
                final ResourceConfigFormSection config =
                    new ApplicationConfigFormSection(application);
                
                return config;
            }
        };

    }
}
