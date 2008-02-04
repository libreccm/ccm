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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;

import java.net.URL;
import java.net.MalformedURLException;

/**
 *  Verifies that the
 * parameter is a valid URL specification.  Does this by trying to
 * construct an actual URL object from the parameter and catching any
 * MalformedURLException thrown by the URL constructor.
 *
 * <p><b>Note</b>: Validates empty parameters so that URLs can be left
 * blank without generating a form error.  If you require a URL be
 * sure to add a NotEmptyValidationListener.  This class will also
 * validate a protocol-only URL like "http://".
 *
 * @see com.arsdigita.bebop.parameters.NotEmptyValidationListener
 * @see java.net.URL
 *
 * @version $Id: URLValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class URLValidationListener implements ParameterListener {

    public static final String versionId = "$Id: URLValidationListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * A label for the item being validated
     */

    private String m_label;

    /**
     * Constructor
     */

    public URLValidationListener() {
        this("This parameter");
    }

    /**
     * Constructor
     */

    public URLValidationListener(String label) {
        m_label = label;
    }


    /**
     * Validate the parameter using the URL constructor.
     */

    public void validate (ParameterEvent event) {

        ParameterData data = event.getParameterData();

        String value = data.getValue().toString();

        if (value != null && value.length() > 0) {
            try {
                new URL(value);
            } catch (MalformedURLException ex) {
                StringBuffer msg = new StringBuffer(128);
                msg.append(m_label);
                msg.append(" is not a valid URL specification: ");
                msg.append(ex.getMessage());
                msg.append(". A valid URL looks like \"http://mysite.net\".");
                data.addError(msg.toString());
            }
        }
    }
}
