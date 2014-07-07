package com.arsdigita.cms.portlet;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseServlet;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.math.BigDecimal;
import javax.portlet.PortletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is the backend for the the {@link ContentItemJSRPortlet} and provides the view of a
 * content item. Using a servlet is necessary to include the transformed HTML of the content item.
 * The {@link PageTransformer} requires a HTTP request but in the Portlet only a
 * {@link PortletRequest} is available. And a {@link PortletRequest} can't be converted into a
 * {@link ServletHttpRequest}.
 *
 * @author Jens Pelzetter <jens.pelzetter@scientificcms.org>
 */
public class ContentItemPortletItemProviderServlet extends BaseServlet{  //BaseApplicationServlet {

    private static final String ITEMS = "items";
    private static final String CATEGORIES = "categories";

    /**
     * This method is the entry point to the logic of this class. The method analyses the path (from
     * {@link HttpServletRequest#getPathInfo()}) and delegates to the responsible method of this
     * class.
     *
     * @param request
     * @param response
     * @param app
     * @throws ServletException
     * @throws IOException
     */
    @Override
//    protected void doService(final HttpServletRequest request,
//                             final HttpServletResponse response,
//                             final Application app) 
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        final String[] pathTokens = path.split("/");

        if (pathTokens.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //Check the first token of the path and delegate the clal
        if (ITEMS.equals(pathTokens[0])) {
            serveContentItem(pathTokens, request, response);
        } else if (CATEGORIES.equals(pathTokens[0])) {
            throw new UnsupportedOperationException("Not implemtend yet");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Serves an HTML fragment showing the detail view of a specific content item. Will create a
     * BAD_REQUEST HTTP error if the provided ID/OID is invalid and a NOT_FOUND HTTP error if no
     * content item with the provided ID/OID is found.
     *
     * @param pathTokens The tokens of the path
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    protected void serveContentItem(final String[] pathTokens,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response) throws IOException,
                                                                               ServletException {
        //Retrieve the content item and ensure that we work with the live version of the item.
        ContentItem item = null;
        try {
            final ContentItem tmpItem = retrieveContentItem(pathTokens[1]);
            if (tmpItem.isLiveVersion()) {
                item = tmpItem;
            } else {
                if (tmpItem.isLive()) {
                    item = tmpItem.getLiveVersion();
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                       String.format("Item with ID %d is not published.",
                                                     pathTokens[1]));
                }
            }
        } catch (DataObjectNotFoundException ex) {
            //No item with the provided ID/OID found, respond with NOT_FOUND (404).
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               String.format("Item with ID/PID %d not found.", pathTokens[1]));
            return;
        } catch (NumberFormatException ex) {
            //The provided ID was not a number, respond with bad request error.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format("'%s' is not a valid item id", pathTokens[1]));
            return;
        } catch (IllegalArgumentException ex) {
            //The provided OID is was invalid, respond with bad request error.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format("'%s' is not a valid item OID", pathTokens[1]));
            return;
        }

        //Create the XML output
        final Page page = PageFactory.buildPage(
                "PortletDataProvider",
                String.format("ContentItem %s", item.getOID().toString()));
        final PortletDataItemPanel panel = new PortletDataItemPanel(item);
        page.add(panel);
        page.lock();

        //Delegate to theming enging
        final Document document = page.buildDocument(request, response);
        final PresentationManager presenter = Templating.getPresentationManager();
        presenter.servePage(document, request, response);
    }

    /**
     * Helper method encapsulating the logic for retrieving a content item. If the first character
     * of the provided item id is a digit the method assumes that the numeric ID of the item was
     * provided. Otherwise the method assumes that the OID of the item to serve was provided.
     *
     * @param itemId
     * @return
     */
    private ContentItem retrieveContentItem(final String itemId) {
        if (Character.isDigit(itemId.charAt(0))) {
            return new ContentItem(new BigDecimal(itemId));
        } else {
            return new ContentItem(OID.valueOf(itemId));
        }
    }

    /**
     * Special component to make it possible to use special XSL for the data served by the
     * PortletDataProvider
     *
     */
    private class PortletDataItemPanel extends SimpleComponent {

        private final XMLGenerator xmlGenerator;

        public PortletDataItemPanel(final ContentItem item) {
            super();
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
