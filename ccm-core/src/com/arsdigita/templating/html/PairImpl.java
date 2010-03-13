/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.templating.html;

import com.arsdigita.templating.html.AttributeMap;
import com.arsdigita.util.Assert;

/**
 * An implementation of {@link AttributeMap.Pair}.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2002-08-29
 * @version $Id: PairImpl.java 287 2005-02-22 00:29:02Z sskracic $
 **/
final class PairImpl implements AttributeMap.Pair {
    String m_qName;
    String m_value;

    public PairImpl(String qName, String value) {
        setName(qName);
        setValue(value);
    }

    public String getName() {
        return m_qName;
    }

    public String getValue() {
        return m_value;
    }

    public void setName(String qName) {
        Assert.exists(qName, "qName");
        m_qName = qName;
    }

    public void setValue(String value) {
        Assert.exists(value, "value");
        m_value = value;
    }
}
