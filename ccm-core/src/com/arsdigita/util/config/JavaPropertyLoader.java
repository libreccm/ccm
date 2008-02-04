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
package com.arsdigita.util.config;

import com.arsdigita.util.Assert;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterLoader;
import com.arsdigita.util.parameter.ParameterValue;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * @deprecated Use {@link com.arsdigita.util.JavaPropertyReader}
 * instead
 */
public class JavaPropertyLoader extends JavaPropertyReader
        implements ParameterLoader {
    public final static String versionId =
        "$Id: JavaPropertyLoader.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyLoader.class);

    private final Properties m_props;

    /**
     * Constructs a parameter loader that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyLoader(final Properties props) {
        super(props);

        m_props = props;
    }

    public final ParameterValue load(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading " + param + " from " + this);
        }

        Assert.exists(param, Parameter.class);

        final String key = param.getName();

        if (m_props.containsKey(key)) {
            final ParameterValue value = new ParameterValue();

            value.setObject(param.read(this, value.getErrors()));

            return value;
        } else {
            return null;
        }
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + properties.size()
     */
    public String toString() {
        return super.toString() + ":" + m_props.size();
    }
}
