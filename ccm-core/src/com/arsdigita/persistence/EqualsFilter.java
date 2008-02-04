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
package com.arsdigita.persistence;

import java.util.Map;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.oql.Equals;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Literal;
import com.redhat.persistence.oql.Not;
import com.redhat.persistence.oql.Static;

class EqualsFilter extends FilterImpl {

    public final static String versionId = "$Id: EqualsFilter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final String m_attribute;
    private final String m_bindName;
    private final boolean m_not;

    static FilterImpl eq(String attribute, Object value) {
        return new EqualsFilter(attribute, value, false);
    }

    static FilterImpl notEq(String attribute, Object value) {
        return new EqualsFilter(attribute, value, true);
    }

    private EqualsFilter(String attribute, Object value, boolean not) {
        m_attribute = attribute;
        m_not = not;
        m_bindName = value == null ? null : FilterImpl.bindName(m_attribute);

        if (m_bindName != null) {
            set(m_bindName, value);
        }
    }

    private boolean isValueNull() {
        return m_bindName == null;
    }

    protected Expression makeExpression(DataQueryImpl query, Map bindings) {
        Path path = Path.get(m_attribute);
        path = query.unalias(path);

        Expression variable;
        if (query.hasProperty(path)) {
            path = query.mapAndAddPath(path);
            variable = Expression.valueOf(path);
        } else {
            // this handles cases like eq("lower(attribute)", value)
            String expr = query.unalias(m_attribute);
            expr = query.mapAndAddPaths(expr);
            variable = new Static(expr);
        }

        Expression value;
        if (isValueNull()) {
            value = new Literal(null);
        } else {
            value = new Literal(getBindings().get(m_bindName));
        }


        Expression expr = new Equals(variable, value);

        if (m_not) {
            expr = new Not(expr);
        }

        return expr;
    }

    public String getConditions() {
        String connector = m_not ? "!=" : "=";
        String conditions;
        if (isValueNull()) {
            conditions = FilterImpl.createNullString(connector, m_attribute);
        } else {
            conditions = m_attribute + connector + " :" + m_bindName;
        }

	return conditions;
    }

    public String toString() {
        return "Equals Filter: " + m_attribute + " = " + m_bindName +
            Utilities.LINE_BREAK + "  Values: " + getBindings();
    }

}
