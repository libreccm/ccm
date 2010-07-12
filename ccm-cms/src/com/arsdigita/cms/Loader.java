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
package com.arsdigita.cms;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.cms.installer.ContentCenterSetup;
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
//import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.cms.portlet.ContentItemPortlet;
//import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.xml.XML;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The CMS loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Loader extends PackageLoader {

    /** Private logger instance  */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    // Load main CMS configuration file
    private static final LoaderConfig s_conf = new LoaderConfig();


    /**
     * Constructor
     */
    public Loader() {

    }


    public void run(final ScriptContext ctx) {
        // XXX: Should move on demand initialization stuff here.
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                // ////////////////////////////////
                // Experimental:
                // Put ContentCenterSetup in Loader
                // ////////////////////////////////
//          final String workspaceURL = s_conf.getWorkspaceURL();
//          final String contentCenterMap = s_conf.getContentCenterMap();
//          ContentCenterSetup centerSetup = new ContentCenterSetup(
//              workspaceURL,
//              contentCenterMap);
//
//          centerSetup.run();

                // ////////////////////////
                // Loading content type definitions
                // Used to be step 2 in former enterprise.init file
                // ////////////////////////////////
                List contentTypes = s_conf.getCTDefFiles();
                if ( contentTypes != null) {
                    Iterator i = contentTypes.iterator();
                    while (i.hasNext()) {
                        String xmlFile = (String)i.next();
                        s_log.debug("Processing contentTypes in: " + xmlFile);
                        XML.parseResource(xmlFile, new XMLContentTypeHandler());
                    }
                }


                // ///////////////////////////////////////////////////////
                // Loading CMS portlets
                // Used to be step 5 (last step) in former enterprise.init
                // ///////////////////////////////////////////////////////
                //ContentDirectoryPortlet.loadPortletType();
                ContentItemPortlet.loadPortletType();
                //ContentSectionsPortlet.loadPortletType();
                TaskPortlet.loadPortletType();
                
            }
        }.run();
    }

}
