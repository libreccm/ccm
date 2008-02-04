/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterReader;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * Static utility methods for handling Java system properties.
 *
 * @see java.lang.System
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: SystemProperties.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class SystemProperties {
    public final static String versionId =
        "$Id: SystemProperties.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (SystemProperties.class);

    private static final ParameterReader s_reader = new JavaPropertyReader
        (System.getProperties());

    /**
     * Uses <code>param</code> to decode, validate, and return the
     * value of a Java system property.
     *
     * @see com.arsdigita.util.parameter.Parameter
     * @param param The <code>Parameter</code> representing the type
     * and name of the field you wish to recover; it cannot be null
     * @return A value that may be cast to the type enforced by the
     * parameter; it can be null
     */
    public static final Object get(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the value of " + param + " from " +
                        "the system properties");
        }

        Assert.exists(param, Parameter.class);

        final ErrorList errors = new ErrorList();

        final Object value = param.read(s_reader, errors);

        errors.check();

        if (value == null) {
            final Object dephalt = param.getDefaultValue();

            param.validate(dephalt, errors);

            errors.check();

            return dephalt;
        } else {
            param.validate(value, errors);

            errors.check();

            return value;
        }
    }
}
