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
package com.arsdigita.runtime;

import com.arsdigita.domain.DomainObjectFactory;

/**
 * A DomainInitEvent is passed to the {@link
 * Initializer#init(DomainInitEvent)} to provide access to the
 * necessary objects required to initialize the domain layer.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: DomainInitEvent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class DomainInitEvent {
    public final static String versionId =
        "$Id: DomainInitEvent.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private final DomainObjectFactory m_factory;

    DomainInitEvent(final DomainObjectFactory factory) {
        m_factory = factory;
    }

    /**
     * Returns a DomainObjectFactory object that should be used to
     * register domain object instantiators.
     *
     * @return a DomainObjectFactory
     **/

    public final DomainObjectFactory getFactory() {
        return m_factory;
    }

}
