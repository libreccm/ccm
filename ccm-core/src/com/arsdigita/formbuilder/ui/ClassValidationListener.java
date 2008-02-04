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
package com.arsdigita.formbuilder.ui;


import com.arsdigita.bebop.parameters.ParameterData;

import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;


/**
 * Validates that the parameter submitted is the fully qualified
 * class name of a class that can be loaded. Can also be supplied with a
 * class object and will then additionally check that the submitted class is assignable from the
 * class we are verifying against (see Class.isAssignableFrom(Class), this is useful
 * for checking that the submitted class implements a certain interface or extends a certain
 * class). This validation listener requires that invoking toString() on the value object
 * submitted will yield the fully qualified class name (you may for example use a StringParameter
 * which is the default of most widgets).
 *
 * @author Peter Marklund
 * @version $Id: ClassValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ClassValidationListener
    implements ParameterListener {

    public static final String versionId = "$Id: ClassValidationListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Class m_assignableClass = null;

    /**
     * With this constructor it will only be checked that the submitted class can be
     * loaded.
     */
    public ClassValidationListener() {
        // Intentionally empty
    }

    /**
     * Use this constructor to check that the submitted class can be loaded and that it is
     * assignable from the class that is supplied to this constructor. For example, if you wanted
     * to check that the user-supplied class name belongs to a class that is serializable you
     * would supply the java.io.Serializable class to this constructor.
     */
    public ClassValidationListener(Class assignableClass) {

        m_assignableClass = assignableClass;
    }

    public void validate(ParameterEvent parameterEvent) {
        ParameterData parameterData = parameterEvent.getParameterData();
        Object parameterValue = parameterData.getValue();

        String className = parameterValue.toString();

        Class parameterClass;

        try {
            // Load the class
            parameterClass = Class.forName(className);

        } catch (Exception e) {
            parameterData.addError("The name of the supplied class could not be loaded. Check the name " +
                                   "and make sure you are prefixing the class name with the right package name " +
                                   "(that the class name is fully qualified)");

            // If the class could not be loaded we cannot check assignability
            return;
        }


        if (m_assignableClass != null) {

            if (!m_assignableClass.isAssignableFrom(parameterClass)) {
                parameterData.addError("The supplied class name is not assignable from the class or interface " +
                                       m_assignableClass.getName());
            }
        }
    }
}
