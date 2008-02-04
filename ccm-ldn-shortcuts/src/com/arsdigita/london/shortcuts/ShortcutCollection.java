/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.shortcuts;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @see Shortcut
 * @version $Id: ShortcutCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShortcutCollection extends DomainCollection {
    public static final String versionId = "$Id: ShortcutCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2003/10/22 07:27:00 $";

    protected ShortcutCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    /**
     * Get the ID for the portal for the current row.
     *
     * @return the id of this portal.
     * @post return != null 
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)m_dataCollection.get("id");
 
        Assert.exists(id, BigDecimal.class);
       
        return id;
    }

    /**
     * Get the current item as a domain object.
     * 
     * @return the domain object for the current row.
     * @post return != null
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getShortcut();

        Assert.exists(domainObject, DomainObject.class);

        return domainObject;
    }

    /**
     * Get the current item as a Shortcut domain object.
     *
     * @return a Shortcut domain object.
     * @post return != null
     */
    public Shortcut getShortcut() {
        DataObject dataObject = m_dataCollection.getDataObject();
 
        Shortcut portal = new Shortcut(dataObject);

        Assert.exists(portal, Shortcut.class);

        return portal;
    }

    /**
     * Get the title for the portal for the current row.
     *
     * @return the title of this portal.
     * @post return != null 
     */
    public String getTitle() {
        String title = (String)m_dataCollection.get("title");

        Assert.exists(title, String.class);

        return title;
    }
}
