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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.cms.Workspace;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/**
 * Bread crumb trail for a content item.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #12 $ $Date: 2004/08/17 $
 * @version $Id: ContentItemNavbar.java 287 2005-02-22 00:29:02Z sskracic $ 
 */
public class ContentItemNavbar extends CMSContainer {

    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentItemNavbar.class);

    private ItemSelectionModel m_item;

    public ContentItemNavbar(ItemSelectionModel item) {
        super();
        m_item = item;
        setClassAttr("item");
    }

    /**
     * 
     * @param state
     * @param parent 
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        Element element = new Element("cms:breadCrumbTrail", CMS.CMS_XML_NS);

        ContentSection section = CMS.getContext().getContentSection();
        ContentItem item = m_item.getSelectedItem(state);
        ContentType type = item.getContentType();

        final String url = URL.there(state.getRequest(),
                                     Workspace.getURL()).toString();

        element.addAttribute("workspaceURL", url);
        element.addAttribute("sectionName", section.getName());

        // If the item is a template, the backlink should go to the root
        // folder, not the templates folder (since the user doesn't know
        // the templates folder exists).

        String returnFolder = "";

        if (! type.getClassName().equals(com.arsdigita.cms.Template.BASE_DATA_OBJECT_TYPE)) {
            returnFolder = "?" + ContentSectionPage.SET_FOLDER + "=" +
                item.getParent().getID().toString();
        }

        element.addAttribute("sectionURL", section.getURL() +
                             PageLocations.SECTION_PAGE +
                             returnFolder);
        element.addAttribute("type", type.getLabel());
        element.addAttribute("name", item.getDisplayName());
        exportAttributes(element);
        parent.addContent(element);
    }

}
