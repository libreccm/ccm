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

public class DomainObjectFactoryTest extends DomainTestCase {

    public DomainObjectFactoryTest(String name) {
        super(name);
    }

    protected void domainTearDown() {
        // Return to original state.
        DomainObjectFactory.resetFactory();
        super.domainTearDown();
    }

    public void testNewInstance() throws Exception {

        DataObject object = getSession().create("com.arsdigita.kernel.User");
        try {
            DomainObjectFactory.newInstance(object);
            fail("Shouldn't be able to make a newInstance for " + object.getObjectType());
        }
        catch(RuntimeException e) {
        }

        DomainObjectFactory.registerInstantiator( object.getObjectType(),
                                                  makeDefaultInstantiator() );
        DomainObject o = DomainObjectFactory.newInstance(object);
        assertNotNull("Should have made a domain object!!!", o);

    }


    /**
     * Tests fix for bz 98292
     */
    public void testNullDataOjbect() {
        DomainObject result = DomainObjectFactory.newInstance((DataObject) null);
        assertNull(result);
    }

    public void testRegisterInstantiator() {
        DomainObjectInstantiator prev;
        DomainObjectInstantiator first = makeDefaultInstantiator();

        prev = DomainObjectFactory.registerInstantiator( "com.arsdigita.kernel.User",
                                                         first );
        assertNull("Should be no previous instantiator!", prev);
        DataObject object = getSession().create("com.arsdigita.kernel.User");
        prev = DomainObjectFactory.registerInstantiator( object.getObjectType(),
                                                         makeDefaultInstantiator() );

        assertEquals( "Didn't get back original instantiator!", first, prev);


    }

    public void testGetInstantiator() {
        DomainObjectInstantiator inst;
        inst = DomainObjectFactory.getInstantiator( "com.arsdigita.kernel.User" );
        assertNull( "There should be no instantiators!", inst );

        DomainObjectInstantiator userInst = makeDefaultInstantiator();
        DomainObjectFactory.registerInstantiator( "com.arsdigita.kernel.User",
                                                  userInst );
        inst = DomainObjectFactory.getInstantiator( "com.arsdigita.kernel.User" );
        assertEquals( "Didn't get back user instantiator!", userInst, inst);

        DataObject object = getSession().create("com.arsdigita.kernel.Party");
        DomainObjectInstantiator partyInst = makeDefaultInstantiator();
        DomainObjectFactory.registerInstantiator( object.getObjectType(),
                                                  partyInst);

        inst = DomainObjectFactory.getInstantiator( object.getObjectType() );
        assertEquals( "Didn't get back party instantiator!", partyInst, inst);


    }

    DomainObjectInstantiator makeDefaultInstantiator() {
        return new DomainObjectInstantiator()  {
                protected DomainObject doNewInstance(DataObject dataObject)  {
                    return new DomainObject(dataObject) {

                        };
                }
            };
    }


}
