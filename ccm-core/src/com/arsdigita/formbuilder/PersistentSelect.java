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
import com.arsdigita.bebop.form.Select;

// All ACSObjects have a unique id
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;


/**
 * This class is responsible for persisting Bebop Selects. The Select
 * is saved with the save() method. To resurrect the Select, use the constructor
 * taking the id of the saved Select and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentSelect.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public abstract class PersistentSelect extends PersistentOptionGroup {

    public static final String versionId = "$Id: PersistentSelect.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";


    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new Select domain object that
     * can be saved to the database later on.
     */
    public PersistentSelect(String objectType) {

        super(objectType);
    }


    public PersistentSelect(ObjectType type) {
        super(type);
    }

    public PersistentSelect(DataObject obj) {
        super(obj);
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentSelect(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    protected void addDataToComponent(Component component) {

        super.addDataToComponent(component);

        Select select = (Select)component;

        if (getComponentAttribute("size") != null) {
            select.setSize(getSize());
        }
    }

    // *** Attribute Methods

    /**
     * The HTML Size attribute of this widget
     */
    public void setSize(int size) {

        setComponentAttribute("size", Integer.toString(size));
    }

    /**
     * The HTML Size attribute of this widget
     */
    public int getSize() {

        return Integer.parseInt(getComponentAttribute("size"));
    }
}
