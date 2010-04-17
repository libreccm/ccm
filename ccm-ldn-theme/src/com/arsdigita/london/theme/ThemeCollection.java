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

import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import java.util.Date;

/**
 * This class contains a collection of {@link
 * com.arsdigita.london.theme.Theme types}.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 * @version $Id: ThemeCollection.java 2004 2009-10-03 22:23:08Z pboy $ 
 */
public class ThemeCollection extends ACSObjectCollection {

    /**
     * Constructor.
     *
     */
    public ThemeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public static ThemeCollection getAllThemes() {
        return new ThemeCollection(SessionManager.getSession().retrieve
                                   (Theme.BASE_DATA_OBJECT_TYPE));
    }


    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     */
    public DomainObject getDomainObject() {
        return new Theme(m_dataCollection.getDataObject());
    }

    public Theme getTheme() {
        return (Theme)getDomainObject();
    }

    public String getTitle() {
        return(String)m_dataCollection.get(Theme.TITLE);
    }

    public String getDescription() {
        return(String)m_dataCollection.get(Theme.DESCRIPTION);
    }

    public String getURL() {
        return(String)m_dataCollection.get(Theme.URL);
    }

    public Date getLastPublishedDate() {
        return (Date)m_dataCollection.get(Theme.LAST_PUBLISHED_DATE);
    }
}
