/*
 * Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.camden.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;


/**
 * This content type represents an redirect to an external form or an other resource identified by
 * an URL.
 * 
 * The item stores a description text about the form/resource and an URL.
 *
 * @version $Id: EForm.java 2570 2013-11-19 12:49:34Z jensp $
 */
public class EForm extends ContentPage {

    /**
     * PDL property name for definition
     */
    public static final String URL = "url";
    public static final String DESCRIPTION = "description";

    /**
     * Data object type for this domain object
     */
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.camden.cms.contenttypes.EForm";
    /**
     * Data object type for this domain object (for CMS compatibility)
     */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;

    /**
     * Default Constructor. Creates a new eForm item.
     */
    public EForm() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("EForm type not registered", ex);
        }
    }

    /**
     * Creates an eForm object for an item in the database.
     *
     * @param id The id of the eForm to retrieve from the database.
     *
     * @throws DataObjectNotFoundException
     */
    public EForm(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates an eForm object for an item in the database.
     *
     * @param oid The {@link OID} of the item to retrieve from the database.
     *
     * @throws DataObjectNotFoundException
     */
    public EForm(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Wraps an {@link DataObject} into an {@code EForm} domain object.
     *
     * @param obj
     */
    public EForm(final DataObject obj) {
        super(obj);
    }

    /**
     * Creates a new domain object for an subtype of EForm.
     *
     * @param type
     */
    public EForm(final String type) {
        super(type);
    }

    /**
     * Retrieve the target URL for this e-Form object.
     *
     * @return The URL to redirect to.
     */
    public String getURL() {
        return (String) get(URL);
    }

    /**
     * Set the target URL for this e-Form object. 
     *
     * @return
     */
    public void setURL(final String url) {
        set(URL, url);
    }

    /**
     * Retrieve the description for the resource the URL is pointing to.
     * 
     *
     * @return Description about the URL this EForm is redirecting to.
     */
    @Override
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Set the description for this e-Form object.
     *
     */
    @Override
    public void setDescription(final String description) {
        set(DESCRIPTION, description);
    }

}
