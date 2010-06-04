/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.globalization;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * <p>
 * Charset DomainObject implements a persistent Charset object. A charset, or
 * character set, is ...
 * </p>
 *
 * @author Yon Feldman
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 * @version $Id: Charset.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Charset extends DomainObject {

    public final static String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.globalization.Charset";

    /**
     * <p>
     * Constructor. Retrieves an instance for a new Charset
     * </p>
     *
     */
    public Charset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * <p>
     * Constructor. Retrieves a persistent Charset object from the database.
     * </p>
     *
     * @param oid Object ID of the Charset to retrieve.
     *
     * @exception DataObjectNotFoundException Thrown if we cannot retrieve a
     *            DataObject for the specified OID.
     */
    public Charset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * <p>
     * Constructor. Retrieves a persistent Charset object from the database.
     * </p>
     *
     * @param dataObject DataObject of the Charset to retrieve.
     *
     */
    public Charset(DataObject dataObject) {
        super(dataObject);
    }

    protected void initialize() {
        super.initialize();
        try {
            if (isNew() && getID() == null) {
                setID(Sequences.getNextValue());
            }
        } catch (java.sql.SQLException ex) {
            throw new UncheckedWrapperException("cannot set charset id", ex);
        }
    }

    /**
     * <p>
     * Returns the appropriate object type for a Charset so that the proper
     * type validation can take place when retrieving Charset by OID.
     * </p>
     *
     * @return String The fully qualified name of the base data object type
     *         for the Charset DataObject.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * <p>
     * Return ID.
     * </p>
     *
     * @return BigDecimal The ID of this DataObject
     */
    public BigDecimal getID() {
        return (BigDecimal) get("id");
    }

    /**
     * <p>
     * Set the ID
     * </p>
     *
     * @param id ID
     */
    private void setID(BigDecimal id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be empty.");
        }

        if (isNew()) {
            set("id", id);
        }
    }

    /**
     * <p>
     * Returns the charset.
     * </p>
     *
     * @return String charset
     */
    public String getCharset() {
        return (String) get("charset");
    }

    /**
     * <p>
     * Set the charset
     * </p>
     *
     * @param String charset
     */
    public void setCharset(String charset) {
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset cannot be empty.");
        }

        set("charset", charset);
    }

    protected void afterSave() {
        super.afterSave();
        Globalization.loadLocaleToCharsetMap();
    }

    protected void afterDelete() {
        super.afterDelete();
        Globalization.loadLocaleToCharsetMap();
    }
}
