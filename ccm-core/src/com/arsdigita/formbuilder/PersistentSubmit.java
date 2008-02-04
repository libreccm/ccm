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


// This class is an ACSObject using BigDecimals for its ids
import java.math.BigDecimal;

// This factory creates Submits
import com.arsdigita.bebop.form.Submit;

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
 * This class is responsible for persisting Bebop Submits. The Submit
 * is saved with the save() method. To resurrect the Submit, use the constructor
 * taking the id of the saved Submit and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentSubmit.java 317 2005-03-11 19:04:37Z mbooth $
 *
 */
public class PersistentSubmit extends PersistentWidget {

    public static final String versionId = "$Id: PersistentSubmit.java 317 2005-03-11 19:04:37Z mbooth $ by $Author: mbooth $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentSubmit.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    public PersistentSubmit() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Submit domain object that
     * can be saved to the database later on.
     */
    public PersistentSubmit(String typeName) {
        super(typeName);
    }

    public PersistentSubmit(ObjectType type) {
        super(type);
    }

    public PersistentSubmit(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Submit domain object
     * from the database.
     *
     * @param id The object id of the Submit domain object to retrieve
     */
    public PersistentSubmit(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentSubmit(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentSubmit create(String parameterName) {
        return PersistentSubmit.create(parameterName, "Submit");
    }

    public static PersistentSubmit create(String parameterName,
                                          String label) {
        PersistentSubmit s = new PersistentSubmit();
        s.setup(parameterName, label);
        return s;
    }

    protected void setup(String parameterName,
                         String label) {
        super.setup(parameterName);
        setButtonLabel(label);
    }

    /**
     * Create the Submit button whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Submit submit;

        if (get("defaultValue") == null) {

            // There is no label set - use the constructor without
            // label

            submit = new Submit(getParameterName());

        } else {

            // There is a label set - use the constructor taking name
            // and label

            submit = new Submit(getParameterName(), (String)getDefaultValue());
        }

        return submit;
    }

    //*** Attribute Methods
    // No attributes in addition to the Widget attributes
    // Note that setDefaultValue() is used instead of setButtonLabel()

    /**
     * Set the label of the submit button. Identical to setDefaultValue().
     */
    public void setButtonLabel(String label) {
        setDefaultValue(label);
    }

    /**
     * Get the label of the submit button. Identical to getDefaultValue().
     */
    public String getButtonLabel() {
        return (String)getDefaultValue();
    }

    public boolean hasLabel() {
        return false;
    }
}
