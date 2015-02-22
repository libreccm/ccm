/*
 * Copyright (C) 2015 University of Bremen. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;


/**
 * This content type represents an redirect to an external link or an 
 * other resource identified by an URL.
 * 
 * The item stores a description text about the link/resource and an URL.
 *
 * @author tosmers
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLink extends ContentPage {

    /**
     * PDL property name for definition
     */
    public static final String URL = "url";
    public static final String DESCRIPTION = "description";

    /**
     * Data object type for this domain object
     */
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contenttypes.ExternalLink";
    
    /**
     * Data object type for this domain object (for CMS compatibility)
     */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;

    /**
     * Default Constructor. Creates a new ExternalLink item.
     */
    public ExternalLink() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("ExternalLink type not registered", ex);
        }
    }

    /**
     * Creates an ExternalLink object for an item in the database.
     *
     * @param id The id of the ExternalLink to retrieve from the database.
     *
     * @throws DataObjectNotFoundException
     */
    public ExternalLink(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates an ExternalLink object for an item in the database.
     *
     * @param oid The {@link OID} of the item to retrieve from the database.
     *
     * @throws DataObjectNotFoundException
     */
    public ExternalLink(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Wraps an {@link DataObject} into an {@code ExternalLink} domain object.
     *
     * @param obj The {@link DataObject} to be wrapped.
     */
    public ExternalLink(final DataObject obj) {
        super(obj);
    }

    /**
     * Creates a new domain object for an subtype of ExternalLink.
     *
     * @param type The subtype for witch a new domain object will be created.
     */
    public ExternalLink(final String type) {
        super(type);
    }

    /**
     * Retrieve the target URL for this ExternalLink object.
     *
     * @return The URL to redirect to.
     */
    public String getURL() {
        return (String) get(URL);
    }

    /**
     * Set the target URL for this ExternalLink object. 
     * 
     * @param url The URL to redirect to.
     */
    public void setURL(final String url) {
        set(URL, url);
    }

    /**
     * Retrieve the description for the resource the URL is pointing to.
     *
     * @return Description about the URL this ExternalLink is redirecting to.
     */
    @Override
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Set the description for the resource the URL is pointing to.
     * 
     * @param description The description about the URL this ExternalLink 
     *                    is redirecting to.
     */
    @Override
    public void setDescription(final String description) {
        set(DESCRIPTION, description);
    }

}
