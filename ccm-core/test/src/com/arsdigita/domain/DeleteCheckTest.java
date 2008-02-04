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
package com.arsdigita.domain;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

public class DeleteCheckTest extends DomainTestCase {
    private static final Logger s_log =
        Logger.getLogger( DeleteCheckTest.class );

    public DeleteCheckTest( String name ) {
        super(name);
    }

    private DomainObjectInstantiator m_objInst =
            new DomainObjectInstantiator() {
                protected DomainObject doNewInstance( DataObject dataObject ) {
                    return new ACSObject( dataObject ) { };
                }
            };

    protected void domainSetUp() {
        DomainObjectFactory.registerInstantiator(
            "com.arsdigita.domain.SingleComponent1",
            m_objInst
        );

        DomainObjectFactory.registerInstantiator(
            "com.arsdigita.domain.MultipleComponent1",
            m_objInst
        );

        DomainObjectFactory.registerInstantiator(
            "com.arsdigita.domain.SingleAssociation1",
            m_objInst
        );
    }

    public void testSingleComponent() {
        s_log.debug( "Start single component" );

        ACSObject obj1 = new ACSObject( "com.arsdigita.domain.SingleComponent1" ) { };
        ACSObject obj2 = new ACSObject( "com.arsdigita.domain.SingleComponent2" ) { };
        obj2.set( "compObject1", obj1 );

        DeleteCheckObserver.observe( obj1 );
        obj1.delete();

        s_log.debug( "Finished single component" );
    }

    public void testMultipleComponent() {
        s_log.debug( "Start multiple component" );

        ACSObject container = new ACSObject( "com.arsdigita.domain.MultipleComponent1" ) { };

        ACSObject contained1 = new ACSObject( "com.arsdigita.domain.MultipleComponent2" ) { };
        ACSObject contained2 = new ACSObject( "com.arsdigita.domain.MultipleComponent2" ) { };

        container.add( "compObjects2", contained1 );
        container.add( "compObjects2", contained2 );

        DeleteCheckObserver.observe( container );
        container.delete();

        s_log.debug( "Finished multiple component" );
    }

    public void testSingleAssociation() {
        s_log.debug( "Start single association" );

        ACSObject obj1 = new ACSObject( "com.arsdigita.domain.SingleAssociation1" ) { };
        ACSObject obj2 = new ACSObject( "com.arsdigita.domain.SingleAssociation2" ) { };

        obj1.set( "assocObject2", obj2 );

        DeleteCheckObserver.observe( obj1 );
        try {
            obj1.delete();
        } catch( DeleteException ex ) {
            Set properties = ex.getDependencyProperties();

            if( !properties.contains( "assocObject2" ) )
                fail( "Test case should have dependent property assocObject2" );

            Iterator objects = ex.getDependentObjects( "assocObject2" ).iterator();
            boolean gotObject = false;
            while( objects.hasNext() ) {
                if( gotObject )
                    fail( "Test case should only have exactly 1 dependent object" );
                
                DataObject object = (DataObject) objects.next();
                s_log.debug( "SingleAssociation dependent object " + object.getOID() );

                if( !object.getOID().equals( obj2.getOID() ) )
                    fail( "DeleteException returned incorrect dependent object" );

                gotObject = true;
            }

            if( !gotObject )
                fail( "Dependent property contained no objects" );

            s_log.debug( "Finished single association" );
            return;
        }

        fail( "Delete of non-composite associated object should have thrown a DeleteException" );
    }

    public void testMultipleAssociation() {
        s_log.debug( "Start multiple assocation" );

        ACSObject container = new ACSObject( "com.arsdigita.domain.MultipleAssociation1" ) { };
        ACSObject contained1 = new ACSObject( "com.arsdigita.domain.MultipleAssociation2" ) { };
        ACSObject contained2 = new ACSObject( "com.arsdigita.domain.MultipleAssociation2" ) { };

        container.add( "assocObjects2", contained1 );
        container.add( "assocObjects2", contained2 );

        DeleteCheckObserver.observe( container );
        try {
            container.delete();
        } catch( DeleteException ex ) {
            Set properties = ex.getDependencyProperties();

            if( !properties.contains( "assocObjects2" ) )
                fail( "Test case should have dependent property assocObjects2" );
            Iterator objects = ex.getDependentObjects( "assocObjects2" ).iterator();
            HashSet depObjects = new HashSet();
            while( objects.hasNext() ) {
                DataObject obj = (DataObject) objects.next();
                depObjects.add( obj.getOID() );
            }

            if( depObjects.size() > 2 ) {
                Iterator i = depObjects.iterator();
                StringBuffer buf = new StringBuffer();
                while( i.hasNext() ) {
                    DataObject obj = (DataObject) i.next();
                    buf.append( ' ' ).append( obj.getOID() );
                }

                fail( "assocObjects2 should only contain 2 objects. It actually contains:" + buf.toString() );
            }

            if( !depObjects.contains( contained1.getOID() ) )
                fail( "assocObjects2 doesn't contain contained1" );

            if( !depObjects.contains( contained2.getOID() ) )
                fail( "assocObjects2 doesn't contain contained2" );

            s_log.debug( "Finished multiple association" );
            return;
        }

        fail( "Delete of non-composite associated object should have thrown a DeleteException" );
    }

    public static void fail( String msg ) {
        s_log.debug( "Failed: " + msg );
        DomainTestCase.fail( msg );
    }
}
