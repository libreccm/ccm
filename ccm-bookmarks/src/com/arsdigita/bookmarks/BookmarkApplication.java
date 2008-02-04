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
 * BookmarkApplication class.
 *
 */

public class BookmarkApplication extends Application {

    public static final String versionId = "$Id: //apps/bookmarks/dev/src/com/arsdigita/bookmarks/BookmarkApplication.java#3 $ by $Author: dennis $, $DateTime: 2003/07/10 14:47:30 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.BookmarkApplication";

    private static final int SORT_KEY_JUMP = 10;

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
    private static final org.apache.log4j.Logger log =
        org.apache.log4j.Logger.getLogger(BookmarkApplication.class);

    public BookmarkApplication(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public BookmarkApplication(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public BookmarkApplication(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Use this instead of the constructor to create new Bookmark
     * Application objects
     */
    public static BookmarkApplication create(String urlName, String title,
                                             Application parent) {
        return (BookmarkApplication) Application.createApplication
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
}
