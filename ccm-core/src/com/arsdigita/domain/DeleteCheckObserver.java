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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.metadata.Property;

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * <code>DomainObjectObserver</code> which checks that a delete will not fail
 * before trying to perform it. If it will, it returns a
 * <code>DeleteException</code> containing information about which objects
 * and associations would cause the failure.
 *
 * @author <a href="mailto:mbooth@redhat.com">Matthew Booth</a>
 */
public class DeleteCheckObserver
    extends AbstractDomainObjectObserver
{
    private static final Logger s_log =
        Logger.getLogger( DeleteCheckObserver.class );

    private static final DeleteCheckObserver s_observer =
        new DeleteCheckObserver();

    private DeleteCheckObserver() { }

    public static final void observe( ObservableDomainObject obj ) {
        obj.addObserver( s_observer );
    }

    /**
     * Check there are no associated objects which would cause a failure if
     * this object were deleted.
     *
     * @param obj the domain object to be deleted
     */
    public void beforeDelete( DomainObject obj ) throws PersistenceException {
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Checking delete of " + obj.getOID() );
        }

        DeleteException ex = new DeleteException( obj );

        DataObject dObj = obj.getDataObject();

        boolean error = false;
        Iterator properties = dObj.getObjectType().getProperties();
        while( properties.hasNext() ) {
            Property property = (Property) properties.next();
            String propName = property.getName();

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Checking property " + propName );
            }

            // If this is a component association, persistence should
            // delete it automatically
            if( property.isComponent() ) continue;

            // If this object is required by a non-component object, that's
            // a problem
            Property assocProp = property.getAssociatedProperty();
            if( null != assocProp && assocProp.isRequired() ) {
                if( s_log.isDebugEnabled() ) {
                    s_log.debug( propName + " would cause failure" );
                }

                Object assocObj = dObj.get( property.getName() );

                if( assocObj instanceof DataAssociation ) {
                    DataAssociation assoc = (DataAssociation) assocObj;
                    DataAssociationCursor cursor = assoc.cursor();

                    while( cursor.next() ) {
                        error = true;
                        ex.addDependency( propName,
                                          cursor.getDataObject() );

                        if( s_log.isDebugEnabled() ) {
                            s_log.debug( "Dependent object " + cursor.getDataObject().getOID() );
                        }
                    }
                }

                else if( null != assocObj ) {
                    error = true;
                    DataObject assocDObj = (DataObject) assocObj;
                    ex.addDependency( propName, assocDObj );

                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Dependent object " + assocDObj.getOID() );
                    }
                }
            }
        }

        if( error ) throw ex;
    }
}
