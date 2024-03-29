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
 * SearchTestAuthor class.  Used to test generating XML documents
 * for related objects.
 *
 * @author Jeff Teeters
 **/
public class SearchTestAuthor extends ACSObject {

    // Chapters associated with this author
    DataAssociation m_chapters = null;

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.intermedia.SearchTestAuthor";

    public SearchTestAuthor() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SearchTestAuthor(String typeName) {
        super(typeName);
    }

    public SearchTestAuthor(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SearchTestAuthor(int key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public SearchTestAuthor(DataObject authorData) {
        super(authorData);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDisplayName() {
        return "SearchTestAuthor-DisplayName";
    }

    /* Register instantiator with DomainObjectFactory, must be called by
       initializer */
    public static void setupDomainObjectFactory() {
        DomainObjectInstantiator instantiator;

        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new SearchTestAuthor(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(
                                                 SearchTestAuthor.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);
    }


    public void addChapter(SearchTestChapter chapter) {
        if (m_chapters == null) {
            m_chapters = (DataAssociation) get("chapters");
        }
        chapter.addToAssociation(m_chapters);
    }

    public void removeChapter(SearchTestChapter chapter) {
        if (m_chapters == null) {
            m_chapters = (DataAssociation) get("chapters");
        }
        chapter.removeFromAssociation(m_chapters);
    }




    public String getName() {
        return (String) get("name");
    }

    public void setName(String name) {
        set("name", name);
    }
}
