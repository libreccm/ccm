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
 *
 */
package com.arsdigita.kernel;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * Represents a collection of package types.
 *
 * @since ACS 5.0
 * @version $Revision: #15 $, $Date: 2004/08/16 $
 * @version $Id: PackageTypeCollection.java 287 2005-02-22 00:29:02Z sskracic $

 * @deprecated without direct replacement. Refactor to use
 *             {@link com.arsdigita.web.ApplicationTypeCollection} instead.
 */
public class PackageTypeCollection extends DomainCollection {

    /**
     * 
     * @param dataCollection 
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationTypeCollection} instead.
     */
    protected PackageTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * 
     * @return 
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationTypeCollection} instead.
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getPackageType();

        return domainObject;
    }

    /**
     * 
     * @return 
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationTypeCollection} instead.
     */
    public PackageType getPackageType() {
        DataObject dataObject = m_dataCollection.getDataObject();

        PackageType packageType = new PackageType(dataObject);

        return packageType;
    }
}
