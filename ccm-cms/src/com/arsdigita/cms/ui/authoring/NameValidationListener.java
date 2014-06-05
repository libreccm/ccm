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
import com.arsdigita.cms.util.GlobalizationUtil;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Verifies that the parameter is a valid filenamersp. URL stub, is not null, 
 * and contains no reserved characters.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pb@zes.uni-bremen.de&gt;
 * @version $Id: NameValidationListener.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class NameValidationListener implements ParameterListener {


    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.cms.ui.authoring.NameValidationListener=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger
                                               (NameValidationListener.class);

    /**
     * Default Constructor, creates a new <code>NameValidationListener</code>.
     */
    public NameValidationListener() {

    }

    /**
     * Constructs a new <code>NameValidationListener</code>.
     *
     * @param label the label for the error message
     * @deprecated with no replacement. Does nothing anymore.
     */
    public NameValidationListener(final String label) {
        // Do nothing
    }

    /**
     * Validate the input field as passed in by ParameterEvent.
     * 
     * @param e ParameterEvent containing input data. 
     */
    @Override
    public void validate(final ParameterEvent e) {
        final ParameterData data = e.getParameterData();
        final Object value = data.getValue();

        if (value == null || value.toString().length() < 1) {
            data.addError(GlobalizationUtil
                          .globalize("cms.ui.authoring.parameter_not_empty"));
            return;
        }

        final String text = value.toString();

        final StringTokenizer tok =
            new StringTokenizer(text, "*? /\\\'\"&$`~");

        if (tok.hasMoreTokens()) {
            final String token = tok.nextToken();

            if (!token.equals(text)) {
                data.addError(GlobalizationUtil.globalize(
                     "cms.ui.authoring.parameter_should_be_a_valid_filename"));
            }
        }

        if (text.indexOf(".") != -1) {
            data.addError(GlobalizationUtil.globalize(
                 "cms.ui.authoring.parameter_should_be_a_valid_filename"));
        }
    }
}
