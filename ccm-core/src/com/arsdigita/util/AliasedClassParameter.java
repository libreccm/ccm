/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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


import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.ErrorList;
import java.util.Map;
import java.util.HashMap;

public class AliasedClassParameter extends ClassParameter {

    private Map m_aliases;
    
    public AliasedClassParameter(final String name,
                                 final int multiplicity,
                                 final Object defaalt) {
        super(name, multiplicity, defaalt);
        m_aliases = new HashMap();
    }

    public void addAlias(String alias,
                         String className) {
        m_aliases.put(alias, className);
    }

    protected Object unmarshal(String value, ErrorList errors) {
        if (m_aliases.containsKey(value)) {
            value = (String)m_aliases.get(value);
        }

        return super.unmarshal(value, errors);
    }
}
