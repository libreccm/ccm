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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;


/**
 * <p>This <code>ContentPanel</code> component fetches
 * the {@link com.arsdigita.cms.dispatcher.XMLGenerator} for the content
 * section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #10 $ $Date: 2004/08/17 $
 */
public class ContentPanel extends SimpleComponent {

    public static final String versionId = "$Id: ContentPanel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public ContentPanel() {
        super();
    }

    /**
     * Fetches an XML Generator. This method can be overidden to fetch
     * any {@link com.arsdigita.cms.dispatcher.XMLGenerator}, but by default,
     * it fetches the <code>XMLGenerator</code> registered to the current
     * {@link com.arsdigita.cms.ContentSection}.
     *
     * @param state The page state
     */
    protected XMLGenerator getXMLGenerator(PageState state) {
        ContentSection section = CMS.getContext().getContentSection();
        Assert.assertNotNull(section);
        return section.getXMLGenerator();
    }

    /**
     * Generates XML that represents a content item.
     *
     * @param state The page state
     * @param parent The parent DOM element
     * @see com.arsdigita.cms.dispatcher.XMLGenerator
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {
            Element content = parent.newChildElement("cms:contentPanel", CMS.CMS_XML_NS);
            exportAttributes(content);

            // Take advantage of caching in the CMS Dispatcher.
            XMLGenerator xmlGenerator = getXMLGenerator(state);

            xmlGenerator.generateXML(state, content, null);
        }
    }

}
