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
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.search.Search;
import org.apache.log4j.Logger;


/**
 * This class is deprecated since it is Intermedia specific.
 * All code should now be using the generic search API found
 * in the com.arsdigita.search package.
 * @deprecated register a com.arsdigita.search.MetadataProvider instead
 **/

public abstract class SearchableACSObject extends ACSObject implements Searchable {

    protected void initialize() {
        super.initialize();
        if (Search.getConfig().isIntermediaEnabled()) {
            addObserver(new SearchableObserver(this));
        }
    }

    private static final Logger s_log =
        Logger.getLogger( SearchableACSObject.class.getName() );


    /**
     ** CONSTRUCTORS so that we are compatible with ACSObject
     **/
    public SearchableACSObject(DataObject SearchableACSObjectData) {
        super(SearchableACSObjectData);
    }
    public SearchableACSObject(String typeName) {
        super(typeName);
    }
    public SearchableACSObject(ObjectType type) {
        super(type);
    }
    public SearchableACSObject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }


    /**
     ** Searchable interface methods that must be implemented by any class that
     ** extends SearchableACSObject.  These methods return the content that
     ** is indexed.
     **/

    // These could be protected, but public may make it easier to debug
    abstract public String getSearchSummary();
    abstract public String getSearchLinkText();
    abstract public String getSearchUrlStub();
    abstract public String getSearchXMLContent();
    abstract public byte[] getSearchRawContent();
    abstract public String getContentSection();

    public String getSearchLanguage() {
        // Returns language type of document.  "eng" is english, (ISO 639-2)
        // If not English, should be overridden.
        return "eng";
    }
}
