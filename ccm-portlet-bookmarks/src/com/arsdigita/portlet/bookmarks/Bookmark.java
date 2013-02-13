/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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
package com.arsdigita.portlet.bookmarks;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * Extends link, but has no additional data. Extension is to enable overriding
 * of swap methods which don't work in Link - if you delete a link in the middle
 * you cannot move links past the resulting gap
 * 
 * @author Chris Gilbert (cgyg9330)
 */
public class Bookmark extends Link implements BookmarkConstants{

    /** Private Logger instance for debugging purpose.                        */
    private static Logger s_log = Logger.getLogger(Bookmark.class);

    /** PDL stuff                                                             */
    public static String BASE_DATA_OBJECT_TYPE =
                         "com.arsdigita.portlet.Bookmark";

    public Bookmark() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Bookmark(DataObject data) {
        super(data);
    }

    public Bookmark( OID id ) throws DataObjectNotFoundException {
        super( id );
    }
	
    public Bookmark( BigDecimal id ) throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    /**
     * Swaps this <code>Bookmark</code> with the next one,
     * according to the linkOrder
     */
    @Override
    public void swapWithNext() {
        s_log.debug("Start - swapWithNext");
        swapWithNext("com.arsdigita.portlet.minRelatedLinkOrderForPortlet",
                     "com.arsdigita.portlet.swapRelatedLinkWithNextInGroup");
    }

    /**
     * Swaps this <code>Bookmark</code> with the previous one,
     * according to the linkOrder
     */
    @Override
    public void swapWithPrevious() {
        s_log.debug("Start - swapWithPrevious");
        swapWithPrevious("com.arsdigita.portlet.maxRelatedLinkOrderForPortlet",
                         "com.arsdigita.portlet.swapRelatedLinkWithNextInGroup");
    }

    /**
     * Given a dataquery name, returns the (possibly filtered)
     * DataQuery for use in swapKeys. This implementation filters
     * on the <code>portlet</code> property, so that only
     * Bookmarks which belong to the same <code>BookmarksPortlet</code>
     * will be swapped.
     *
     * @param queryName name of the DataQuery to use
     * @return the DataQuery
     */
    @Override
    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = super.getSwapQuery(queryName);
        BigDecimal portletID = BookmarksPortlet
                               .getPortletForBookmark(this).getID();
        s_log.debug("setting owner in swapquerie as " + portletID);
        query.setParameter("ownerID", portletID);
        return query;
    }

    /**
     * Given a data operation name, returns the
     * DataOperation for use in swapKeys. This implementation sets the
     * portlet parameter, in addition to what is set by
     * super.getSwapOperation
     *
     * @param operationName the Name of the DataOperation to use
     *
     * @return the DataOperation used to swap the sort keys.
     */
    @Override
	protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = super.getSwapOperation(operationName);
        BigDecimal portletID =
                BookmarksPortlet.getPortletForBookmark(this).getID();
        s_log.debug("setting owner in swapoperation as " + portletID);

        operation.setParameter("ownerID", portletID);
        return operation;
    }

    /**
     * This method is only used for setting initial sort keys for
     * links which exist without them. This is called by swapKeys
     * instead of attempting to swap if the key found is
     * null. This implementation sorts all Bookmarks owned by this
     * Bookmark's portlet by title.
     */
    @Override
    protected void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection links = session.retrieve(BASE_DATA_OBJECT_TYPE);
        links.addEqualsFilter( PORTLET + ".id", 
                               BookmarksPortlet.getPortletForBookmark(this).getID());
        links.addOrder(TITLE);
        int sortKey = 0;
        while (links.next()) {
            sortKey++;
            Link link = new Bookmark(links.getDataObject());
            link.setOrder(sortKey);
            link.save();
        }

    }

}
