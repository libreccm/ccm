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
 */

package com.arsdigita.london.theme;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class contains a collection of {@link
 * com.arsdigita.london.theme.ThemeFiles types}.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 * @version $Id: ThemeFileCollection.java 990 2005-11-09 17:10:48Z sskracic $
 */
public class ThemeFileCollection extends DomainCollection {

    /**
     * Constructor.
     *
     */
    public ThemeFileCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     */
    public ThemeFile getThemeFile() {
        return new ThemeFile(m_dataCollection.getDataObject());
    }


    public String getFilePath() {
        return (String)m_dataCollection.get(ThemeFile.FILE_PATH);
    }

    public BigDecimal getID() {
        return (BigDecimal)m_dataCollection.get(ThemeFile.ID);
    }

    public Date getLastModifiedDate() {
        return(Date)m_dataCollection.get(ThemeFile.LAST_MODIFIED_DATE);
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(m_dataCollection.get(ThemeFile.DELETED));
    }
}
