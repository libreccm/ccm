/*
 * Copyright (C) 2014 Peter Boy, Universitaet Bremen. All Rights Reserved.
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
package com.arsdigita.cms.portlet;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.OID;
import com.arsdigita.portal.JSRPortlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;

/**
 * Currently a wrapper for ContentItemPortlet to deliver content to an JSR compliant portal server.
 *
 * WORK IN PROGRESS!
 *
 * @author pb
 * @author Jens Pelzetter <jens.pelzetter@scientificcms.org>
 */
public class ContentItemJSRPortlet extends JSRPortlet {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by editing
     * /WEB-INF/conf/log4j.properties int the runtime environment and set
     * com.arsdigita.portal.JSRPortlet=DEBUG by uncommenting or adding the line.
     */
    private static final Logger LOGGER = Logger.getLogger(ContentItemJSRPortlet.class);
    private static final String PREFS_SELECTED_ITEM = ".selectedContentItem";
    private static final String SELECTED_ITEM = "selectedItem";
    private static final String ACTION = "action";
    private static final String SELECT_ITEM = "selectItem";
    private static final String CONTENT_ITEM_SEARCH_STRING = "contentItemSearchString";
    private static final String CONTENT_SECTION_SELECT = "contentSectionSelect";

    private final List<String> errors = new ArrayList<String>();
    private String selectedContentSection;
    private String search;

    /**
     *
     * @param request
     * @param response
     *
     * @throws PortletException
     * @throws IOException
     */
    @Override
    protected void doEdit(final RenderRequest request, final RenderResponse response)
        throws PortletException, IOException {
        //response.setContentType("text/html");  
        //PrintWriter writer = new PrintWriter(response.getWriter());
        //writer.println("You're now in Edit mode.");  
        final ContentSectionCollection contentSections = ContentSection.getAllSections();
        final List<ContentSection> sections = new ArrayList<ContentSection>((int) contentSections
            .size());

        while (contentSections.next()) {
            sections.add(contentSections.getContentSection());
        }

        request.setAttribute("contentSections", sections);
        request.setAttribute("selectedContentSection", selectedContentSection);
        request.setAttribute(CONTENT_ITEM_SEARCH_STRING, search);

        if ((selectedContentSection != null)) {
            final ContentSection selectedSection = new ContentSection(OID.valueOf(
                selectedContentSection));
            final ItemCollection items = selectedSection.getItems();
            items.addFilter(String.format("(lower(name) LIKE lower('%%%s%%'))", search));
            items.addVersionFilter(true);
            items.addEqualsFilter("isFolder", false);
            items.addFilter("language != ''");

            final List<ContentItem> matchingItems = new ArrayList<ContentItem>((int) items.size());
            while (items.next()) {
                matchingItems.add(items.getContentItem());
            }

            request.setAttribute("matchingItems", matchingItems);
        }

        request.setAttribute("errors", errors);

        final String itemOID = request.getPreferences().getValue(response.getNamespace().concat(
            PREFS_SELECTED_ITEM), "");
        if (!itemOID.isEmpty()) {
            try {
                final OID oid = OID.valueOf(itemOID);
                ContentItem item = (ContentItem) DomainObjectFactory.newInstance(oid);
                if (item instanceof ContentBundle) {
                    final ContentBundle bundle = (ContentBundle) item;
                    if (bundle.hasInstance(request.getLocale().getLanguage())) {
                        item = bundle.getInstance(request.getLocale().getLanguage());
                    } else {
                        item = bundle.getPrimaryInstance();
                    }
                }

                request.setAttribute("selectedItemOID", item.getOID().toString());
                request.setAttribute("selectedItemPath", item.getPath());
                request.setAttribute("selectedItemTitle", item.getDisplayName());
                request.setAttribute("selectedItemType", item.getContentType().getName());

                if (item.getPublicVersion() == null) {
                    request.setAttribute("selectedItemStatus", "unpublished");
                } else {
                    request.setAttribute("selectedItemStatus", "published");
                }

            } catch (IllegalArgumentException ex) {
                //errors.add(String.format("The OID '%s' set in the preferences is invalid.",
                //                         itemOID));
                errors.add(MessageFormat.format(getResourceBundle(request.getLocale()).getString(
                    "contentItemJSRPortlet.errors.perferences.illegal_oid"),
                                                itemOID));
            } catch (DataObjectNotFoundException ex) {
                //errors.add(String.format("The item identified by the OID '%s' does not exist.",
                //                         itemOID));
                errors.add(MessageFormat.format(
                    getResourceBundle(request.getLocale()).getString(
                        "contentItemJSRPortlet.errors.perferences.item_does_not_exist"),
                    itemOID));
            }
        }

        request.setAttribute("namespace", response.getNamespace());

        //request.setAttribute("helloworld", "Hello World Attribute");
        final PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(
            "/templates/portlets/ContentItemJSRPortletAdmin.jsp");
        dispatcher.include(request, response);
    }

    /**
     *
     * @param request
     * @param response
     *
     * @throws PortletException
     * @throws IOException
     */
    @Override
    protected void doHelp(final RenderRequest request, final RenderResponse response)
        throws PortletException, IOException {
        response.setContentType("text/html");
        final PrintWriter writer = new PrintWriter(response.getWriter());
        writer.println("You're now in Help mode.");
    }

    /**
     *
     * @param request
     * @param response
     *
     * @throws PortletException
     * @throws IOException
     */
    @Override
    protected void doView(final RenderRequest request, final RenderResponse response)
        throws PortletException, IOException {
        response.setContentType("text/html");
        final PrintWriter writer = new PrintWriter(response.getWriter());
        writer.println("Hello world! You're in View mode.");
    }

    @Override
    public void processAction(final ActionRequest actionRequest,
                              final ActionResponse actionResponse) throws PortletException,
                                                                          IOException {
        if (actionRequest.getParameter(CONTENT_SECTION_SELECT) != null) {
            selectedContentSection = actionRequest.getParameter(CONTENT_SECTION_SELECT);
            search = actionRequest.getParameter(CONTENT_ITEM_SEARCH_STRING);
        }

        if (SELECT_ITEM.equals(actionRequest.getParameter(ACTION))) {
            final String itemOID = actionRequest.getParameter(SELECTED_ITEM);

            if ((itemOID == null) || itemOID.isEmpty()) {
                errors.add("OID is null");
            } else {
                try {
                    OID oid = OID.valueOf(itemOID);
                    final ContentItem item = new ContentItem(oid);

                    final ContentItem draftItem = item.getDraftVersion();
                    if (draftItem instanceof ContentPage) {
                        final ContentPage page = (ContentPage) draftItem;
                        final ContentBundle bundle = page.getContentBundle();
                        oid = bundle.getOID();
                    }

                    final PortletPreferences preferences = actionRequest.getPreferences();
                    preferences.setValue(actionResponse.getNamespace().concat(PREFS_SELECTED_ITEM),
                                         oid.toString());
                    preferences.store();

                } catch (IllegalArgumentException ex) {
                    errors.add(MessageFormat.format(getResourceBundle(actionRequest.getLocale()).
                        getString("contentItemJSRPortlet.errors.parameters.illegal_oid"),
                                                    itemOID));
                } catch (DataObjectNotFoundException ex) {
                    errors.add(MessageFormat.format(getResourceBundle(actionRequest.getLocale())
                        .getString(
                            "contentItemJSRPortlet.errors.parameters.item_does_not_exist"),
                                                    itemOID));
                }
            }
        }
    }

}
