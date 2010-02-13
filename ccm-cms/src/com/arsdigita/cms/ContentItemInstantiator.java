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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;

/**
 * Implements the {@link DomainObjectInstantiator} for {@link
 * com.arsdigita.cms.ContentItem content items} using reflection. This
 * class should always instantiate the right subclass of {@link
 * com.arsdigita.cms.ContentItem} automatically.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: ContentItemInstantiator.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated Use {@link com.arsdigita.kernel.ACSObjectInstantiator}
 * instead
 */
public class ContentItemInstantiator extends DomainObjectInstantiator {

    /**
     * Construct a {@link ContentItem} given a data object.  Called from
     * DomainObjectFactory.newInstance() as the last step of
     * instantiation.
     *
     * @param dataObject The data object from which to construct a domain
     * object.
     *
     * @return A domain object for this data object.
     */
    public DomainObject doNewInstance(DataObject dataObject) {
        return ACSObjectFactory.castContentItem(dataObject);
    }
}
