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
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLink extends ContentPage {

    /** PDL property url */
    public static final String URL = "url";
    /** PDL property comment */
    public static final String COMMENT = "comment";
    /** PDL property showComment */
    public static final String SHOW_COMMENT = "showComment";
    /** PDL property targetWindow */
    public static final String TARGET_WINDOW = "targetWindow";

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
     * @param type The subtype for which a new domain object will be created.
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
     * Retrieve the comment for this ExternalLink object.
     *
     * @return The COMMENT shown when opening this link.
     */
    public String getComment() {
        return (String) get(COMMENT);
    }

    /**
     * Set the Comment for this ExternalLink object. 
     * 
     * @param comment The COMMENT shown when opening this Link.
     */
    public void setComment(final String comment) {
        set(COMMENT, comment);
    }
    
    /**
     * Retrieve the value weather the comment will be shown.
     *
     * @return the value weather the comment will be shown
     */
    public String getShowComment() {
        return (String) get(SHOW_COMMENT);
    }

    /**
     * Set the value weather the comment should be shown. 
     * 
     * @param show The value weather the comment should be shown.
     */
    public void setShowComment(final String show) {
        set(SHOW_COMMENT, show);
    }
    
    /**
     * Returns the target Window of this <code>ExternalLink</code>
     *
     * @return The Target Window
     */
    public String getTargetWindow() {
        return (String) get(TARGET_WINDOW);
    }

    /**
     * Sets the target Window of this <code>ExternalLink</code>
     *
     * @param window The Target Window
     */
    public void setTargetWindow(String window) {
        set(TARGET_WINDOW, window);
    }
}