/*
 * Copyright (c) 2013 Jens Pelzetter,
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
package com.arsdigita.cms.portletdataprovider;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PortletDataProviderServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = 60123844988240232L;

    private static final String ITEMS = "items";
    private static final String CATEGORIES = "categories";

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Application app) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        final String[] pathTokens = path.split("/");

        if (pathTokens.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (ITEMS.equals(pathTokens[0])) {
            serveContentItem(pathTokens, request, response);
        } else if (CATEGORIES.equals(pathTokens[0])) {
            throw new UnsupportedOperationException("Not implemtend yet");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void serveContentItem(final String[] pathTokens,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response) throws IOException,
                                                                               ServletException {
        final String itemIdString = pathTokens[1];
        final BigDecimal itemId;
        try {
            itemId = new BigDecimal(itemIdString);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format("'%s' is not a valid item id", itemIdString));
            return;
        }

        ContentItem item = null;
        try {
            final ContentItem tmpItem = new ContentItem(itemId);
            if (tmpItem.isLiveVersion()) {
                item = tmpItem;
            } else {
                if (tmpItem.isLive()) {
                    item = tmpItem.getLiveVersion();
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                       String.format("Item with ID %d is not published.",
                                                     itemIdString));
                }
            }
        } catch (DataObjectNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               String.format("Item with ID %d not found.", itemIdString));
            return;
        }
        final Page page = PageFactory.buildPage(
            "PortletDataProvider",
            String.format("ContentItem %s", item.getOID().toString()));
        final PortletDataItemPanel panel = new PortletDataItemPanel(item);

        page.add(panel);

        page.lock();

        final Document document = page.buildDocument(request, response);
        final PresentationManager presenter = Templating.getPresentationManager();
        presenter.servePage(document, request, response);
    }

    private class PortletDataItemPanel extends SimpleComponent {

        private final XMLGenerator xmlGenerator;

        public PortletDataItemPanel(final ContentItem item) {
            this.xmlGenerator = new XMLGenerator(item);
        }

        @Override
        public void generateXML(final PageState state, final Element parent) {
            final Element content = parent.newChildElement("cms:contentPanel",
                                                           CMS.CMS_XML_NS);
            xmlGenerator.generateXML(state, content, "");
        }

    }

    private class XMLGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XMLGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }

    }

}
