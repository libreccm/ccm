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

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *     Verifies that the
 *    parameter's value is not null.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon 
 * @version $Id: NotNullValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class NotNullValidationListener extends GlobalizedParameterListener {

    public static final NotNullValidationListener DEFAULT = new NotNullValidationListener();

    public NotNullValidationListener(String title) {
        setError(new GlobalizedMessage(title, getBundleBaseName()));
    }

    public NotNullValidationListener() {
        setError(new GlobalizedMessage(
                                       "parameter_is_required", getBundleBaseName()
                                       ));
    }

    public NotNullValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    public void validate (ParameterEvent e) {
        ParameterData data = e.getParameterData();
        Object value = data.getValue();

        if (value != null && value.toString().length() > 0) {
            return;
        }

        data.addError(getError());
    }
}
