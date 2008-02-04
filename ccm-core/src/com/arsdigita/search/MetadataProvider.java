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

import java.util.Date;
import java.util.Locale;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;


/**
 * The interface defining an adapter between a domain object
 * and the search index metadata. Any domain object which
 * wishes to make itself searchable should register an
 * instance of this interface with the MetadataProviderRegistry
 * 
 * @see com.arsdigita.search.MetadataProviderRegistry
 * @see com.arsdigita.search.ContentProvider
 */
public interface MetadataProvider {
    
    /**
     * Gets an opaque fragment of object type specific metadata.
     * This method is a short term workaround for limitations
     * in our Lucene implementation. ie for c.a.cms.ContentPage
     * it lets us distinguish between live and draft items. It
     * will likely be removed in the near future. 
     *
     * @param dobj the domain object
     * @return the type specific metadata
     *
     * @pos $retval != null
     */
    String getTypeSpecificInfo(DomainObject dobj);
    
    /**
     * Gets the locale to which this object belongs
     *
     * @param dobj the domain object
     * @return the locale of the object
     *
     * @pos $retval != null
     */
    Locale getLocale(DomainObject dobj);

    /**
     * Gets the Title property for the DomainObject
     *
     * @param dobj the domain object
     * @return title of the object
     *
     * @post $retval != null
     */
    String getTitle(DomainObject dobj);
    
    /**
     * Gets the (optional) summary of the DomainObject
     *
     * @param dobj the domain object
     * @return the object summary, or null
     */
    String getSummary(DomainObject dobj);
    
    /**
     * Gets the (optional) creation date of the DomainObject
     *
     * @param dobj the domain object
     * @return the creation date, or null
     */
    Date getCreationDate(DomainObject dobj);

    /**
     * Gets the (optional) creating party of the DomainObject
     *
     * @param dobj the domain object
     * @return the creation party, or null
     */
    Party getCreationParty(DomainObject dobj);

    /**
     * Gets the (optional) last modification date of the DomainObject
     *
     * @param dobj the domain object
     * @return the modification date, or null
     */
    Date getLastModifiedDate(DomainObject dobj);

    /**
     * Gets the (optional) last modifying party of the DomainObject
     *
     * @param dobj the domain object
     * @return the modification party, or null
     */
    Party getLastModifiedParty(DomainObject dobj);

    /**
     * Gets the content for the DomainObject.
     * Content can be returned in multiple formats.
     * To see which the current indexer supports
     * use Search.getConfig().allowsXXXContent()
     *
     * @param dobj the domain object
     * @return array of content for this object, or an empty array
     * @post $retval != null
     */
    ContentProvider[] getContent(DomainObject dobj,
                                 ContentType type);

    /**
     * Gets the content section for the DomainObject.
     * @param dobj the domain object
     * @return the content section name or null
     **/
    String getContentSection(DomainObject dobj);
    
    /**
     * Allow searchable objects to decide at runtime whether they should 
     * be indexed or not. Normally should return true
     * @param dobj
     * @return
     */
    boolean isIndexable(DomainObject dobj);
    
}
