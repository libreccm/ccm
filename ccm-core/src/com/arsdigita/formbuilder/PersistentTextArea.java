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

// This factory creates a TextArea
import com.arsdigita.bebop.form.TextArea;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;

// Id class used by internal constructor
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// We define a process listener for adding a text area
//import com.arsdigita.bebop.event.FormProcessListener;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Bebop TextAreas. The TextArea
 * is saved with the save() method. To resurrect the TextArea, use the constructor
 * taking the id of the saved TextArea and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentTextArea.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentTextArea extends PersistentWidget {

    public static final String versionId = "$Id: PersistentTextArea.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentTextArea.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new TextArea domain object that
     * can be saved to the database later on. This class was only
     * included to make it possible to use this DomainObject with the
     * FormGenerator (to make the class JavaBean compliant). Use the constructor
     * taking a parameter name instead if possible.
     */
    public PersistentTextArea() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new TextArea domain object that
     * can be saved to the database later on.
     */
    public PersistentTextArea(String typeName) {
        super(typeName);
    }

    public PersistentTextArea(ObjectType type) {
        super(type);
    }

    public PersistentTextArea(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing TextArea domain object
     * from the database.
     *
     * @param id The object id of the TextArea domain object to retrieve
     */
    public PersistentTextArea(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentTextArea(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentTextArea create(String parameterName) {
        PersistentTextArea t = new PersistentTextArea();
        t.setup(parameterName);
        return t;
    }

    /**
     * Create the TextArea whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        TextArea textArea = new TextArea(getParameterName());

        copyValuesToWidget(textArea);

        if (getComponentAttribute("cols") != null) {
            textArea.setCols(getCols());
        }

        if (getComponentAttribute("rows") != null) {
            textArea.setRows(getRows());
        }

        return textArea;
    }

    //*** Attribute Methods

    public void setCols(int cols) {
        setComponentAttribute("cols", Integer.toString(cols));
    }

    /**
     * Defaults to 30 if no value has been set.
     */
    public int getCols() {

        String cols = getComponentAttribute("cols");

        if (cols == null) {
            return 30;
        }

        return Integer.parseInt(cols);
    }

    public void setRows(int rows) {

        setComponentAttribute("rows", Integer.toString(rows));
    }

    /**
     * Defaults to 10 if no value has been set.
     */
    public int getRows() {

        String rows = getComponentAttribute("rows");

        if (rows == null) {
            return 10;
        }

        return Integer.parseInt(rows);
    }

    //*** Attribute metadata
    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList list = super.getAttributeMetaData();

        list.add(new AttributeMetaData("cols", "Number of columns"));
        list.add(new AttributeMetaData("rows", "Number of rows"));

        return list;
    }
}
