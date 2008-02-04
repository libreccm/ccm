/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.ContentSectionDispatcher;
import com.arsdigita.xml.Element;

/**
 * @deprecated Use ContentItemPane instead
 *
 * A Bebop component that takes a the {@link ContentItem} from the request and
 * renders it as XML. The XML can then be styled with XSL in order to insert the
 * object's properties into the page.
 *
 * @author <a href="mailto:scott@arsdigita.com">Scott Seago</a>
 * @version $Id: ContentItemRenderer.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */

public class ContentItemRenderer extends DomainObjectRenderer {

    /* recursive depth of DomainObjectRenderer */
    public static final int XML_DEPTH = 3;

    /**
     * Construct a new <code>ContentItemRenderer</code>.
     *
     */
    public ContentItemRenderer() {
        super(new ItemSelectionModel("item_id"), XML_DEPTH);
    }

    /**
     * Generate XML for the domain object supplied by the
     * selection model.
     */
    public void generateXML(PageState state, Element parent) {
        ContentItem item = ContentSectionDispatcher.getContentItem(state.getRequest());
        Element e = generateXMLElement(item);
        if(e != null)
            e.addAttribute("id",getIdAttr());
        parent.addContent(e);
    }

}
