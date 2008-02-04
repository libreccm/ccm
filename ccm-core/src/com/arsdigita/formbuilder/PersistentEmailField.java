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

import java.math.BigDecimal;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

import org.apache.log4j.Logger;

/**
 * This class is responsible for persisting Bebop TextFields. The TextField
 * is saved with the save() method. To resurrect the TextField, use the constructor
 * taking the id of the saved TextField and then invoke createComponent().
 *
 *  @author Shubham nagar 
 *  @company Infoaxon Technology
 *  @date 13-12-2004
 *
 */
public class PersistentEmailField extends PersistentWidget {

    public static final String versionId = "$Id: PersistentEmailField.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentEmailField.class.getName());

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
    public PersistentEmailField() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new TextField domain object that
     * can be saved to the database later on.
     */
    public PersistentEmailField(String typeName) {
        super(typeName);
    }

    public PersistentEmailField(ObjectType type) {
        super(type);
    }

    public PersistentEmailField(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing TextField domain object
     * from the database.
     *
     * @param id The object id of the TextField domain object to retrieve
     */
    public PersistentEmailField(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentEmailField(OID oID) {
        super(oID);
    }

    public static PersistentEmailField create(String parameterName) {
        PersistentEmailField t = new PersistentEmailField();
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
