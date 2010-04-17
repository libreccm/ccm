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

import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * <p>
 * Abstract class to be extended by globalized parameters.
 * </p>
 *
 * @version $Id: GlobalizedParameterListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class GlobalizedParameterListener
    implements Globalized, ParameterListener {

    private final static String BUNDLE_NAME =
        "com.arsdigita.bebop.parameters.ParameterResources";

    private GlobalizedMessage m_error = null;

    /**
     * <p>
     * Return the base name of the target ResourceBundle.
     * </p>
     *
     * @return String target ResourceBundle base name.
     */
    public String getBundleBaseName() {
        return BUNDLE_NAME;
    }

    /**
     * <p>
     * Get the error message for this parameter.
     * </p>
     *
     * @return GlobalizedMessage The error.
     */
    protected GlobalizedMessage getError() {
        return m_error;
    }

    /**
     * <p>
     * Set the error message for this parameter.
     * </p>
     *
     * @param error The error message to use for this parameter.
     */
    protected void setError(GlobalizedMessage error) {
        m_error = error;
    }
}
