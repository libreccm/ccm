/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.form.Widget;

/**
 * FormValidationException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: FormValidationException.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class FormValidationException extends FormProcessException {

    private String m_name = null;

    public FormValidationException(String message) {
        super(message);
    }

    public FormValidationException(String name, String message) {
        super(message);
        m_name = name;
    }

    public FormValidationException(ParameterModel parameter, String message) {
        this(parameter.getName(), message);
    }

    public FormValidationException(Widget widget, String message) {
        this(widget.getParameterModel(), message);
    }

    public FormValidationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public FormValidationException(String name, String message,
                                   Throwable rootCause) {
        super(message, rootCause);
        m_name = name;
    }

    public FormValidationException(ParameterModel parameter, String message,
                                   Throwable rootCause) {
        this(parameter.getName(), message, rootCause);
    }

    public FormValidationException(Widget widget, String message,
                                   Throwable rootCause) {
        this(widget.getParameterModel(), message, rootCause);
    }

    public String getName() {
        return m_name;
    }

}
