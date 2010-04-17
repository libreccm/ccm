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
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;

import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Option;

// All ACSObjects have a unique id
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;


/**
 * This class is responsible for persisting Bebop SingleSelects. The SingleSelect
 * is saved with the save() method. To resurrect the SingleSelect, use the constructor
 * taking the id of the saved SingleSelect and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentSingleSelect.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentSingleSelect extends PersistentSelect {

    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new SingleSelect domain object that
     * can be saved to the database later on.
     */
    public PersistentSingleSelect() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new SingleSelect domain object that
     * can be saved to the database later on.
     */
    public PersistentSingleSelect(String typeName) {
        super(typeName);
    }

    public PersistentSingleSelect(ObjectType type) {
        super(type);
    }

    public PersistentSingleSelect(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing SingleSelect domain object
     * from the database.
     *
     * @param id The object id of the SingleSelect domain object to retrieve
     */
    public PersistentSingleSelect(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    protected PersistentSingleSelect(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentSingleSelect create(String parameterName) {
        PersistentSingleSelect s = new PersistentSingleSelect();
        s.setup(parameterName);
        return s;
    }

    public boolean isMultiple() {
        return false;
    }

    protected void addDataToComponent(Component component) {
        OptionGroup optionGroup = (OptionGroup)component;
	optionGroup.addOption(new Option("", "-- Select --"));
	super.addDataToComponent(component);

    }
    /**
     * Create the SingleSelect whose persistence is managed
     * by this domain object.
     */
    protected OptionGroup createOptionGroup() {

        SingleSelect select = new SingleSelect(getParameterName());

        addDataToComponent(select);
        copyValuesToWidget(select);

        return select;
    }

}
