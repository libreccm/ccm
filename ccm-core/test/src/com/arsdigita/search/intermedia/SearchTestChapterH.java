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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * SearchTestChapterH class.  Used to test generating XML documents
 * for related objects.
 *
 * @author Jeff Teeters
 **/
public class SearchTestChapterH extends ACSObject
    implements SearchIndexHelpCustomize {
    public static final String versionId = "$Id: SearchTestChapterH.java 746 2005-09-02 10:56:35Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.intermedia.SearchTestChapterH";

    public SearchTestChapterH() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SearchTestChapterH(String typeName) {
        super(typeName);
    }

    public SearchTestChapterH(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SearchTestChapterH(int key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public SearchTestChapterH(DataObject chapterData) {
        super(chapterData);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDisplayName() {
        return "SearchTestChapterH-DisplayName";
    }

    /* Register instantiator with DomainObjectFactory, must be called by
       initializer */
    public static void setupDomainObjectFactory() {
        DomainObjectInstantiator instantiator;

        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new SearchTestChapterH(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(
                                                 SearchTestChapterH.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);
    }


    public Integer getChapterNum() {
        return (Integer) get("chapterNum");
    }

    public void setChapterNum(Integer num) {
        set("chapterNum", num);
    }

    public BigDecimal getBookId() {
        return (BigDecimal) get("bookId");
    }

    public void setBookId(BigDecimal num) {
        set("bookId", num);
    }

    public String getContent() {
        return  (String)get("content");
    }

    public void setContent(String content) {
        set("content", content);
    }

    private final static String [] FIELDS = {
        "chapterNum" + "XML",
        "content" + "RAW"
    };

    public String[] searchIndexHelpFields() {
        return FIELDS;
    }
}
