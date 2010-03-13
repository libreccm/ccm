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
package com.arsdigita.versioning;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

// old versioning

/**
 * VersionController class
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @author Stanislav Freidin
 * @version $Revision: #21 $ $Date: 2004/08/16 $
 */
class VersionController {

    private static final Logger s_log =
        Logger.getLogger(VersionController.class);

    /**
     * Recursively follows the object's composite sub-objects, setting their
     * master_id to the given top-level object. This method should be used by an
     * initializer to upgrade existing objects to correctly versioned objects.
     *
     * @param obj     the object to be fixed
     * @param master  the correct master object
     */
    protected static void autoPropagateMaster
        (VersionedACSObject obj, VersionedACSObject master) {
        Assert.isTrue
            (master.isMaster(),
             "Object " + master.getOID() + " is the master object");

        recursivelyTraverse
            (obj, master, new TraversalListener() {
                public boolean preorderProcess
                    (DataObject data, VersionedACSObject vobj,
                     VersionedACSObject master) {

                    // Fix the master for the current object
                    if(! master.equals(vobj.getMaster())) {
                        s_log.info
                            ("Object " + vobj.getID() +
                             " had wrong master object " +
                             vobj.getMaster().getID() +
                             ", setting correct master " + master.getOID());
                        vobj.setMaster(master);
                        vobj.save();
                    }

                    return true;
                }

                public boolean postorderProcess
                    (DataObject data, VersionedACSObject vobj,
                     VersionedACSObject master) {
                    return true;
                }
            });
    }


    /**
     * Recursively follows the object's composite sub-objects, executing some
     * action for each object (including the topmost parent).
     *
     * @param obj      the object to check
     * @param master   the correct master object
     * @param action   the {@link TraversalListener} that will perform the
     *   required action on each object
     */
    private static void recursivelyTraverse
        (VersionedACSObject obj, VersionedACSObject master,
         TraversalListener action) {
        recursivelyTraverse(obj, master, new HashSet(), action);
    }

    /**
     * Recursively follows the object's composite sub-objects, executing some
     * action for each object (including the topmost parent).
     *
     * @param obj      the object to check
     * @param master   the correct master object
     * @param visited  a set of all objects visited so far; ensures that
     *   there are no loops in the containment hierarchy.
     * @param action   the {@link TraversalListener} that will perform the
     *   required action on each object
     */
    private static void recursivelyTraverse
        (VersionedACSObject obj, VersionedACSObject master, HashSet visited,
         TraversalListener action) {
        // Specialize the object
        DataObject data = DomainServiceInterfaceExposer.getDataObject(obj);
        VersionedACSObject vobj =
            (VersionedACSObject) DomainObjectFactory.newInstance(data);
        OID oid = vobj.getOID();

        if (visited.contains(oid)) {
            throw new IllegalStateException
                ("The object " + oid + " appears more than once in the " +
                 "containment hierarchy. This indicates that the PDL files " +
                 "for the object or its containters create a loop, " +
                 "such as \'A contains component B, B contains " +
                 "component A\'; this is illegal for a variety of reasons.");
        }

        // Prevent loops
        visited.add(oid);

        // Perform the action for the current object
        if(!action.preorderProcess(data, vobj, master)) {
            return;
        }

        // Iterate over properties
        ObjectType type = oid.getObjectType();

        for (Iterator i = type.getProperties(); i.hasNext(); ) {
            Property prop = (Property) i.next();
            String propName = prop.getName();

            // Skip aggregates, skip scalars,
            // skip master object reference
            if (!prop.isComponent() || !prop.isRole() ||
                VersionedACSObject.MASTER.equals(propName)) {
                continue;
            }

            // Get the value directly from the data object, to avoid
            // the overridden get method that skips deleted objects
            Object value = data.get(propName);

            // If value is null, nothing to worry about
            if(value == null) {
                continue;
            }

            if (value instanceof DataObject) {
                // Check a 1..1 or 0..1 association
                traverseDataObject((DataObject)value, master, visited, action);
            } else if (value instanceof DataAssociation) {
                // Check 1..n, 0..n association
                DataAssociation assoc = (DataAssociation)value;
                ObjectType childType = assoc.getObjectType();
                if (VersionedACSObject.isSubtype(childType)) {
                    // Iterate over children
                    DataAssociationCursor cur =
                        assoc.getDataAssociationCursor();
                    while (cur.next()) {
                        // Fix link attribute
                        DataObject linkData = cur.getLink();
                        if (linkData != null) {
                            traverseDataObject
                                (linkData, master, visited, action);
                        }
                        // Fix actual data
                        traverseDataObject
                            (cur.getDataObject(), master, visited, action);
                    }

                    cur.close();
                }
            }
        }

        // Perform the postorder action for the current object
        if (!action.postorderProcess(data, vobj, master)) {
            return;
        }
    }

    // Helper method to convert a data object to a domain object, if possible
    private static void traverseDataObject
        (DataObject data, VersionedACSObject master, HashSet visited,
         TraversalListener action) {
        ObjectType type = data.getObjectType();
        if (VersionedACSObject.isSubtype(type)) {
            VersionedACSObject obj =
                (VersionedACSObject)DomainObjectFactory.newInstance(data);
            recursivelyTraverse(obj, master, visited, action);
        }
    }

    /**
     * A listener that runs for each object during recursive traversal.
     */
    private interface TraversalListener {

        /**
         * Processes the given object before processing its children.
         *
         * @param data the current <code>DataObject</code>
         * @param vobj the corresponding <code>VersionedACSObject</code>
         * @param master the correct master object for the current object;
         *   may be different from <code>vobj.getMaster()</code>
         * @return true if the traversal is to continue, false if it needs
         *   to be aborted
         */
        public boolean preorderProcess
            (DataObject data, VersionedACSObject vobj,
             VersionedACSObject master);

        /**
         * Processes the given object after processing its children.
         *
         * @param data the current <code>DataObject</code>
         * @param vobj the corresponding <code>VersionedACSObject</code>
         * @param master the correct master object for the current object;
         *   may be different from <code>vobj.getMaster()</code>
         * @return true if the traversal is to continue, false if it needs
         *   to be aborted
         */
        public boolean postorderProcess
            (DataObject data, VersionedACSObject vobj,
             VersionedACSObject master);
    }
}
