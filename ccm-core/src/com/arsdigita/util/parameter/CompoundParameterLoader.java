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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @deprecated Use {@link
 * com.arsdigita.util.parameter.CompoundParameterReader} instead.
 **/

public class CompoundParameterLoader implements ParameterLoader {

    public final static String versionId = "$Id: CompoundParameterLoader.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final List m_loaders;

    public CompoundParameterLoader() {
        m_loaders = new ArrayList();
    }

    public void add(ParameterLoader loader) {
        m_loaders.add(loader);
    }

    public String read(final Parameter param, final ErrorList errors) {
        for (final Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            final ParameterReader reader = (ParameterReader) it.next();

            final String result = reader.read(param, errors);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public ParameterValue load(Parameter param) {
        for (Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            ParameterLoader loader = (ParameterLoader) it.next();
            ParameterValue value = loader.load(param);
            if (value != null) { return value; }
        }

        return null;
    }

}
