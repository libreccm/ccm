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

/**
 * This abstract class provides an interface for DomainObjectFactory to
 * use in order to delegate the job of instantiating a domain object
 * from a data object to custom code.
 *
 * <p>
 * Whenever a developer adds a new data object type, if they want
 * the DomainObjectFactory to support the new data object type, then
 * they must register a DomainObjectInstantiator for the new type
 * using DomainObjectFactory.registerInstantiator().
 *
 * <p>
 * An instantiator is responsible for producing a DomainObject given
 * DataObject foo if the instantiator was registered for
 * foo's object type.
 *
 * <p>
 * The task of producing a DomainObject generally involves 2 parts:
 * <ol>
 * <li>optionally delegating to another instantiator based on properties
 * of the given DataObject, and
 * <li>instantiating a DomainObject if no further delegation should occur.
 * </ol>
 *
 * Typically, the logic for delegating to another instantiator is the
 * same for all data object types with a common base type (e.g. ACSObject).
 * For this reason, the task of producing a DomainObject is broken into
 * two methods that can be overrided independently.  The method
 * <code>resolveInstantiator()</code> examines the given DataObject and
 * returns a delegate instantiator.  The method doNewInstance() performs
 * the last step of producing a data object, generally assuming that
 * no futher delegation is necessary.  Typically the instantiator for
 * a base object type (e.g. ACSObjectInstantiator) will provide an
 * implementation of <code>resolveInstantiator()</code> that will work
 * for extended types as well, so an extended type's instantiator need
 * only override doInstantiate() (usually with only one line of code
 * to instantiate a DomainObject class for the data object type in
 * question).
 *
 * @see com.arsdigita.kernel.ACSObjectInstantiator
 * @see DomainObjectFactory
 * @see DomainObject
 * @see com.arsdigita.persistence.DataObject
 *
 * @author Oumi Mehrotra
 * @version 1.0
 * @version $Id: DomainObjectInstantiator.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public abstract class DomainObjectInstantiator {

    /**
     * Return a delegated instantiator.  Called from
     * DomainObjectFactory.newInstance().  If the returned instantiator
     * is <code>this</code>, then the factory will call
     * <code>this.doNewInstance()</code>.  Otherwise, the factory will
     * again call resolveInstantiator() on the returned instantiator,
     * repeating the process until finally an instantiator's
     * resolveInstantiator() method returns itself.
     *
     * @param dataObject The data object for which to find a
     * DomainObjectInstantiator.
     *
     * @return A domain object for this data object.
     */
    public DomainObjectInstantiator resolveInstantiator(DataObject dataObject) {
        return this;
    }

    /**
     * Construct a DomainObject given a data object.  Called from
     * DomainObjectFactory.newInstance() as the last step of
     * instantiation.
     *
     * @param dataObject The data object from which to construct a domain
     * object.
     *
     * @return A domain object for this data object.
     */
    protected abstract DomainObject doNewInstance(DataObject dataObject);

}
