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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ui.portlet.ContentItemPortletEditor;
import com.arsdigita.cms.ui.portlet.ContentItemPortletRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;

import org.apache.log4j.Logger;


public class ContentItemPortlet extends Portlet {

    /** Private logger instance for this class. */
    private static final Logger s_log = Logger.getLogger(ContentItemPortlet.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.portlet.ContentItemPortlet";

    public static final String ITEM = "item";

    public ContentItemPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public ContentItem getContentItem() {
        return (ContentItem) DomainObjectFactory.newInstance
            ((DataObject) get(ITEM));
    }

    public void setContentItem(ContentItem item) {
        setAssociation(ITEM, item);
    }

    public AbstractPortletRenderer doGetPortletRenderer() {
        return new ContentItemPortletRenderer(this);
    }


    /**
     * Load the portlet type into database. Part of the non-recurring loader
     * step as part of the installation.
     *
     * Usage: in loader.run(final ScriptContext ctx):
     *        ContentItemPortlet.loadPortletType();
     */
    public static void loadPortletType()
    {
        PortletType type = PortletType
                .createPortletType("Content Item",
                                   PortletType.WIDE_PROFILE,
                                   ContentItemPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays the body of a content item");
        s_log.info("Loading portlet type " + type);
    }

    /**
     * Reccurring initialization of the portlet each time ccm starts. 
     *
     * It registers an object instantiator with the portal infrastructure.
     * Usage: in initialize.init(domainInitEvent):
     *        ContentItemPortlet.initPortlet();
     */
    public static void initPortlet()
    {
        // register instantiator
        DomainObjectFactory.registerInstantiator(BASE_DATA_OBJECT_TYPE,
                                                 new ACSObjectInstantiator()
        {
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new ContentItemPortlet(dataObject);
            }
        });

        new ResourceTypeConfig(BASE_DATA_OBJECT_TYPE)
        {
            public ResourceConfigFormSection getCreateFormSection(
                    final ResourceType resType,
                    final RequestLocal parentAppRL)
            {
                return new ContentItemPortletEditor(resType, parentAppRL);
            }

            public ResourceConfigFormSection getModifyFormSection(
                    final RequestLocal application)
            {
                return new ContentItemPortletEditor(application);
            }
        };


    }


//  Alternative way to load and initialize a portlet using class PortletSetup.
//  It performs the loading step as well as the registerInstantiator step in
//  one class and can be used by the loader as well as the initializer. Was
//  especially useful in the old initializer system where the loading step and
//  the recurring initialization step were not separated.
//
//  private void loadContentItemPortlet() {
//
//      PortletSetup setup = new PortletSetup(s_log);
//      setup.setPortletObjectType(ContentItemPortlet.BASE_DATA_OBJECT_TYPE);
//      setup.setTitle("Content Item");
//      setup.setDescription("Displays the body of a content item");
//      setup.setProfile(PortletType.WIDE_PROFILE);
//
//      setup.setInstantiator(new ACSObjectInstantiator() {
//          protected DomainObject doNewInstance(DataObject dataObject) {
//              return new ContentItemPortlet(dataObject);
//          }
//      });
//      setup.run();
//
//      new ResourceTypeConfig(ContentItemPortlet.BASE_DATA_OBJECT_TYPE) {
//          public ResourceConfigFormSection getCreateFormSection
//              (final ResourceType resType, final RequestLocal parentAppRL) {
//              final ResourceConfigFormSection config =
//                  new ContentItemPortletEditor(resType, parentAppRL);
//
//              return config;
//          }
//
//          public ResourceConfigFormSection getModifyFormSection
//              (final RequestLocal application) {
//              final ContentItemPortletEditor config =
//                  new ContentItemPortletEditor(application);
//
//              return config;
//          }
//      };
//
//  }



}
