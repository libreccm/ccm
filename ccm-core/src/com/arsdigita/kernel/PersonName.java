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
package com.arsdigita.kernel;

import com.arsdigita.domain.DomainObject;

// Support for persistent objects
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;

// Access to a unique ID generating sequence.
import java.math.BigDecimal;
import java.sql.SQLException;
import com.arsdigita.db.Sequences;

// Support for Logging.
import org.apache.log4j.Logger;

/**
 * Represents the name of a person.  PersonName objects
 * are intended to be components of other objects.
 *
 * @author Oumi Mehrotra
 * @author Phong Nguyen
 * @version $Id: PersonName.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class PersonName extends DomainObject {

    public static final String versionId = "$Id: PersonName.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final String m_baseDataObjectType =
        "com.arsdigita.kernel.PersonName";

    private static final Logger s_cat =
        Logger.getLogger(PersonName.class.getName());

    protected String getBaseDataObjectType() {
        return m_baseDataObjectType;
    }

    public PersonName(DataObject nameData) {
        super(nameData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "PersonName".
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public PersonName() {
        super(m_baseDataObjectType);
    }

    protected void initialize() {
        super.initialize();
        if (isNew()) {
            setID();
        }
    }

    /**
     * Returns the given name.
     *
     * @return the given name.
     **/
    public String getGivenName() {
        return (String) get("givenName");
    }

    /**
     * Sets the given name.
     *
     * @param givenName the value to set for the given name
     **/
    public void setGivenName(String givenName) {
        set("givenName", givenName);
    }

    /**
     * Returns the family name.
     *
     * @return the family name.
     **/
    public String getFamilyName() {
        return (String) get("familyName");
    }

    /**
     * Sets the family name.
     *
     * @param familyName the value to set for the family name
     **/
    public void setFamilyName(String familyName) {
        set("familyName", familyName);
    }

    /**
     * Gets the value of the "id" property.
     *
     * @return value of the "id" property.
     */
    BigDecimal getID() {
        return (BigDecimal) get("id");
    }

    /**
     * Sets the value of the "id" property to
     * <code>com.arsdigita.db.Sequences.getNextValue()</code>
     * only if the "id" property is not already set.
     * Returns the value that the "id" is set to after execution.
     *
     * @param id value to try to set the "id" property to.
     * @return value that the "id" property is set to after execution.
     *
     * @exception PersistenceException thrown when a unique id could not
     * be generated.
     */
    private BigDecimal setID() throws PersistenceException {
        if (!isNew()) {
            return getID();
        }
        BigDecimal currentID = getID();
        if (currentID == null) {
            currentID = generateID();
            set("id", currentID);
        }
        return currentID;
    }

    /**
     * Returns a displayable string for this person name.
     *
     * @return the displayable string for this person name
     */
    public String toString() {
        return getGivenName() + " " + getFamilyName();
    }

    private BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "PersonName id.";
            s_cat.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }
}
