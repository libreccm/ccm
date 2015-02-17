/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;

import com.arsdigita.search.Search;
import com.arsdigita.search.Document;
import com.arsdigita.search.ui.QueryGenerator;
import com.arsdigita.search.ui.ResultsPane;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

/**
 * An extension of {@link ItemSearch} for use in a popup search window. The display of results is
 * altered so that selecting a result closes the window & passes the id of the selected item back to
 * the opener.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearchPopup.java 1397 2006-11-29 14:10:38Z sskracic $
 */
public class ItemSearchPopup extends ItemSearch {

//    private static final org.apache.log4j.Logger s_log = org.apache.log4j.Logger.getLogger(
//        ItemSearchPopup.class);
    public static final String WIDGET_PARAM = "widget";
    public static final String URL_PARAM = "useURL";
    public static final String QUERY = "query";

    /**
     * Construct a new <code>ItemSearchPopup</code> component
     *
     * @param context               the context for the retrieved items. Should be
     *                              {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param limitToContentSection limit the search to the current content section
     */
    public ItemSearchPopup(final String context, final boolean limitToContentSection) {
        super(context, limitToContentSection);
    }

    // Hide results by default
    @Override
    public void register(final Page page) {
        super.register(page);
        page.addGlobalStateParam(new StringParameter(WIDGET_PARAM));
        page.addGlobalStateParam(new StringParameter(URL_PARAM));
        page.addGlobalStateParam(new StringParameter(QUERY));
    }

    @Override
    protected ItemSearchSection createSearchSection(final String context, 
                                                    final boolean limitToContentSection) {
        return new ItemSearchSectionPopup(context, limitToContentSection);
    }

    private static class ItemSearchSectionPopup extends ItemSearchSection {

        public ItemSearchSectionPopup(final String context, 
                                      final boolean limitToContentSection) {
            super(context, limitToContentSection);
        }

        @Override
        protected Component createResultsPane(final QueryGenerator generator) {
            return new PopupResultsPane(generator);
        }

    }

    /**
     * The default context is Live.No need to append &context=live explicitly.
     * @param request
     * @param oid
     * @return 
     */
    public static String getItemURL(final HttpServletRequest request, final OID oid) {
        // redirect doesn't use /ccm prefix for some reason, so just returning the raw string.
        //ParameterMap map = new ParameterMap();
        //map.setParameter("oid", oid.toString());
        //return URL.there(request, "/redirect/", map).toString();
        // Always link directly to the live version.
        if (Web.getWebappContextPath() == null) {
            return "/redirect/?oid=" + oid.toString();
        } else {
            return Web.getWebappContextPath() + "/redirect/?oid=" + oid.toString();
        }
    }

    private static class PopupResultsPane extends ResultsPane {

        public PopupResultsPane(final QueryGenerator generator) {
            super(generator);
            setRelativeURLs(true);
            setSearchHelpMsg(GlobalizationUtil.globalize("cms.ui.search.help"));
            setNoResultsMsg(GlobalizationUtil.globalize("cms.ui.search.no_results"));
        }

        @Override
        protected Element generateDocumentXML(final PageState state, 
                                              final Document doc) {
            final Element element = super.generateDocumentXML(state, doc);

            element.addAttribute("class", "jsButton");

            final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
            final String searchWidget = (String) state.getValue(new StringParameter("searchWidget"));

            final boolean useURL = "true".equals(state.getValue(new StringParameter(URL_PARAM)));

            String fillString;
            if (useURL) {
                fillString = getItemURL(state.getRequest(), doc.getOID());
            } else {
                fillString = doc.getOID().get("id").toString();
            }

            final String title = doc.getTitle();

            final Element jsLabel = Search.newElement("jsAction");
            jsLabel.addAttribute("name", "fillItem"
                                             + doc.getOID().get("id") + "()");
            jsLabel.setText(generateJSLabel((BigDecimal) doc.getOID().get("id"),
                                            widget, 
                                            searchWidget, 
                                            fillString, 
                                            title));
            jsLabel.addAttribute("action",
                                 String.format(
                                     "window.opener.document.%s.value = \"%s\"; self.close(); return false;",
                                     widget, 
                                     fillString));
            element.addContent(jsLabel);

            return element;
        }

        private String generateJSLabel(final BigDecimal id, 
                                       final String widget, 
                                       final String searchWidget,
                                       final String fill, 
                                       final String title) {
            return " <script language=\"javascript\"> "
                       + " <!-- \n"
                       + " function fillItem" + id + "() { \n"
                       + " alert('test');\n"
                       + " window.opener.document." + widget + ".value=\"" + fill + "\";\n"
                       //+ " window.opener.document." + searchWidget + ".value=\"" + title.replace(
                       //"\"", "\\\"") + "\";\n"
                       + " self.close(); \n"
                       + " return false; \n"
                       + " } \n"
                       + " --> \n"
                       + " </script> ";
        }

    }

}
