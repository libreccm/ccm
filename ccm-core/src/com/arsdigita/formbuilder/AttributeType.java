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

import com.arsdigita.bebop.event.ParameterListener;

import com.arsdigita.util.UncheckedWrapperException;

import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class contains the attribute data type that are used for form
 * generation by the Form Builder.
 *
 * @author Peter Marklund
 * @version $Id: AttributeType.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class AttributeType {

    private static final Logger logger = Logger.getLogger(AttributeType.class);
    private Class m_parameterModelClass;
    private List m_validationListeners;

    public AttributeType(Class parameterModelClass) {

        m_parameterModelClass = parameterModelClass;

        m_validationListeners = new java.util.ArrayList();
    }
    /**
     * Standard attribute types
     */
    public static AttributeType INTEGER;
    public static AttributeType TEXT;
    public static AttributeType DATE;
    // The classes of the standard data types
    private static Class s_integerClass;
    private static Class s_textClass;
    private static Class s_dateClass;

    // Initialization of the standard attribute types
    static {
        logger.debug("Static initalizer starting...");
        try {

            s_integerClass = Class.forName(
                    "com.arsdigita.bebop.parameters.IntegerParameter");
            s_textClass = Class.forName(
                    "com.arsdigita.bebop.parameters.StringParameter");
            s_dateClass = Class.forName(
                    "com.arsdigita.bebop.parameters.DateParameter");

        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }

        INTEGER = new AttributeType(s_integerClass);
        TEXT = new AttributeType(s_textClass);
        DATE = new AttributeType(s_dateClass);
        logger.debug("Static initalizer finished.");
    }

    //*** Attribute Methods
    public Class getParameterModelClass() {
        return m_parameterModelClass;
    }

    /**
     * Note that a not null validation listener needs not be added here. If the
     * attribute is required a NotEmptyValidationListener will be added to
     * to the widget.
     */
    public void addValidationListener(ParameterListener validationListener) {
        m_validationListeners.add(validationListener);
    }

    public List getValidationListeners() {
        return m_validationListeners;
    }
}
