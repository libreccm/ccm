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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;

/**
 * DomainColumn
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public abstract class DomainColumn {

    private String m_key;
    private String m_name;
    private boolean m_sortable;
    private boolean m_active;

    public DomainColumn(String key, String name) {
        this(key, name, true, false);
    }

    public DomainColumn(String key, String name, boolean sortable) {
        this(key, name, sortable, false);
    }

    public DomainColumn(String key, String name, boolean sortable,
                        boolean active) {
        m_key = key;
        m_name = name;
        m_sortable = sortable;
        m_active = active;
    }

    public String getKey() {
        return m_key;
    }

    public String getName() {
        return m_name;
    }

    public boolean isSortable() {
        return m_sortable;
    }

    public boolean isActive() {
        return m_active;
    }

    public abstract void order(PageState ps);

    public abstract Object get(PageState ps);

}
