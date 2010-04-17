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

// This factory creates TextFields
import com.arsdigita.bebop.form.TextField;

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
 * This class is responsible for persisting Bebop TextFields. The TextField
 * is saved with the save() method. To resurrect the TextField, use the constructor
 * taking the id of the saved TextField and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentTextField.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentTextField extends PersistentWidget {

    private static final Logger s_log =
        Logger.getLogger(PersistentTextField.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new TextField domain object that
     * can be saved to the database later on. This class was only
     * included to make it possible to use this DomainObject with the
     * FormGenerator (to make the class JavaBean compliant). Use the constructor
     * taking a parameter name instead if possible.
     */
    public PersistentTextField() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new TextField domain object that
     * can be saved to the database later on.
     */
    public PersistentTextField(String typeName) {
        super(typeName);
    }

    public PersistentTextField(ObjectType type) {
        super(type);
    }

    public PersistentTextField(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing TextField domain object
     * from the database.
     *
     * @param id The object id of the TextField domain object to retrieve
     */
    public PersistentTextField(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentTextField(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentTextField create(String parameterName) {
        PersistentTextField t = new PersistentTextField();
        t.setup(parameterName);
        return t;
    }

    /**
     * Create the TextField whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        TextField textField = new TextField(getParameterName());

        copyValuesToWidget(textField);

        if (getComponentAttribute("maxlength") != null) {
            textField.setMaxLength(getMaxLength());
        }

        if (getComponentAttribute("size") != null) {
            textField.setSize(getSize());
        }

        return textField;
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
