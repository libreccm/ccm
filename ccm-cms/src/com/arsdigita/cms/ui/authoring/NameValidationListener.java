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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import org.apache.log4j.Logger;

import java.util.StringTokenizer;

/**
 * Verifies that the parameter is a valid filename, is not null, and
 * contains no reserved characters.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: NameValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class NameValidationListener implements ParameterListener {
    public static final String versionId = 
        "$Id: NameValidationListener.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (NameValidationListener.class);

    // Why is this protected? XXX
    protected String label;

    // XXX this stuff needs globalization

    /**
     * Constructs a new <code>NameValidationListener</code>.
     *
     * @param label the label for the error message
     */
    public NameValidationListener(final String label) {
        this.label = label;
    }

    /**
     * Constructs a new <code>NameValidationListener</code>.
     */
    public NameValidationListener() {
        this("This parameter");
    }

    public void validate(final ParameterEvent e) {
        final ParameterData data = e.getParameterData();
        final Object value = data.getValue();

        if (value == null || value.toString().length() < 1) {
            data.addError(label + " may not be null");
            return;
        }

        final String text = value.toString();

        final StringTokenizer tok =
            new StringTokenizer(text, "*? /\\\'\"&$`~");

        if (tok.hasMoreTokens()) {
            final String token = tok.nextToken();

            if (!token.equals(text)) {
                data.addError(label + " should be a valid filename");
            }
        }

        if (text.indexOf(".") != -1) {
            data.addError(label + " may not contain periods");
        }
    }
}
