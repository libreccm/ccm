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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionServlet;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * <p>This <code>ContentPanel</code> component fetches
 * the {@link com.arsdigita.cms.dispatcher.XMLGenerator} for the content
 * section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #10 $ $Date: 2004/08/17 $
 * @version $Id: ContentPanel.java 1848 2009-03-10 03:17:42Z terry $
 */
public class ContentPanel extends SimpleComponent {

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
        Assert.exists(section);
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
        if (isVisible(state)) {
            Element content = parent.newChildElement("cms:contentPanel", CMS.CMS_XML_NS);
            exportAttributes(content);

            // Generate path information about the content item
            generatePathInfoXML(state, content);

            // Take advantage of caching in the CMS Dispatcher.
            XMLGenerator xmlGenerator = getXMLGenerator(state);

            xmlGenerator.generateXML(state, content, null);
        }
    }
    
    /**
     * Generate information about the path to this content item.
     * 
     * @param state the page state
     * @param parent the element that will contain the path info
     */
    protected void generatePathInfoXML(PageState state, Element parent) {
        Element pathInfo = parent.newChildElement("cms:pathInfo", CMS.CMS_XML_NS);
        
        if (CMS.getContext().hasContentSection()) {
            pathInfo.newChildElement("cms:sectionPath", CMS.CMS_XML_NS).setText(
                    CMS.getContext().getContentSection().getPath());
        }
        String url = DispatcherHelper.getRequestContext().getRemainingURLPart();
        if (url.startsWith(CMSDispatcher.PREVIEW)) {
            pathInfo.newChildElement("cms:previewPath", CMS.CMS_XML_NS).setText(ContentSectionServlet.PREVIEW);
        }
        pathInfo.newChildElement("cms:templatePrefix", CMS.CMS_XML_NS).setText(
                "/" + AbstractItemResolver.TEMPLATE_CONTEXT_PREFIX);

        if (CMS.getContext().hasContentItem()) {
            ContentItem item = CMS.getContext().getContentItem();
            ContentItem bundle = (ContentItem) item.getDraftVersion().getParent();
            pathInfo.newChildElement("cms:itemPath", CMS.CMS_XML_NS).setText("/" + bundle.getPath());
        }
    }
}
