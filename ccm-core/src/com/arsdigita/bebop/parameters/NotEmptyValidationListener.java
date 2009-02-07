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
package com.arsdigita.bebop.parameters;

import org.apache.commons.lang.StringUtils;

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *    Verifies that the parameter's value is non-empty.
 *    A value is considererd non-empty if it exists in the page state,
 *    and it contains some data besides whitespace.
 *   
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon 
 *    @author Bill Schneider 
 */
public class NotEmptyValidationListener extends GlobalizedParameterListener {

    public static final String versionId = 
            "$Id: NotEmptyValidationListener.java 1502 2007-03-20 11:38:53Z chrisgilbert23 $" +
            "by $Author: chrisgilbert23 $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    public NotEmptyValidationListener(String label) {
        setError(new GlobalizedMessage(label, getBundleBaseName()));
    }

    public NotEmptyValidationListener() {
        setError(new GlobalizedMessage(
                                       "parameter_is_required", getBundleBaseName()
                                       ));
    }

    public NotEmptyValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    public void validate (ParameterEvent e) {
        ParameterData data = e.getParameterData();
        Object value = data.getValue();

        if (value != null) {
        	// all these are possible:
        	// "&nbsp;"
        	// "    &nbsp;"
        	// "    &nbsp;     "
        	// need to validate for all these possibilities
        	//
        	// take out whitespace at the edges
        	String valueString = value.toString().trim();
        	// then take out &nbsp; at the edges
        	valueString = StringUtils.strip(valueString, Character.toString('\u00A0'));
        	valueString = StringUtils.strip(valueString, Character.toString('\u2007'));
        	if (valueString.length() > 0) {
            return;
        }

        }
        
        

        data.addError(getError());
    }
}
