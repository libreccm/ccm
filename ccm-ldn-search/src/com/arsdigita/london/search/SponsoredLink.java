/*
 * Copyright (C) 2004 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.search;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

/**
 * A "Sponsored Link" is a mapping from a search term to
 * a URL.  It will usually be used to display a link to the URL 
 * when someone types in a search query containing the specified term.
 **/
public class SponsoredLink extends ACSObject {

    /**
     * The default PDL object type.
     **/
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.search.SponsoredLink";

    /**
     * The PDL "title" attribute.
     **/
    public static final String TITLE = "title";
    /**
     * The PDL "term" attribute.
     **/
    public static final String TERM = "term";
    /**
     * The PDL "url" attribute.
     **/
    public static final String URL  = "url";

    /**
     * Create a new SponsoredLink.
     **/
    public SponsoredLink() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Create a new SponsoredLink.
     *
     * @param type The PDL type of the object to create.  Must be
     *             "com.arsdigita.london.search.SponsoredLink" or a
     *             subtype.
     **/
    public SponsoredLink(String type) {
        super(type);
    }

    /**
     * Load an existing SponsoredLink.
     *
     * @param dobj The {@link DataObject} of the existing SponsoredLink.
     **/
    public SponsoredLink(DataObject dobj) {
        super(dobj);
    }

    /**
     * Load an existing SponsoredLink.
     *
     * @param oid The {@link OID} of the SponsoredLink to load
     *            from the database.
     **/
    public SponsoredLink(OID oid) {
        super(oid);
    }

    /**
     * Retrieve all SponsoredLinks.
     *
     * @return An {@link ACSObjectCollection} of all SponsoredLinks.
     **/
    public static ACSObjectCollection retrieveAll() {
        DataCollection coll = SessionManager.getSession().
            retrieve(BASE_DATA_OBJECT_TYPE);
        return new ACSObjectCollection(coll);
    }

    /**
     * Retrieve all matching SponsoredLinks.
     *
     * @param term Search for SponsoredLinks with this term.
     * @return An {@link ACSObjectCollection} of SponsoredLinks that match the given term.
     **/
    public static ACSObjectCollection retrieveLinksForTerm(String term) {
        Assert.isTrue(term != null && term.length() > 0);
        ACSObjectCollection coll = retrieveAll();
        coll.addEqualsFilter(SponsoredLink.TERM, term);

        return coll;
    }

    /**
     * Get the default PDL object type.
     *
     * @return The value of BASE_DATA_OBJECT_TYPE.
     **/
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Get the display name for this SponsoredLink.
     **/
    public String getDisplayName() {
        return "Sponsored Link: " + getTerm();
    }
    
    /**
     * Get the title to display for this SponsoredLink.
     *
     * @return The title.
     **/
    public String getTitle() {
        return (String) get(TITLE);
    }

    /**
     * Set the title for this SponsoredLink.
     *
     * @param title The new title.
     **/
    public void setTitle(String title) {
        Assert.isTrue(title != null && title.length() > 0);
        set(TITLE, title);
    }
    
    /**
     * Get the term for this SponsoredLink.
     *
     * @return The term.
     **/
    public String getTerm() {
        return (String) get(TERM);
    }

    /**
     * Set the term for this SponsoredLink.
     *
     * @param term The new term.
     **/
    public void setTerm(String term) {
        Assert.isTrue(term != null && term.length() > 0);
        set(TERM, term);
    }

    /**
     * Get the URL for this SponsoredLink.
     *
     * @return The URL.
     **/
    public String getURL() {
        return (String) get(URL);
    }

    /**
     * Set the URL for this SponsoredLink.
     *
     * @param The new URL.
     **/
    public void setURL(String url) {
        Assert.isTrue(url != null && url.length() > 0);
        set(URL, url);
    }
}
