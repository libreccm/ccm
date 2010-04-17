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


// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// Used in constructor to create data object in base class
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// ACS objects are identified by a number
import java.math.BigDecimal;

// The factory creates Bebop Components
import com.arsdigita.bebop.Label;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Bebop Labels. The Label
 * is saved with the save() method. To resurrect the Label, use the constructor
 * taking the id of the saved Label and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentLabel.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentLabel extends PersistentComponent {

    private static final Logger s_log =
        Logger.getLogger(PersistentLabel.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Component";

    // *** Constructors -------------

    public PersistentLabel() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Label domain object
     * that can be persisted.
     */
    public PersistentLabel(String typeName) {
        super(typeName);
    }

    public PersistentLabel(ObjectType type) {
        super(type);
    }

    public PersistentLabel(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Label domain object
     * from the database.
     *
     * @param id The object id of the Label domain object to retrieve
     */
    public PersistentLabel(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * To be used by extending classes when retrieving instance from the database.
     */
    public PersistentLabel(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentLabel create(String label) {
        PersistentLabel l = new PersistentLabel();
        l.setup(label);
        return l;
    }

    protected void setup(String label) {
        setComponentAttribute("label", label);
    }

    /**
     * Create the Label whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Label label = new Label(getComponentAttribute("label"));

        return label;
    }

    //*** Attribute Methods
    public void setLabel(String label) {
        setComponentAttribute("label", label);
    }

    public String getLabel() {
        return getComponentAttribute("label");
    }
}
