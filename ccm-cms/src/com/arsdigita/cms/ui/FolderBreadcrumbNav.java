/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Generates XML that is breadcrumb trail.  Includes all folders in
 * the path to the currently requested content item, minus the content
 * section root folder.
 *
 * @author Crag Wolfe
 */
public class FolderBreadcrumbNav extends SimpleComponent {

    public static final String versionId = "$Id: FolderBreadcrumbNav.java,v 1.2 2003/12/18 18:33:55 cwolfe Exp $ by $Author: cwolfe $, $DateTime: 2003/08/18 23:54:14 $";

    private static Logger s_log =
        Logger.getLogger(FolderBreadcrumbNav.class);

    public FolderBreadcrumbNav() {
        super();
    }

    /**
     * Generates XML for a breadcrumb trail
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public void generateXML(PageState state, Element parent) {
        if ( !isVisible(state)) {
            return;
        }
        if (!CMS.getContext().hasContentItem()) {
            s_log.debug("no content item found for current request");
            return;
        }
 
        ContentItem curItem = CMS.getContext().getContentItem();
       
        ArrayList folders = new ArrayList(8);
        
        Folder f = null;
        try {
            f = (Folder) curItem;
            folders.add(0,f);
        } catch (java.lang.ClassCastException ccex) {
            f = (Folder)
                ((ContentBundle) curItem.getParent()).getParent();
            folders.add(0,f);
        }
        
        f = (Folder) f.getParent();
        while (f != null) {
            folders.add(0,f);
            f = (Folder) f.getParent();
        }

        // EE 2006-01-18 disabled this
        //if (folders.size() < 2) {
        //    return;
        //}

        Element component = parent.newChildElement
            ("cms:folderPathLinks", CMSPage.CMS_XML_NS);
        component.addAttribute("id","folderPath");

        // root folder of the content section is not in breadcrumb
        // trail, so remove it
        folders.remove(0);

        String contentSection = CMS.getContext().getContentSection().getURL();

        while (folders.size() > 0) {
            f = (Folder) folders.remove(0);
            Element folderElem = component.newChildElement
                ("cms:folderPathLink", CMSPage.CMS_XML_NS);
            folderElem.addAttribute("title", f.getLabel());
            folderElem.addAttribute
                ("url", contentSection+f.getPath()+"/");
        }
    }
}
