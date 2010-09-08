/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p>
 * This class defines an content type which represents a publication with a
 * publisher. The content type has three additional properties:
 * </p>
 * <ul>
 * <li>ISBN</li>
 * <li>URL</li>
 * <li>Publisher</li>
 * </ul>
 * <p>
 * For more details please refer to the documentation of the getter methods for
 * these attributes.
 * </p>
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisher extends Publication {

    public final static String ISBN = "isbn";
    public final static String PUBLISHER = "publisher";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicationWithPublisher";
    private static final Logger s_log = Logger.getLogger(
            PublicationWithPublisher.class);

    public PublicationWithPublisher() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicationWithPublisher(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationWithPublisher(OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationWithPublisher(DataObject obj) {
        super(obj);
    }

    public PublicationWithPublisher(String type) {
        super(type);
    }

    /**
     * Returns the ISBN of the publication.
     *
     * @return The ISBN of the publication.
     */
    public String getISBN() {
        return (String) get(ISBN);
    }

    /**
     * Sets the ISBN. Attention: This method does not check if the ISBN is 
     * valid yet!
     *
     * @param isbn New ISBN
     */
    public void setISBN(String isbn) {
        set(ISBN, isbn);
    }

    /**
     * Retrieves the publisher of the publication.
     *
     * @return The publisher of the publication.
     */
    public Publisher getPublisher() {
        DataObject dataObj;

        dataObj = (DataObject) get(PUBLISHER);

        if (dataObj == null) {
            return null;
        } else {
            return new Publisher(dataObj);
        }
    }

    /**
     * Links a publisher to the publication.
     *
     * @param publisher The publisher of the publication.
     */
    public void setPublisher(Publisher publisher) {
        set(PUBLISHER, publisher);
    }
}
