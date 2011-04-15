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
package com.arsdigita.portalserver;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

public class ThemeCollection extends DomainCollection {

    protected ThemeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the ID for the Theme for the current row.
     *
     * @return the id of this Theme.
     * @post return != null
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)m_dataCollection.get("id");

     // Assert.assertNotNull(id);
        Assert.exists(id);

        return id;
    }

    /**
     * Get the current item as a domain object.
     *
     * @return the domain object for the current row.
     * @post return != null
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getTheme();

     // Assert.assertNotNull(domainObject);
        Assert.exists(domainObject);

        return domainObject;
    }

    /**
     * Get the current item as a Theme domain object.
     *
     * @return a Theme domain object.
     * @post return != null
     */
    public Theme getTheme() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Theme theme = Theme.retrieveTheme(dataObject);

     // Assert.assertNotNull(theme);
        Assert.exists(theme);

        return theme;
    }

    /**
     * Get the name for the Theme for the current row.
     *
     * @return the name of this theme.
     * @post return != null
     */
    public String getName() {
        String name = (String)m_dataCollection.get("theme_name");

     // Assert.assertNotNull(name);
        Assert.exists(name);

        return name;
    }

}
