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
package com.arsdigita.domain;

import com.arsdigita.persistence.DataObject;

public class JavaPackageInstantiator extends DomainObjectInstantiator
{

    public static final String versionId = "$Id: JavaPackageInstantiator.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    /**
     * Construct a JavaPackage given a data object.  Called from
     * DomainObjectFactory.newInstance() as the last step of
     * instantiation.
     *
     * @param dataObject The data object from which to construct a domain
     * object.
     *
     * @return A domain object for this data object.
     */
    protected DomainObject doNewInstance(DataObject dataObject) {
        return new JavaPackage(dataObject);

    }

}
