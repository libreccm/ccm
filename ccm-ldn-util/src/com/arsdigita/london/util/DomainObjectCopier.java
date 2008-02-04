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
 */

package com.arsdigita.london.util;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainService;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Tracer;
import com.arsdigita.util.Classes;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Recursively copies a domain object.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: DomainObjectCopier.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class DomainObjectCopier extends DomainService {
    public static final String versionId =
        "$Id: DomainObjectCopier.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/03/01 09:31:36 $";

    private static Logger s_log = Logger.getLogger(DomainObjectCopier.class);

    // A map of OID => DomainObject
    private final HashMap m_copied;
    private final TraversedSet m_traversed;
    private final Tracer m_trace;

    /**
     * Constructs a new <code>DomainCopier</code>
     */
    public DomainObjectCopier() {
        m_copied = new HashMap();
        m_traversed = new TraversedSet();
        m_trace = new Tracer(DomainObjectCopier.class);
    }

    /**
     * Kicks off the copying process.  Creates a copy by value of
     * <code>source</code> and then traverses its associations and
     * repeats the process.
     *
     * @param source the <code>DomainObject</code> from which to copy
     */
    public  DomainObject copy(final DomainObject source) {
        m_trace.enter("copy", source);

        final OID sourceOID = source.getOID();
        final DomainObject already = (DomainObject) m_copied.get(sourceOID);

        if (already == null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Copying " + source);
            }

            final Class clacc = source.getClass();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Using class " + clacc.getName());
            }

            DomainObject target = null;

            if (source instanceof ACSObject) {
                final String type =
                    ((ACSObject) source).getSpecificObjectType();
                
                target = (DomainObject)Classes.newInstance(
                    clacc, 
                    new Class[] { String.class },
                    new Object[] { type });
            } else {
                // XXX we assume non-ACSObjects have a no-arg constructor
                // that 'does the right thing'. This is probably bogus,
                // we for lack of better ideas....
                target = (DomainObject)Classes.newInstance(
                    clacc,
                    new Class[] {},
                    new Object[] {});
            }

            Assert.exists(target, DomainObject.class);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Created empty copy shell " + target);
            }

            // Prevent infinite recursion
            m_copied.put(sourceOID, target);

            copyData(source, target);
            m_trace.exit("copy", target);

            return target;
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("There is already a copy; returning it");
            }

            m_trace.exit("copy", already);

            return already;
        }
    }

    protected void copyData(final DomainObject source, DomainObject target) {
            final ObjectType type = source.getObjectType();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Using object type " + type.getName());
            }

            // XXX This is what I would like to do:
            //
            //final Iterator iter = type.getProperties();
            //
            //while (iter.hasNext()) {
            //    copyProperty(source, target, (Property) iter.next());
            //}

            // But the beforeSave-on-flush behavior makes it so I have
            // to do this instead:

            Iterator iter = type.getProperties();
            final ArrayList attributes = new ArrayList();
            final ArrayList roles = new ArrayList();
            final ArrayList collections = new ArrayList();

            while (iter.hasNext()) {
                final Property prop = (Property) iter.next();

                if (prop.isAttribute()) {
                    attributes.add(prop);
                } else if (prop.isCollection()) {
                    collections.add(prop);
                } else {
                    roles.add(prop);
                }
            }

            iter = attributes.iterator();

            while (iter.hasNext()) {
                copyProperty(source, target, (Property) iter.next());
            }

            iter = roles.iterator();

            while (iter.hasNext()) {
                copyProperty(source, target, (Property) iter.next());
            }

            iter = collections.iterator();

            while (iter.hasNext()) {
                copyProperty(source, target, (Property) iter.next());
            }

        
    }

    /**
     * Copies properties.  This method is called from {@link
     * #copy(DomainObject)} for each property of the object being
     * copied.
     *
     * This implementation calls {@link #copyAttribute(DomainObject,
     * DomainObject, Property)} if the property is a scalar attribute
     * and {@link #copyRole(DomainObject, DomainObject, Property)} if
     * the property belongs to an association.
     *
     * If the property is a key property, this method skips it.
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new copy
     * @param prop the <code>Property</code> currently under
     * consideration
     */
    protected void copyProperty(final DomainObject source,
                                final DomainObject target,
                                final Property prop) {
        m_trace.enter("copyProperty", source, target, prop);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Considering property " + prop + " for copying");
        }

        if (prop.isKeyProperty()) {
            s_log.debug("The property is one of the key properties; " +
                        "skipping it");
        } else {
            s_log.debug("Copying is enabled; proceeding");

            if (prop.isAttribute()) {
                copyAttribute(source, target, prop);
            } else {
                copyRole(source, target, prop);
            }
        }

        m_trace.exit("copyProperty");
    }

    /**
     * Copies properties that are scalar attributes, not associations.
     *
     * This implementation gets the value of <code>prop</code> on
     * <code>source</code> and sets it on <code>target</code>.
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new <code>DomainObject</code> copy
     * @param prop the <code>Property</code> of <code>source</code>
     * being copied
     */
    protected void copyAttribute(final DomainObject source,
                                 final DomainObject target,
                                 final Property prop) {
        m_trace.enter("copyAttribute", source, target, prop);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying attribute " + prop + " by value");
        }

        final String name = prop.getName();

        set(target, name, get(source, name));

        m_trace.exit("copyAttribute");
    }

    /**
     * Copies properties that belong to associations.  This method is
     * called from {@link #copyProperty(DomainObject, DomainObject,
     * Property}.
     *
     * This implementation calls {@link #copyCollection(DomainObject,
     * DomainObject, Property)} if the property belongs to a
     * <code>0..n</code> association.  Otherwise it calls {@link
     * #copy(DomainObject, DomainObject, DomainObject, Property)} for
     * the value on <code>source</code> and sets the result on
     * <code>target</code>.
     *
     * If the role belongs to a link that has already been traversed,
     * this method skips it.
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new <code>DomainObject</code> copy
     * @param prop the <code>Property</code> of <code>source</code>
     * being copied
     */
    protected void copyRole(final DomainObject source,
                            final DomainObject target,
                            final Property prop) {
        m_trace.enter("copyRole", source, target, prop);

        final String name = prop.getName();

        if (m_traversed.contains(source, prop)) {
            s_log.debug("The role belongs to a link that has " +
                        "already been traversed; skipping it");
        } else {
            // This marks the forward link traversed.  Further down,
            // in this method and in copyCollection, we mark the
            // reverse link traversed as well.
            m_traversed.add(source, prop);

            if (prop.isCollection()) {
                s_log.debug("The property is a 0..n association");

                copyCollection(source, target, prop);
            } else if (prop.isRequired()) {
                s_log.debug("The property is a 1..1 association");

                final DataObject data = (DataObject) get(source, name);

                Assert.exists(data, DataObject.class);

                final DomainObject domain = domain(data);

                m_traversed.add(domain, prop.getAssociatedProperty());

                set(target, name, copy(source, target, domain, prop));
            } else if (prop.isNullable()) {
                s_log.debug("The property is a 0..1 association");

                final DataObject data = (DataObject) get(source, name);

                if (data == null) {
                    set(target, name, null);
                } else {
                    final DomainObject domain = domain(data);

                    m_traversed.add(domain, prop.getAssociatedProperty());

                    set(target, name, copy(source, target, domain(data), prop));
                }
            } else {
                Assert.fail("Unknown property type");
            }
        }

        m_trace.exit("copyRole");
    }

    /**
     * Copies properties that belong to <code>0..n</code>
     * associations.  This method is called from {@link
     * #copyRole(DomainObject, DomainObject, Property)}.
     *
     * This implementation calls {@link #copy(DomainObject,
     * DomainObject, DomainObject, Property)} for each value fetched
     * from <code>prop</code> on <code>source</code> and sets the
     * result on <code>target</code>.
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new <code>DomainObject</code> copy
     * @param prop the <code>Property</code> of <code>source</code>
     * being copied
     */
    protected void copyCollection(final DomainObject source,
                                  final DomainObject target,
                                  final Property prop) {
        m_trace.enter("copyCollection", source, target, prop);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying collection " + prop);
        }

        final String name = prop.getName();

        final DataAssociation sass = (DataAssociation) get(source, name);
        final DataAssociationCursor scursor = sass.cursor();
        final Property reverse = prop.getAssociatedProperty();

        while (scursor.next()) {
            final DomainObject selem = domain(scursor.getDataObject());

            m_traversed.add(selem, reverse);

            final DomainObject telem = copy(source, target, selem, prop);
            DataObject tgtLink = null;

            // removing this assert since copy will return null in the
            // case of deferred association creation in VersionCopier
            //Assert.exists(telem, DomainObject.class);
            
            if (telem != null) {
                tgtLink = add(target, name, telem);
            }
            if (tgtLink != null) {
                // Copy link attributes as well
                copyData(new WrapperDomainObject(scursor.getLink()),
                         new WrapperDomainObject(tgtLink));
            }

        }

        m_trace.exit("copyCollection");
    }

    /**
     * Creates a copy, by reference or by value, of the property
     * represented in <code>object</code>.
     *
     * This implementation returns the result of {@link
     * #copy(DomainObject)} if the property is a component and simply
     * returns <code>object</code> if it is not.
     *
     * @param source the <code>DomainObject</code> source (original)
     * object to which this property belongs
     * @param target the new <code>DomainObject</code> copy to which
     * the return value of this method will be attached
     * @param object the <code>DomainObject</code> property being
     * copied
     * @param prop a <code>Property</code> representing
     * <code>object</code>
     * @return <code>object</code> if <code>prop</code> is not a
     * component or a copy of <code>object</code> it is a component
     */
    protected DomainObject copy(final DomainObject source,
                                final DomainObject target,
                                final DomainObject object,
                                final Property prop) {
        m_trace.enter("copy", object, prop);

        if (prop.isComponent()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The property is a component; " +
                            "copying by value");
            }

            final DomainObject copy = copy(object);

            m_trace.exit("copy", copy);

            return copy;
        } else {
            s_log.debug("The property is not a component; " +
                        "copying by reference");

            m_trace.exit("copy", object);

            return object;
        }
    }

    /**
     * Fetch the copy of the object with the given OID. Return null if
     * the copy does not exist, or if the copy is not a DomainObject.
     *
     * @param oid the OID to look up
     * @return a copy of the object with the given OID, or null on
     * failure
     * @see ItemCopier#getCopy(OID)
     */
    public DomainObject getCopy(final OID oid) {
        return (DomainObject) m_copied.get(oid);
    }


    // Utility methods and classes

    private DomainObject domain(final DataObject data) {
        Assert.exists(data, DataObject.class);

        final DomainObject domain = DomainObjectFactory.newInstance(data);

        Assert.exists(domain, DomainObject.class);

        return domain;
    }

    private static class TraversedSet extends HashSet {
        void add(final DomainObject object, final Property prop) {
            Assert.exists(object, DomainObject.class);

            if (prop != null) {
                add(object.getOID() + "." + prop.getName());
            }
        }

        boolean contains(final DomainObject object,
                         final Property prop) {
            Assert.exists(object, DomainObject.class);
            Assert.exists(prop, Property.class);

            return contains(object.getOID() + "." + prop.getName());
        }
    }
    private final class WrapperDomainObject extends DomainObject{
        public WrapperDomainObject(DataObject dobj) {
            super(dobj);
        }
        
        public WrapperDomainObject(OID oid) {
            super(oid);
        }
        
    }
}
