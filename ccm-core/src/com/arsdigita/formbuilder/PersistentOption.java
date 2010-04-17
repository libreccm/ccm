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

// All ACSObjects have a unique id
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// The class we are making persistent
import com.arsdigita.bebop.form.Option;


/**
 * This class is responsible for persisting Bebop Options. The Option
 * is saved with the save() method. To resurrect the Option, use the constructor
 * taking the id of the saved Option and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentOption.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentOption extends PersistentComponent {

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Option";

    // *** Constructors -------------

    /**
     * Constructor that creates a new Option domain object that
     * can be saved to the database later on.
     */
    public PersistentOption() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PersistentOption(String typeName) {
        super(typeName);
    }

    public PersistentOption(ObjectType type) {
        super(type);
    }

    public PersistentOption(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Option domain object
     * from the database.
     *
     * @param id The object id of the Option domain object to retrieve
     */
    public PersistentOption(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentOption(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentOption create(String parameterValue,
                                          String label) {
        PersistentOption o = new PersistentOption();
        o.setup(parameterValue, label);
        return o;
    }


    protected void setup(String parameterValue,
                         String label) {
        setLabel(label);
        setParameterValue(parameterValue);
    }

    /**
     * Create the Label whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Option option = new Option(getParameterValue(), getLabel());

        return option;
    }

    //*** Attribute Methods

    public void setParameterValue(String parameterValue) {
        set("parameterValue", parameterValue);
    }

    public String getParameterValue() {
        return (String)get("parameterValue");
    }

    public void setLabel(String label) {
        set("label", label);
    }

    public String getLabel() {
        return (String)get("label");
    }
}
