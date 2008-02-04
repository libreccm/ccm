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
package com.arsdigita.util.parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * This takes in a path and makes sure that the resource exists either
 * as a File or an actual resource.  If it does, it returns the
 * InputStream for the given Resource.  If it does not, and if it is
 * required, it logs an error.  Otherwise, it returns null.
 *
 * @deprecated Use {@link URLParameter} instead.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ResourceParameter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ResourceParameter extends StringParameter {
    public final static String versionId =
        "$Id: ResourceParameter.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(ResourceParameter.class);

    public ResourceParameter(final String name) {
        super(name);
    }

    public ResourceParameter(final String name,
                         final int multiplicity,
                         final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected Object unmarshal(String value, final ErrorList errors) {
        File file = new File(value);

        if (!file.exists()) {
            // it is not a standard file so lets try to see if it
            // is a resource
            if (value.startsWith("/")) {
                value = value.substring(1);
            }

            ClassLoader cload = Thread.currentThread().getContextClassLoader();
            URL url = cload.getResource(value);
            InputStream stream = cload.getResourceAsStream(value);
            if (stream == null && isRequired()) {
                s_log.error(value + " is not a valid file and is required");

                final ParameterError error = new ParameterError
                    (this, "Resource not found");
                errors.add(error);
            }
            return stream;
        } else {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException ioe) {
                // we know the file exists so this should not
                // be an issue
                s_log.error(value + " is not a valid file and is required", ioe);

                errors.add(new ParameterError(this, ioe));

                return null;
            }
        }
    }
}
