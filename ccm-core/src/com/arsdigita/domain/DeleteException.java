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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DeleteException
 *
 * @author <a href="mailto:mbooth@redhat.com">Matthew Booth</a>
 */

public class DeleteException extends PersistenceException {
    private HashMap m_deps = new HashMap();
    private DomainObject m_obj;

    public DeleteException( DomainObject obj ) {
        super( (String) null );

        m_obj = obj;
    }

    private HashSet getObjects( String property ) {
        HashSet objects = (HashSet) m_deps.get( property );
        if( null != objects ) return objects;

        objects = new HashSet();
        m_deps.put( property, objects );

        return objects;
    }

    /**
     * Add a object dependency
     *
     * @param property The name of the persistence property which associates
     *                 the objects
     * @param obj The dependent DataObject
     */
    public void addDependency( String property, DataObject obj ) {
        HashSet objects = getObjects( property );
        objects.add( obj );
    }

    /**
     * Returns the list of persistence properties preventing the deletion of
     * this object.
     *
     * @return A Set of the String names of properties
     */
    public Set getDependencyProperties() {
        return m_deps.keySet();
    }

    /**
     * Returns the list of objects dependent on the object being deleted,
     * associated by the given property.
     *
     * @param property The name of the persistence property which associates
     *                 the objects
     * @return A Set of dependent DataObjects.
     */
    public Set getDependentObjects( String property ) {
        Set objects = (Set) m_deps.get( property );
        if( null == objects ) return null;

        return objects;
    }

    /**
     * Display dependent objects, nicely formatted.
     */
    public String getMessage() {
        StringBuffer msg = new StringBuffer();

        msg.append( "Object " ).append( m_obj.getOID().toString() );
        msg.append( " has dependencies which prevent it from being deleted.\n" );

        Iterator properties = getDependencyProperties().iterator();
        while( properties.hasNext() ) {
            String property = (String) properties.next();

            msg.append( property ).append( ':' );

            Iterator objs = getDependentObjects( property ).iterator();
            while( objs.hasNext() ) {
                DataObject obj = (DataObject) objs.next();

                msg.append( ' ' ).append( obj.getOID().toString() );
            }
            msg.append( '\n' );
        }

        return msg.toString();
    }
}
