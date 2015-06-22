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
import com.arsdigita.bebop.Label;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Texts that can be used
 * as section headings to break the form in to multiple logical groupings.
 * The Text is saved with the save() method. To resurrect the Text, 
 * use the constructor taking the id of the saved Text and then invoke 
 * createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentText.java 317 2005-03-11 19:04:37Z mbooth $
 *
 */
public class PersistentText extends PersistentComponent {

    private static final Logger s_log =
        Logger.getLogger(PersistentText.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Component";

    // *** Constructors -------------

    public PersistentText() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Text domain object
     * that can be persisted.
     */
    public PersistentText(String typeName) {
        super(typeName);
    }

    public PersistentText(ObjectType type) {
        super(type);
    }

    public PersistentText(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Text domain object
     * from the database.
     *
     * @param id The object id of the Text domain object to retrieve
     */
    public PersistentText(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * To be used by extending classes when retrieving instance from the database.
     */
    public PersistentText(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentText create(String textString) {
        PersistentText text = new PersistentText();
        text.setup(textString);
        return text;
    }

    protected void setup(String text) {
        setText(text);
    }

    /**
     * Create the Text whose persistence is managed
     * by this domain object.
     */
    @Override
    public Component createComponent() {
        Label label = new Label(getText());
        label.setClassAttr("persistenceText");
        label.setOutputEscaping(false);
        return label;
    }

    // Currently, we just store the text as part of the description
    // since it does not make sense to have a description for a text.
    // We don't use the "attributes" method because we would loose several
    // characters of storing because the attributes are stored like
    // 'text="my text here"' in the same size column as the description.
    public void setText(String label) {
        setDescription(label);
    }

    public String getText() {
        return getDescription();
    }

    public boolean hasLabel() {
        return false;
    }
}
