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
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * SearchTestBookH class.  Used to test generating XML documents
 * for related objects.
 *
 * @author Jeff Teeters
 **/
public class SearchTestBookH extends SearchableACSObject {

    // Chapters associated with this book
    DataAssociation m_chapters = null;

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.intermedia.SearchTestBookH";

    public SearchTestBookH() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SearchTestBookH(String typeName) {
        super(typeName);
    }

    public SearchTestBookH(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SearchTestBookH(int key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public SearchTestBookH(DataObject dataObject) {
        super(dataObject);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDisplayName() {
        return "SearchTestBookH-DisplayName";
    }

    public String getContentSection() {
        return "";
    }

    /* Register instantiator with DomainObjectFactory, must be called by
       initializer */
    public static void setupDomainObjectFactory() {
        DomainObjectInstantiator instantiator;

        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new SearchTestBookH(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(
                                                 SearchTestBookH.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);
    }



    public void addChapter(SearchTestChapterH chapter) {
        if (m_chapters == null) {
            m_chapters = (DataAssociation) get("chapters");
        }
        chapter.addToAssociation(m_chapters);
    }

    public void removeChapter(SearchTestChapterH chapter) {
        if (m_chapters == null) {
            m_chapters = (DataAssociation) get("chapters");
        }
        chapter.removeFromAssociation(m_chapters);
    }


    public String getTitle() {
        return (String) get("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    // Methods needed to implment Searchable interface
    public String getSearchSummary()  { return ""; };
    public String getSearchLinkText() { return ""; };
    public String getSearchUrlStub()  { return ""; };
    public String getSearchXMLContent() {
        return "use_SearchIndexHelp";
    }
    public byte[] getSearchRawContent() {
        return "use_SearchIndexHelp".getBytes();
    }
}
