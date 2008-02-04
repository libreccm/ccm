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
package com.arsdigita.search.intermedia;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * SearchTestChapter class.  Used to test SearchTestHelp class
 * (gathering content to index for related objects).
 *
 * @author Jeff Teeters
 **/
public class SearchTestChapter extends ACSObject {
    public static final String versionId = "$Id: SearchTestChapter.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.intermedia.SearchTestChapter";

    // Authors associated with this chapter
    DataAssociation m_authors = null;


    public SearchTestChapter() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SearchTestChapter(String typeName) {
        super(typeName);
    }

    public SearchTestChapter(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SearchTestChapter(int key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public SearchTestChapter(DataObject chapterData) {
        super(chapterData);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDisplayName() {
        return "SearchTestChapter-DisplayName";
    }

    /* Register instantiator with DomainObjectFactory, must be called by
       initializer */
    public static void setupDomainObjectFactory() {
        DomainObjectInstantiator instantiator;

        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new SearchTestChapter(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(
                                                 SearchTestChapter.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);
    }

    public void addAuthor(SearchTestAuthor author) {
        if (m_authors == null) {
            m_authors = (DataAssociation) get("authors");
        }
        author.addToAssociation(m_authors);
    }

    public void removeAuthor(SearchTestAuthor author) {
        if (m_authors == null) {
            m_authors = (DataAssociation) get("authors");
        }
        author.removeFromAssociation(m_authors);
    }


    public Integer getChapterNum() {
        return (Integer) get("chapterNum");
    }

    public void setChapterNum(Integer num) {
        set("chapterNum", num);
    }

    public SearchTestBook getBook() {
        DataObject book = (DataObject) get("book");
        if (book == null) {
            return null;
        } else {
            return (SearchTestBook) DomainObjectFactory.newInstance(book);
        }
    }

    public void setBook(SearchTestBook book) {
        setAssociation("book", book);
    }

    public String getContent() {
        return  (String)get("content");
    }

    public void setContent(String content) {
        set("content", content);
    }
}
