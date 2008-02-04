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
package com.arsdigita.formbuilder;


// Objects of this class are ACSObjects that is identified by a unique number
import java.math.BigDecimal;

// This factory creates Passwords
import com.arsdigita.bebop.form.Password;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// Id class used by internal constructor
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Bebop Passwords. The Password
 * is saved with the save() method. To resurrect the Password, use the constructor
 * taking the id of the saved Password and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentPassword.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentPassword extends PersistentWidget {

    private static final Logger s_log =
        Logger.getLogger(PersistentPassword.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new Password domain object that
     * can be saved to the database later on.
     */
    public PersistentPassword() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Password domain object that
     * can be saved to the database later on.
     */
    public PersistentPassword(String typeName) {
        super(typeName);
    }

    public PersistentPassword(ObjectType type) {
        super(type);
    }

    public PersistentPassword(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Password domain object
     * from the database.
     *
     * @param id The object id of the Password domain object to retrieve
     */
    public PersistentPassword(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentPassword(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }


    public static PersistentPassword create(String parameterName) {
        PersistentPassword p = new PersistentPassword();
        p.setup(parameterName);
        return p;
    }

    /**
     * Create the Password whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Password password = new Password(getParameterName());

        copyValuesToWidget(password);

        if (getComponentAttribute("maxlength") != null) {
            password.setMaxLength(getMaxLength());
        }

        if (getComponentAttribute("size") != null) {
            password.setSize(getSize());
        }

        return password;
    }

    //*** Attribute Methods
    public void setMaxLength(int maxLength) {

        setComponentAttribute("maxlength", Integer.toString(maxLength));
    }

    /**
     * Defaults to 50 if no value has been set.
     */
    public int getMaxLength() {

        String maxLength = getComponentAttribute("maxlength");

        if (maxLength == null) {
            return 50;
        }

        return Integer.parseInt(maxLength);
    }

    public void setSize(int size) {

        setComponentAttribute("size", Integer.toString(size));
    }

    /**
     * Defaults to 12 if no value is set.
     */
    public int getSize() {

        String size = getComponentAttribute("size");

        if (size == null) {
            return 12;
        }

        return Integer.parseInt(size);
    }

    //*** Attribute metadata
    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList list = super.getAttributeMetaData();

        list.add(new AttributeMetaData("size", "The size of the Text field as shown on the form"));
        list.add(new AttributeMetaData("maxLength", "Maximum number of characters that user can input"));

        return list;
    }
}
