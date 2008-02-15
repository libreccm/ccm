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

import org.apache.log4j.Logger;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.forum.portlet.MyForumsPortlet;
import com.arsdigita.forum.portlet.RecentPostingsPortlet;
import com.arsdigita.forum.search.FileAttachmentMetadataProvider;
import com.arsdigita.forum.search.PostMetadataProvider;
import com.arsdigita.forum.ui.portlet.RecentPostingsPortletEditor;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.web.ui.ApplicationConfigFormSection;
import com.arsdigita.xml.XML;

/**
 * The forum initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 1628 2007-09-17 08:10:40Z chrisg23 $
 */
public class Initializer extends CompoundInitializer {
    public final static String versionId =
		"$Id: Initializer.java 1628 2007-09-17 08:10:40Z chrisg23 $"
			+ "$Author: chrisg23 $"
			+ "$DateTime: 2004/08/17 23:26:27 $";

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
				Post.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new Post(dataObject);
					}
				});
		
		e.getFactory().registerInstantiator(
				PostFileAttachment.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					protected DomainObject doNewInstance(DataObject dataObject) {
						return new PostFileAttachment(dataObject);
					}
				});
				
		e.getFactory().registerInstantiator(
				PostImageAttachment.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					protected DomainObject doNewInstance(DataObject dataObject) {
						return new PostImageAttachment(dataObject);
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
				MyForumsPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					protected DomainObject doNewInstance(DataObject dataObject) {
						return new MyForumsPortlet(dataObject);
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
        

                    
        URLService.registerFinder(
			ThreadedMessage.BASE_DATA_OBJECT_TYPE,
			new PostFinder());
		URLService.registerFinder(PostFileAttachment.BASE_DATA_OBJECT_TYPE, new PostFileAttachmentURLFinder());

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

		// chris.gilbert@westsussex.gov.uk use new constructor that allows create form to be hidden from users other than those
		// with admin rights on parent app. Particularly appropriate for portlet where users 
		// customising their own homepage should NOT be allowed to create new forums
		new ResourceTypeConfig(Forum.BASE_DATA_OBJECT_TYPE, PrivilegeDescriptor.ADMIN, PrivilegeDescriptor.READ) {
            public ResourceConfigFormSection getCreateFormSection
				(final ResourceType resType,
				final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
					new ApplicationConfigFormSection(resType, parentAppRL, true);
                
                return config;
            }
            
			
            public ResourceConfigFormSection getModifyFormSection
                (final RequestLocal application) {
                final ResourceConfigFormSection config =
                    new ApplicationConfigFormSection(application);
                
                return config;
            }
        };


		MetadataProviderRegistry.registerAdapter(Post.BASE_DATA_OBJECT_TYPE, new PostMetadataProvider());
		MetadataProviderRegistry.registerAdapter(PostFileAttachment.BASE_DATA_OBJECT_TYPE, new FileAttachmentMetadataProvider());
		
	}

	public void init(LegacyInitEvent e) {
		super.init(e);
		
		if (RuntimeConfig.getConfig().runBackGroundTasks()) {
			RemoveUnattachedAssetsScheduler.startTimer();
    }
	

	}

}
