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
package com.arsdigita.search;

import com.arsdigita.persistence.OID;
import com.arsdigita.kernel.Party;
import java.util.Date;
import java.util.Locale;
import java.net.URL;
import java.math.BigDecimal;

/**
 * The Document interface provides access to the metadata associated 
 * with a single document obtained from the search index. Rather
 * than implementing this interface directly, it is preferrable to
 * subclass BaseDocument since this provides protection against
 * future additions this interface
 *
 * @see com.arsdigita.search.ResultSet
 * @see com.arsdigita.search.BaseDocument
 */
public interface Document {

    /**
     * Gets the unique OID for the domain object
     * referenced by this document
     * @return the unique OID
     */
    OID getOID();

    /**
     * Gets the url for this document
     * @return the document URL
     */
    URL getURL();

    /**
     * Gets the locale to which this object belongs
     *
     * @return the locale of the object
     *
     * @pos $retval != null
     */
    Locale getLocale();

    /**
     * Gets the Title property for the DomainObject
     *
     * @return title of the object
     *
     * @post $retval != null
     */
    String getTitle();

    /**
     * Gets the (optional) summary of the DomainObject
     *
     * @return the object summary, or null
     */
    String getSummary();
    
    /**
     * Gets the (optional) creation date of the DomainObject
     *
     * @return the creation date, or null
     */
    Date getCreationDate();

    /**
     * Gets the (optional) creating party of the DomainObject
     *
     * @return the creation party, or null
     */
    Party getCreationParty();

    /**
     * Gets the (optional) last modification date of the DomainObject
     *
     * @return the modification date, or null
     */
    Date getLastModifiedDate();

    /**
     * Gets the (optional) last modifying party of the DomainObject
     *
     * @return the modification party, or null
     */
    Party getLastModifiedParty();
    
    /**
     * Gets the document score. The range of values returned
     * by this method are dependant on the indexer backend 
     * the produced the result.
     *
     * @return the score
     */
    BigDecimal getScore();

    /**
     * Gets the title of the content section within which 
     * this item resides.
     **/
    String getContentSection();

}
