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

import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.ui.UI;
import com.arsdigita.xml.Element;

/**
 * Bread crumb trail for a content section.
 *
 * @author Michael Pih
 * @version $Id: ContentSectionNavbar.java 1942 2009-05-29 07:53:23Z terry $
 */
public class ContentSectionNavbar extends CMSContainer {

    private DimensionalNavbar m_navbar;

    public ContentSectionNavbar() {
        super();

        setClassAttr("section");

        String wsUrl = UI.getWorkspaceURL();
        String csUrl = Utilities.getWorkspaceURL();

        m_navbar = new DimensionalNavbar();
        m_navbar.setAlign(DimensionalNavbar.LEFT);
        m_navbar.setDelimiter(" > ");

        m_navbar.add(new Link( new Label(GlobalizationUtil.globalize("cms.ui.content_center")),  csUrl));
        m_navbar.add(new Label(GlobalizationUtil.globalize("cms.ui.content_section")));
        add(m_navbar);
    }


    public void generateXML(PageState state, Element parent) {

        Element element = parent.newChildElement(
            "cms:breadCrumbTrail", CMS.CMS_XML_NS
        );

        ContentSection section = CMS.getContext().getContentSection();
        element.addAttribute("name", section.getName());

        m_navbar.generateXML(state, element);

        exportAttributes(element);
    }

}
