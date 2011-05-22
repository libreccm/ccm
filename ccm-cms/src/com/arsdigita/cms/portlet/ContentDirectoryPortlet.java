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

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.cms.ui.portlet.ContentDirectoryPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;

import org.apache.log4j.Logger;

//
//  CURRENTLY NOT USED.
//  XXX: Portlet of same name is part of ccm-ldn-portlet.
//

/**
 *
 *
 */
public class ContentDirectoryPortlet extends Portlet {

    /** Private logger instance for this class. */
    private static final Logger s_log = Logger.getLogger(ContentDirectoryPortlet.class);

    /** Data object type for this class (i.e. full qualified class name */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.portlet.ContentDirectoryPortlet";

    public ContentDirectoryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ContentDirectoryPortletRenderer(this);
    }

    /**
     * Load the portlet type into database. Part of the non-recurring loader
     * step as part of the installation.
     *
     * Usage: in loader.run(final ScriptContext ctx):
     *        ContentDirectoryPortlet.loadPortletType();
     */
    public static void loadPortletType()
    {
        PortletType type = PortletType
                .createPortletType("CMS Content Directory",
                                   PortletType.WIDE_PROFILE,
                                   ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays the CMS content directory categories");
        s_log.info("Loading portlet type " + type);
    }

    /**
     * Reccurring initialization of the portlet each time ccm starts. 
     *
     * It registers an object instantiator with the portal infrastructure.
     * Usage: in initialize.init(domainInitEvent):
     *        ContentDirectoryPortlet.initPortlet();
     */
    public static void initPortlet()
    {
        // register Instantiator
        DomainObjectFactory.registerInstantiator(BASE_DATA_OBJECT_TYPE,
                                                 new ACSObjectInstantiator()
        {
            @Override
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new ContentDirectoryPortlet(dataObject);
            }
        });
    }

//  Alternative way to load and initialize a portlet using class PortletSetup.
//  It performs the loading step as well as the registerInstantiator step in
//  one class and can be used by the loader as well as the initializer. Was
//  especially useful in the old initializer system where the loading step and
//  the recurring initialization step were not separated.
//
//  private void loadContentDirectoryPortlet() {
//
//      PortletSetup setup = new PortletSetup(s_log);
//      setup.setPortletObjectType(ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
//      setup.setTitle("Content Directory");
//      setup.setDescription("Displays the content directory categories");
//      setup.setProfile(PortletType.WIDE_PROFILE);
//
//      setup.setInstantiator(new ACSObjectInstantiator() {
//          protected DomainObject doNewInstance(DataObject dataObject) {
//              return new ContentDirectoryPortlet(dataObject);
//          }
//      });
//      setup.run();
//  }


}
