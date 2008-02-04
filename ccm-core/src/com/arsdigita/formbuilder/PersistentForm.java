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

// This domain object can create a Bebop form
import com.arsdigita.bebop.Form;


// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// All PersistentComponentFactories can create a Bebop Component
import com.arsdigita.bebop.Component;


// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This domain object manages persistence of a Bebop Form. Properties
 * and components of a Bebop Form can be set (typically with an admin interface),
 * the form should then be saved so that it can be resurrected later
 * on in the application. Of particular importance to application
 * developers are the methods in the PersistentComponentFactory and
 * PersistentContainerFactory interfaces.
 *
 * @author Peter Marklund
 * @version $Id: PersistentForm.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public class PersistentForm extends PersistentFormSection {

    public static final String versionId = "$Id: PersistentForm.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentForm.class.getName());

    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.FormSection";

    // *** Constructors -------------

    /**
     * Default Constructor. Should normally not be used be application
     * developers. Is only here to enable instantiation by FormSectionGenerator
     */
    public PersistentForm() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new form domain object that
     * can be saved to the database later on.
     */
    public PersistentForm(String typeName) {
        super(typeName);
    }

    public PersistentForm(ObjectType type) {
        super(type);
    }

    public PersistentForm(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing form domain object
     * from the database.
     *
     * @param id The object id of the form domain object to retrieve
     */
    public PersistentForm(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PersistentForm(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public static PersistentForm create(String htmlName) {
        PersistentForm f = new PersistentForm();
        f.setup(htmlName);
        return f;
    }

    protected void setup(String htmlName) {
        setHTMLName(htmlName);
    }

    /**
     * Create a Bebop form using the persistent information in this form domain object.
     *
     */
    public Component createComponent() {

        // Create the form with the name attribute
        Form form;
        if (getFormContainer() != null) {
            form = new Form(getHTMLName(), getFormContainer());
        } else {
            form = new Form(getHTMLName());
        }

        // Set the action attribute
        form.setAction(getAction());

        // Add the process listener
        addProcessListeners(form);

        // Add the components
        addComponents(form);

        return form;
    }

    // *** Attribute Methods

    public void setHTMLName(String name) {
        setComponentAttribute("name", name);
    }

    public String getHTMLName() {
        return getComponentAttribute("name");
    }

    public void setAction(String action) {
        set("action", action);
    }

    public String getAction() {
        return (String)get("action");
    }

}
