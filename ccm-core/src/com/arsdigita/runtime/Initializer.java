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



/**
 * The Initializer interface is used to prepare the CCM runtime for
 * interaction with the CCM database. This is done in two phases.
 * First the data layer is initialized by loading any
 * object-relational mapping metadata. Secondly the domain layer is
 * initialized by registering any domain metadata. This usually
 * consists of domain object instantiators and observers. It is not
 * safe for CCM code to interact with a CCM database until these
 * phases have been completed.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface Initializer {
    public final static String versionId =
        "$Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    /**
     * Initializes the object-relational mapping metadata by loading
     * PDL resources.
     *
     * @param e A context object with accessors for data
     * initialization
     */
    void init(DataInitEvent e);

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     * @see com.arsdigita.domain.DomainObjectInstantiator
     * @see com.arsdigita.domain.DomainObjectFactory
     * @see com.arsdigita.domain.GlobalObserver
     * @see com.arsdigita.domain.GlobalObserverManager
     * @param e A context object with accessors for domain
     * initialization
     */
    void init(DomainInitEvent e);

    /**
     * A hook for free-form initialization as existed in the older
     * initialization scheme.
     *
     * @deprecated with no replacement; code that uses this hook
     * should be refactored to use {@link #init(DataInitEvent)} and
     * {@link #init(DomainInitEvent)}
     */
    void init(LegacyInitEvent e);

}
