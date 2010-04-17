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


// The class that we are making persistent
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.OptionGroup;

// All ACSObjects have a unique id
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Every PersistentComponentFactory can create a Bebop Component

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;


/**
 * This class is responsible for persisting Bebop MultipleSelects. The MultipleSelect
 * is saved with the save() method. To resurrect the MultipleSelect, use the constructor
 * taking the id of the saved MultipleSelect and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentMultipleSelect.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public class PersistentMultipleSelect extends PersistentSelect {


    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new MultipleSelect domain object that
     * can be saved to the database later on.
     */
    public PersistentMultipleSelect() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new MultipleSelect domain object that
     * can be saved to the database later on.
     */
    public PersistentMultipleSelect(String typeName) {
        super(typeName);
    }

    public PersistentMultipleSelect(ObjectType type) {
        super(type);
    }

    public PersistentMultipleSelect(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing MultipleSelect domain object
     * from the database.
     *
     * @param id The object id of the MultipleSelect domain object to retrieve
     */
    public PersistentMultipleSelect(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentMultipleSelect(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentMultipleSelect create(String parameterName) {
        PersistentMultipleSelect s = new PersistentMultipleSelect();
        s.setup(parameterName);
        return s;
    }

    public boolean isMultiple() {
        return true;
    }

    /**
     * Create the MultipleSelect whose persistence is managed
     * by this domain object.
     */
    protected OptionGroup createOptionGroup() {

        MultipleSelect select = new MultipleSelect(getParameterName());

        addDataToComponent(select);
        copyValuesToWidget(select);

        return select;
    }

}
