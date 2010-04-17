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

// This factory creates a Date
import com.arsdigita.bebop.form.Date;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.Widget;

// Id class used by internal constructor
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// For instantiating the DateParameter
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.formbuilder.util.FormBuilderUtil;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Bebop Dates. The Date
 * is saved with the save() method. To resurrect the Date, use the constructor
 * taking the id of the saved Date and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentDate.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentDate extends PersistentWidget {

    private static final Logger s_log =
        Logger.getLogger(PersistentDate.class.getName());

    private Class VALUE_CLASS = new java.util.Date().getClass();

    private static final String END_YEAR = "endYear";
    private static final String START_YEAR = "startYear";

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new Date domain object that
     * can be saved to the database later on. This class was only
     * included to make it possible to use this DomainObject with the
     * FormGenerator (to make the class JavaBean compliant). Use the constructor
     * taking a parameter name instead if possible.
     */
    public PersistentDate() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Date domain object that
     * can be saved to the database later on.
     */
    public PersistentDate(String typeName) {
        super(typeName);
    }

    public PersistentDate(ObjectType type) {
        super(type);
    }

    public PersistentDate(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Date domain object
     * from the database.
     *
     * @param id The object id of the Date domain object to retrieve
     */
    public PersistentDate(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentDate(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentDate create(String parameterName) {
        PersistentDate d = new PersistentDate();
        d.setup(parameterName);
        return d;
    }

    /**
     * Create the Date whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Date date = null;

        // If there is a speical DateParameterClass - instantiate that
        if (getDateParameter() != null) {

            DateParameter dateParameter = (DateParameter)
                FormBuilderUtil.instantiateObject(getDateParameter(),
                                                  new Class [] {getParameterName().getClass()},
                                                  new Object [] {getParameterName()});

            date = new Date(dateParameter);

        } else {
            date = new Date(getParameterName());
        }

        copyValuesToWidget(date);

        return date;
    }

    protected void copyValuesToWidget(Widget widget) {
        super.copyValuesToWidget(widget);

        Date date = (Date)widget;

        // Set year range if any has been specified
        Integer startYear = getStartYear();
        Integer endYear = getEndYear();

        if (startYear != null && endYear != null) {
            date.setYearRange(startYear.intValue(), endYear.intValue());
        }
    }

    /**
     *  This returns the end year to display or null if there is no end year
     */
    public Integer getEndYear() {
        String endYear = getComponentAttribute(END_YEAR);
        if (endYear != null && !endYear.equals("")) {
            return new Integer(endYear);
        } 
        return null;
    }

    /**
     *  This returns the start year to display or null if there is no start year
     */
    public Integer getStartYear() {
        String startYear = getComponentAttribute(START_YEAR);
        if (startYear != null && !startYear.equals("")) {
            return new Integer(startYear);
        } 
        return null;
    }

    /**
     * Returns a java.util.Date Class
     */
    protected Class getValueClass() {

        return VALUE_CLASS;
    }

    //*** Attribute Methods
    public void setDateParameter(String dateParameterClass) {

        setComponentAttribute("dateParameter", dateParameterClass);
    }

    /**
     * Will return null if no value has been set.
     */
    public String getDateParameter() {
        return getComponentAttribute("dateParameter");
    }

    public void setYearRange(int startYear, int endYear) {

        setComponentAttribute(START_YEAR, Integer.toString(startYear));
        setComponentAttribute(END_YEAR, Integer.toString(endYear));

    }
}
