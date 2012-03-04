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

package com.arsdigita.bookmarks;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

/**
 * Bookmarks application domain class.
 * Central entry point into the bookmarks application.
 * @author dennis
 * @version $Id: Bookmarks.java#3  2003/07/10 14:47:30 $
 */
public class Bookmarks extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.workspace.BookmarkApplication";

    private static final int SORT_KEY_JUMP = 10;

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
    private static final org.apache.log4j.Logger log =
        org.apache.log4j.Logger.getLogger(Bookmarks.class);

    public Bookmarks(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Bookmarks(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public Bookmarks(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Use this instead of the constructor to create new Bookmark
     * Application objects
     */
    public static Bookmarks create(String urlName, String title,
                                             Application parent) {
        return (Bookmarks) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);
    }

    public Bookmark createBookmark(String name, String URLString) {
   
        Bookmark bmrk = new Bookmark(name,URLString);
        bmrk.setBookmarkApplication(this);
        return bmrk;
    }

    public void removeBookmark(Bookmark bmrk) {
        remove("bookmarks", bmrk);
    }

    public BookmarkCollection getBookmarks() {
        DataAssociation bmrksdata = (DataAssociation)get("bookmarks");
        BookmarkCollection bmrks = 
            new BookmarkCollection(bmrksdata.getDataCollection());
        bmrks.addOrder("sortKey");
        return bmrks;
    }

    public void swapBookmarkWithPrevious(Bookmark b) throws PersistenceException {
    	int newKey = b.getSortKey() - (SORT_KEY_JUMP + 1);
        b.setSortKey(newKey);

        b.save();

        normalizeBookmarkSortKeys();
    }

    public void swapBookmarkWithNext(Bookmark b) throws PersistenceException {
    	int newKey = b.getSortKey() + (SORT_KEY_JUMP + 1);
        b.setSortKey(newKey);

        b.save();

        normalizeBookmarkSortKeys();
    }

    public void normalizeBookmarkSortKeys() {
    	Bookmark b;

    	BookmarkCollection bColl = getBookmarks();

        for (int index = SORT_KEY_JUMP; bColl.next(); index += SORT_KEY_JUMP) {
            b = bColl.getBookmark();
            b.setSortKey(index);
            b.save();
        }
   }
    /**
     * Returns the servletPath part of the URL to the application servlet.
     * (see Servlet API specification or web.URL for more information)
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended for versions newer
     * than 1.0.4) you may use a standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>bookmarks</servlet-name>
     *   <servlet-class>com.arsdigita.bookmarks.BookmarksServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>bookmarks</servlet-name>
     *   <url-pattern>/bookmarks/*</url-pattern>
     * </servlet-mapping>
     *
     * @return ServelPath of the applications servlet
     */
    @Override
    public String getServletPath() {
        return "/bookmarks/";
    }

}
