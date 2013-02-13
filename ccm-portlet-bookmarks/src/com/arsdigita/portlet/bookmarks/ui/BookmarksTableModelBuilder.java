/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.portlet.bookmarks.ui;

import org.apache.log4j.Logger;

import com.arsdigita.portlet.bookmarks.BookmarksPortlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortletSelectionModel;
import com.arsdigita.cms.contenttypes.ui.LinkTableModelBuilder;
import com.arsdigita.persistence.DataCollection;

/**
 * Copied from authoting UI
 *
 * @version $Revision: 1.3 $ $Date: 2007/08/08 09:28:26 $
 * @author Chris Gilbert
 */

public class BookmarksTableModelBuilder 
    extends LinkTableModelBuilder {
    private static final Logger s_log = 
        Logger.getLogger(BookmarksTableModelBuilder.class);

    private PortletSelectionModel m_portlet;

    /**
     * Constructor. Creates a <code>BookmarksTableModelBuilder</code> given a
     * <code>PortletSelectionModel</code>  
     *
     * @param portlet The <code>PortletSelectionModel</code> that refers to the current portlet.
     * 
     */
    public BookmarksTableModelBuilder(PortletSelectionModel portlet) {
        m_portlet = portlet;
        s_log.debug("BookmarksTableModelBuilder");
    }

    /**
     * Returns the DataCollection of Bookmarks for the current
     * TableModel. 
     *
     * @param s The <code>PageState</code> for the current request
     * @return The DataCollection of Bookmarks
     */
    public DataCollection getLinks(PageState state) {
        BookmarksPortlet portlet = (BookmarksPortlet)m_portlet.getSelectedPortlet(state);
        s_log.debug("Getting related links for " + portlet.getOID());
        return portlet.getBookmarks();
        
    }
}
