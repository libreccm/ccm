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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;


/**
 * Show ContentSection info in generated xml.
 */
public class ContentSectionComponent extends SimpleComponent {

    public static final String versionId = "$Id: ContentSectionComponent.java,v 1.1 2003/09/26 20:49:24 cwolfe Exp $ by $Author: cwolfe $, $DateTime: 2003/08/18 23:54:14 $";

    public ContentSectionComponent() {
        super();
    }

    /**
     * Generates XML that includes content section info.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {
	    ContentSection section = CMS.getContext().getContentSection();
	    if (section == null) { return; }
            Element content = parent.newChildElement("cms:contentSection", CMS.CMS_XML_NS);
	    content.addAttribute("displayName", section.getDisplayName());
	    
        }
    }

}
