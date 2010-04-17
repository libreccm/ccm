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
package com.arsdigita.formbuilder.parameters;


// We create instances of this listener
import com.arsdigita.bebop.event.ParameterListener;

// The class that we are persisting
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// For the id of the persistent parameter listener
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;

import org.apache.log4j.Logger;


/**
 * Validates that a string has a particular minimum and maximum
 * length.
 *
 * @author Peter Marklund
 * @version $Id: PersistentStringInRangeValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public abstract class PersistentStringInRangeValidationListener
    extends PersistentParameterListener {

    private static final Logger s_log =
        Logger.getLogger(PersistentStringInRangeValidationListener.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Listener";

    /**
     * Constructor for creating a new listener that can be persisted
     */
    public PersistentStringInRangeValidationListener(int minLength, int maxLength) {

        super(PersistentStringInRangeValidationListener.class.getName());

        set("defaultDomainClass", this.getClass().getName());

        setMinLength(minLength);
        setMaxLength(maxLength);
    }

    /**
     * Constructor for retrieving a persistent parameter listener
     * from the database
     */
    public PersistentStringInRangeValidationListener(BigDecimal id)
        throws DataObjectNotFoundException {

        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PersistentStringInRangeValidationListener(DataObject obj) {
        super(obj);
    }

    /**
     * Factory method that creates the Bebop component whose
     * persistence is handled by this domain object.
     */
    public ParameterListener createListener() {

        return new StringInRangeValidationListener(getMinLength(),
                                                   getMaxLength());
    }

    // *** Attribute Methods

    public void setMinLength(int minLength) {
        setListenerAttribute("minLength", Integer.toString(minLength));
    }

    public int getMinLength() {
        return Integer.parseInt(getListenerAttribute("minLength"));
    }

    public void setMaxLength(int maxLength) {
        setListenerAttribute("maxLength", Integer.toString(maxLength));
    }

    public int getMaxLength() {
        return Integer.parseInt(getListenerAttribute("maxLength"));
    }
}
