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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.ReflectionInstantiator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject       ;

/**
 * Defines the instantiator that should be
 * registered with DomainObjectFactory for the ACSObject data object type.
 * It always delegates domain object instantiation to another instantiator
 * based on the <code>objectType</code> property of the given dataObject.
 *
 * <p>
 *
 * As an example of how subtypes of ACSObject can make use of
 * ACSObjectInstantiator, here is a modified snippet of code from
 * com.arsdigita.kernel.Initializer:
 *
 * <pre>
 *      DomainObjectInstantiator instantiator = new ACSObjectInstantiator() {
 *          public DomainObject doNewInstance(DataObject dataObject) {
 *              return new Group(dataObject);
 *          }
 *      };
 *      DomainObjectFactory.registerInstantiator("com.arsdigita.kernel.Group",
 *                                               instantiator);
 * </pre>
 *
 * This initializer will work even if Group is extended.  For example,
 * suppose we add data object type FooGroup and domain object
 * class FooGroup, and register an instantiator for FooGroup in a
 * similar manner as the example code above.  Further suppose we
 * retrieve a FooGroup data object as type Group instead of FooGroup:
 * <pre>
 *    // create a FooGroup
 *    FooGroup fooGroup = new FooGroup(123);
 *    fooGroup.save();
 *
 *    // retrieve group with id 123, which happens to be a FooGroup.
 *    DataObject groupData =
 *           Session.retrieve(new OID("com.arsdigita.kernel.Group", 123));
 *
 *    // produce a domain object that encapsulates group 123.
 *    DomainObject group = DomainObjectFactory.newInstance(groupData);
 * </pre>
 *
 * What domain class was instantiated by the factory:
 * <code>Group</code> or <code>FooGroup</code>?  The answer is FooGroup.
 * <P>
 * What happens is the factory looks at <code>groupData</code>'s object type,
 * which is <code>Group</code>.  The factory looks up the registered
 * instantiator for <code>Group</code>, and then calls
 * resolveInstantiator(groupData).   <code>resolveInstantiator()</code> was
 * inherited from ACSObjectInstantiator, so it "knows" to look at the
 * <code>objectType</code> property of <code>groupData</code> and delegate
 * to whatever instantiator is registered for that type.  In this example,
 * the call to resolveInstantiator returns the <code>FooGroup</code>
 * instantiator.  The factory again calls <code>resolveInstantiator()</code>
 * on the <code>FooGroup</code> instantiator, and this time the same
 * instantiator is returned.  So the factory calls <code>doNewInstance</code>
 * on the <code>FooGroup</code> instantiator, which contains a hardcoded call
 * to the constructor for <code>FooGroup</code>.
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 *
 * @see com.arsdigita.domain.DomainObjectInstantiator
 * @see com.arsdigita.domain.DomainObjectFactory
 * @see com.arsdigita.domain.DomainObject
 * @see com.arsdigita.persistence.DataObject
 * @see com.arsdigita.kernel.ACSObject
 **/
public class ACSObjectInstantiator extends DomainObjectInstantiator {

    public static final String versionId = "$Id: ACSObjectInstantiator.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    protected boolean m_useReflectionForSubtypes = true;

    /**
     * Returns the instantiator that is registered with DomainObjectFactory
     * for the object type identified by the specified data object's
     * <code>objectType</code> property.
     *
     * <p>
     * The returned instantiator will be used by the DomainObjectFactory
     * in place of this.
     *
     * <p>
     * This method also specializes the DataObject to the object type
     * identified by the data object's <code>objectType</code> property.
     * The specialization happens to "prepare" the data object for processing
     * by the instantiator that was registered with the factory for the
     * specific data object type. This instantiator should be able to assume that
     * it will be given a data object of the type for which the instantiator
     * was registered.
     *
     * @param dataObject the data object for which to find a
     * DomainObjectInstantiator
     *
     * @return a domain object for this data object.
     *
     * @see com.arsdigita.persistence.DataObject#specialize(ObjectType)
     * @see com.arsdigita.domain.DomainObjectInstantiator
     * @see com.arsdigita.domain.DomainObjectFactory
     */
    public DomainObjectInstantiator resolveInstantiator(DataObject dataObject) {
        String type = (String) dataObject.get(ACSObject.OBJECT_TYPE);
        dataObject.specialize(type);

        DomainObjectInstantiator instantiator =
            DomainObjectFactory.getRegisteredInstantiator(dataObject.getObjectType());

        if (instantiator == null) {
            instantiator = DomainObjectFactory.
                getInstantiator(dataObject.getObjectType());

            if (instantiator == this &&
                ((ACSObjectInstantiator)instantiator).m_useReflectionForSubtypes) {
                String defaultDomainClass =
                    (String) dataObject.get(ACSObject.DEFAULT_DOMAIN_CLASS);
                instantiator = ReflectionInstantiator.
                    getInstantiator(defaultDomainClass);
            }

        }
        return instantiator;
    }

    /**
     * UNSUPPORTED--Given a data object,
     * constructs a DomainObject.  Called from
     * DomainObjectFactory.newInstance() as the last step of
     * instantiation.
     * <p>
     * This instantiator is primarily intended to be registered for the
     * <code>ACSObject</code> data object type, which is abstract.  Every
     * ACSObject data object should have a more specific object type
     * (identified by the <code>objectType</code> property), so
     * resolveInstantiator() would have returned a different instantiator
     * to delegate to.  Therefore, this method should never be called by
     * the factory unless someone registered this instantiator for a
     * concrete object type.
     * <p>
     * Note that it is okay to register this instantiator for an object
     * type that is abstract and is a subtype of ACSObject.  For example,
     * an ACSObjectInstantiator is registered for the Party object type.
     *
     * @param dataObject the data object from which to construct a domain
     * object
     *
     * @return a domain object for this data object.
     */
    protected DomainObject doNewInstance(DataObject dataObject) {
        throw new
            UnsupportedOperationException("ACSObjectInstantiator should " +
                                          "only be registered to handle " +
                                          "ACSObject or an abstract subtype " +
                                          "of ACSObject.  This instantiator " +
                                          "cannot instantiate domain " +
                                          "objects directly.  It can only " +
                                          "delegate to other instantiators. " +
                                          "Instantiator registration might " +
                                          "be incorrect, or the data object " +
                                          "might be invalid. DataObject is: " +
                                          dataObject);
    }

}
