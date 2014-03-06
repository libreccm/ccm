/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.bookmarks.ui;


import org.apache.log4j.Logger;

import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bookmarks.Bookmarks;
import com.arsdigita.bookmarks.BookmarkCollection;
import com.arsdigita.bookmarks.util.GlobalizationUtil;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;


/** 
 * 
 * $Author: jparsons $
 * @version $Id: BookmarkPortlet.java#5 2003/05/28 17:31:24 $
 */
public class BookmarkPortlet extends AppPortlet {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(BookmarkPortlet.class);

    /**  PDL stuff - Data Object                                              */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.workspace.BookmarkPortlet";

    /**
     * Provide the Data Object Type.
     * @return DataObjectType as String 
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Constructor
     * 
     * @param dataObject 
     */
    public BookmarkPortlet(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getZoomURL() {
        Application app = getParentApplication();
        if (!PermissionService.checkPermission(
                new PermissionDescriptor(PrivilegeDescriptor.READ, 
                                         app, 
                                         Web.getWebContext().getUser()))) {
            return null;
        }
        return (URL.getDispatcherPath() + app.getPrimaryURL());
    }

    /**
     * 
     * @return 
     */
    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new BookmarkPortletRenderer(this);
    }
}

/**
 * 
 * 
 */
class BookmarkPortletRenderer extends AbstractPortletRenderer {

    private BookmarkPortlet m_portlet;

    /**
     * 
     * @param portlet 
     */
    public BookmarkPortletRenderer
        (BookmarkPortlet portlet) {
        m_portlet = portlet;
    }

    /**
     * 
     * @param pageState
     * @param parentElement 
     */
    protected void generateBodyXML(PageState pageState, Element parentElement) {
        Bookmarks bmrkapp = 
                 (Bookmarks)m_portlet.getParentApplication();

        // Variables used cursorwise.
        int counter;
        String name = null;
        String url = null;

        BookmarkCollection bmrks =
            bmrkapp.getBookmarks();

        GridPanel innerPanel = new GridPanel(1);

        for (counter = 0; bmrks.next(); counter++) {
            name = bmrks.getName();
            url = bmrks.getURL();
            ExternalLink link = new ExternalLink(name, url);
            if (bmrks.getNewWindow()) {
            	link.setTargetFrame("_blank");
            }
            innerPanel.add(link);
        }

        if (counter == 0) {
            innerPanel.add(new Label(GlobalizationUtil.globalize("bookmarks.ui.none")));
        }

        innerPanel.generateXML(pageState, parentElement);
    }
}
