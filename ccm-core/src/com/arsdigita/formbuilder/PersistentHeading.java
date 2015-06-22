/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

// Every PersistentComponentFactory can create a Bebop Component

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;

// ACS 5 uses Log4J for logging

import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Headings that can be used
 * as section headings to break the form in to multiple logical groupings.
 * The Heading is saved with the save() method. To resurrect the Heading, 
 * use the constructor taking the id of the saved Heading and then invoke 
 * createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentHeading.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public class PersistentHeading extends PersistentText {

    private static final Logger s_log =
        Logger.getLogger(PersistentHeading.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Component";

    // *** Constructors -------------

    public PersistentHeading() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Heading domain object
     * that can be persisted.
     */
    public PersistentHeading(String typeName) {
        super(typeName);
    }

    public PersistentHeading(ObjectType type) {
        super(type);
    }

    public PersistentHeading(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Heading domain object
     * from the database.
     *
     * @param id The object id of the Heading domain object to retrieve
     */
    public PersistentHeading(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * To be used by extending classes when retrieving instance from the database.
     */
    public PersistentHeading(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentText create(String headingString) {
        PersistentHeading heading = new PersistentHeading();
        heading.setup(headingString);
        return heading;
    }

    protected void setup(String heading) {
        setHeading(heading);
    }

    /**
     * Create the Heading whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {
        Component component = super.createComponent();
        component.setClassAttr("persistenceHeading");
        ((Label) component).setOutputEscaping(false);
        return component;
    }

    public void setHeading(String heading) {
        setText(heading);
    }

    public String getHeading() {
        return getText();
    }
}
