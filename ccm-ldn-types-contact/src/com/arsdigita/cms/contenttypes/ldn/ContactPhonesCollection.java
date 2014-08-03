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
package com.arsdigita.cms.contenttypes.ldn;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;

/**
 * Collection of <code>ContactPhone</code>S objects.
 *
 * @author Shashin Shinde <a
 * href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactPhonesCollection.java 287 2005-02-22 00:29:02Z sskracic
 * $
 */
public class ContactPhonesCollection extends DomainCollection {

    /**
     * Constructor.
     *
     * @param dataCollection
     */
    public ContactPhonesCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in the
     * collection.
     *
     * @return 
     */
    public ContactPhone getPhone() {
        return new ContactPhone(m_dataCollection.getDataObject());
    }

    public String getPhoneType() {
        return getPhone().getPhoneType();
    }

    public String getPhoneNumber() {
        return getPhone().getPhoneNumber();
    }

}
