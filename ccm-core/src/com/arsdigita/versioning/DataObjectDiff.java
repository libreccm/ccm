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
package com.arsdigita.versioning;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

// new versioning

/*
 * Since this class is package-private, the Javadoc is not generated for it by
 * default.  If you want this class to be included in the Javadoc output, add
 * the "access" attribute to the "javadoc" Ant task:
 *
 *
 * <javadoc
 *   access="package"
 *   ... boilerplate attributes go here ...
 * >
 */

/**
 * Provides an in-memory representation of the difference between the current
 * data object and one of its past states.
 *
 * <p>Data object diffs (DODs) are kept in and manipulated by a {@link
 * DiffSet diff set}.  Both classes exist for the sole purpose of
 * enabling the {@link Versions version controller} to perform rollback.  To
 * roll a data object <em>X</em> back means to restore its attribute values to
 * an earlier point in time.  If we let <em>T<sub>0</sub></em> denote the
 * earlier point in the data object's history and the <em>T<sub>1</sub></em> the
 * present moment, then we can say that the rollback operation goes back in time
 * from <em>T<sub>1</sub></em> to <em>T<sub>0</sub></em> and undoes changes that
 * occured to the data object <em>X</em> during the time period
 * [<em>T<sub>0</sub></em>, <em>T<sub>1</sub></em>]. </p>
 *
 * <p>To perform rollback, the version controller reads the versioning log
 * looking for events pertaining the data object <em>X</em> that is being rolled
 * back. The changes in the values of the data objects attributes are undone one
 * by one. Suppose the data object's <em>X</em> partial history snapshot looks
 * schematically like so: </p>
 *
 * <pre>
 * ---*------*--------*----------*----------*--->
 *   CRT     T0      DLT        CRT         T1
 * </pre>
 *
 * <p>In other words, <em>X</em> was created at some point before
 * <em>T<sub>0</sub></em>. Two things things happend in the interval
 * [<em>T<sub>0</sub></em>, <em>T<sub>1</sub></em>]. The data object <em>X</em>
 * was deleted and, at a later point, recreated again. Here, the term
 * "recreated" refers to the fact that a data object with the same {@link
 * com.arsdigita.persistence.OID oid} was created, most likely as a result of a
 * rollback performed by the versioning service. At point
 * <em>T<sub>1</sub></em>, we decided to roll <em>X</em> back to point
 * <em>T<sub>0</sub></em>.</p>
 *
 * <p>Clearly, there are constraints on what possible histories of <em>X</em>
 * might look like. For example, the following history would be illegal:</p>
 *
 *
 * <pre>
 * ---*-------*--------*-----------*------------->
 *   CRT     CRT      DLT         CRT
 * </pre>
 *
 * <p>An data object cannot be created twice without having been deleted between
 * the two create events.  Likewise, a data object cannot be deleted two times
 * in a row without having been restored after the first deletion. Furthermore,
 * a data object cannot be rolled back to any point with a
 * [<em>DLT<sub>k</sub></em>, <em>CRT<sub>k</sub></em>] interval where
 * <em>DLT<sub>k</sub></em> and <em>CRT<sub>k</sub></em> are two adjacent
 * delete/create events. </p>
 *
 * <p>Note that when the version controller traverses the versioning log
 * backwards from <em>T<sub>1</sub></em> to <em>T<sub>0</sub></em>, changes are
 * made to DODs rather than the actual data objects. (A DOD is tied to its
 * "real" data object via an oid. See {@link #DataObjectDiff(OID)} and {@link
 * #getOID()}.) Once all the changes have been undone in this manner, the DOD
 * represents the difference between the actual data object now, at point
 * <em>T<sub>1</sub></em>, and then, at point <em>T<sub>0</sub></em>.  The
 * computed difference must be applied to the actual data object. It is only at
 * this point that the DOD obtains an actual reference to the data object whose
 * diff it represents. If the actual data object exists, it must be retrieved.
 * If it doesn't exist, it must be created.  </p>
 *
 * <p>There are three kinds of data object for which a DOD may exist: fully
 * versioned, recoverable, and unreachable. Depending on the type of the
 * underlying data object, the DOD may go through different state
 * transitions. For the sake of brevity, we will say "versioned DOD" or
 * "unreachable DOD" instead of the more correct and verbose "DOD for a
 * versioned data object" or "DOD for an unreachable data object."</p>
 *
 * <p>The following state transition diagram formalizes allowed transitions that
 * a versioned DOD may go through.</p>
 *
 * <div align="center"><img src="doc-files/DataObjectDiff-1.png" alt="State Transition
 * Diagram for a fully versioned DOD"></div>
 *
 * <p>The state transition diagram is simpler for a recoverable DOD:</p>
 *
 * <div align="center"><img src="doc-files/DataObjectDiff-2.png" alt="State
 * Transition Diagram for a recoverable DOD"></div>
 *
 * <p>The case of an unreachable DOD is the simplest: </p>
 *
 * <div align="center"><img src="doc-files/DataObjectDiff-3.png" alt="State
 * Transition Diagram for an unreachable DOD"></div>
 *
 * <p><span style="color: FireBrick; font-weight: bold">TODO</span>: describe
 * each possible state.</p>
 *
 * <p>When a data object diff is first created and enqueued in the diff
 * set, it starts out in the ENQUEUED state. At this point, we neither know
 * nor care if the underlying data object actually exists at the present time.
 * The data object diff can go into the UNBORN state as a result of processing a
 * {@link EventType#CREATE create} event. It can go into the UNDEAD state as a
 * result of processing a {@link EventType#DELETE delete} event.</p>
 *
 * <p>Once the version controller has finished reading events from the
 * versioning log down to point <em>T<sub>0</sub></em>, data object diffs must
 * obtain references to the actual data objects whose diffs they represent.
 * This is accomplished by calling {@link #reify()} on each of data object diffs
 * enqueued in the diff set. Prior to this call, {@link #getDataObject()}
 * returns <code>null</code> and should not be used. After the data object diff
 * has been reified, a call to {@link #getDataObject()} may return the data
 * object for which this proxy is proxying. </p>
 *
 * @see DiffSet
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #18 $ $DateTime: 2004/08/16 18:10:38 $
 * @since 2003-04-16
 */
final class DataObjectDiff implements Constants {
    private final static Logger s_log = Logger.getLogger(DataObjectDiff.class);

    private OID m_oid;
    private DataObject m_dataObject;

    /** Single-valued properties */
    private Map m_properties;
    /** Multi-valued properties */
    private Map m_collections;

    private State m_state;
    private State m_postEnqueued;

    private RollbackListener m_listener;


    public DataObjectDiff(OID oid) {
        this(oid, new RollbackAdapter());
    }

    public DataObjectDiff(OID oid, RollbackListener listener) {
        m_oid = oid;
        m_properties = new HashMap();
        m_state = State.ENQUEUED;
        // redundant assignments for documentation purposes:
        m_dataObject = null;
        m_postEnqueued = null;
        m_listener = listener;
        // m_collections is instantiated lazily, because it is not
        // always needed
    }

    public OID getOID() {
        return m_oid;
    }

    public DataObject getDataObject() {
        if ( m_dataObject == null ) {
            throw new UnreifiedDataObjectException(this);
        }
        return m_dataObject;
    }

    public String getState() {
        return m_state.toString();
    }

    private Object get(String propertyName) {
        Attribute attr = (Attribute) m_properties.get(propertyName);
        if ( attr == null ) {
            throw new IllegalArgumentException
                ("no property named " + propertyName);
        }
        return attr.getValue();
    }

    private void set(String propertyName, Object value, Types type) {
        Assert.exists(propertyName, String.class);
        m_listener.onUndoSet(m_oid, propertyName, value);
        m_properties.put(propertyName, new Attribute(value, type));
    }

    private Collection collection(String propertyName) {
        if ( m_collections == null ) {
            m_collections = new HashMap();
        }

        Collection coll = (Collection) m_collections.get(propertyName);
        if ( coll == null ) {
            coll = new Collection();
            m_collections.put(propertyName, coll);
        }
        return coll;
    }

    private void add(String propertyName, String strValue)
        throws CollectionAttributeException {
        Assert.exists(propertyName, String.class);
        m_listener.onUndoRemove(m_oid, propertyName, strValue);
        try {
            collection(propertyName).add(new Attribute(strValue, Types.OID));
        } catch (CollectionAttributeException ex) {
            ex.setAttrName(propertyName);
            throw ex;
        }
    }

    private void remove(String propertyName, String strValue)
        throws CollectionAttributeException {
        Assert.exists(propertyName, String.class);
        m_listener.onUndoAdd(m_oid, propertyName, strValue);
        try {
            collection(propertyName).remove(new Attribute(strValue, Types.OID));
        } catch (CollectionAttributeException ex) {
            ex.setAttrName(propertyName);
            throw ex;
        }
    }

    public void reify() {
        checkStateBeforeReifying();

        if ( m_state == State.ENQUEUED ) {
            retrieveDataObject();
            m_state = State.REIFIED;
        } else if ( m_state == State.UNDEAD ) {
            if ( m_postEnqueued == State.UNBORN ) {
                retrieveDataObject();
            } else if ( m_postEnqueued == State.UNDEAD ) {
                createDataObject();
            } else {
                throw new AssertionError
                    ("Can't possibly get here: " + this);
            }
            m_state = State.REIFIED;
        } else if ( m_state == State.UNBORN ) {
            if ( m_postEnqueued == State.UNBORN ) {
                // According to our records, the data object, whose diff this
                // DOD represents, currently exists. It needs to be deleted.
                retrieveDataObject();
            } else if ( m_postEnqueued != State.UNDEAD ) {
                // The only way to reach UNBORN is via UNBORN or UNDEAD. If
                // m_postEnqueued is neither of the two, then we screwed up
                // somewhere along the way.
                throw new AssertionError
                    ("Can't possibly get here: " + this);
            }
            m_state = State.TERMINAL;
        } else {
            throw new AssertionError("Can't possibly happen: " + this);
        }
    }

    private void checkStateBeforeReifying() throws AssertionError {
        if ( m_state == State.REIFIED || m_state == State.TERMINAL ) {
            throw new AssertionError("Can't possibly happen: " + this);
        }

        if ( m_state == State.ENQUEUED && m_postEnqueued != null ) {
            throw new AssertionError
                ("m_state is ENQUEUED and m_postEnqueued is not null. What gives? " +
                 this);
        }

        if (m_state != State.ENQUEUED
            && m_postEnqueued != State.UNBORN
            && m_postEnqueued != State.UNDEAD) {

            throw new AssertionError
                ("m_state is not ENQUEUED and m_postState is " + m_postEnqueued +
                 "; this=" + this);
        }
    }

    private void retrieveDataObject() {
        m_dataObject = SessionManager.getSession().retrieve(getOID());
        Assert.exists(m_dataObject, DataObject.class);
    }

    private void createDataObject() {
        m_listener.onCreate(getOID());
        m_dataObject = SessionManager.getSession().create(getOID());
        Assert.truth(m_dataObject.isNew(), getOID() + " is new");
    }

    /**
     * Finds all non-null compound attributes and converts oids to the
     * corresponding data objects.
     *
     * @param rec the rollback record in the context of which this data object
     * diff is being computed.
     **/
    public void reifyCompoundAttributes(DiffSet diffSet) {
        Iterator values = m_properties.values().iterator();
        while ( values.hasNext() ) {
            Attribute attr = (Attribute) values.next();
            if ( attr.isCompound() ) {
                attr.reify(diffSet);
            }
        }

        if ( m_collections == null ) return;

        Iterator colls = m_collections.values().iterator();
        while ( colls.hasNext() ) {
            Collection coll = (Collection) colls.next();
            for ( Iterator attrs = coll.attributes(); attrs.hasNext(); ) {
                Attribute attr = (Attribute) attrs.next();
                attr.reify(diffSet);
            }
        }
    }


    /**
     * If this data object diff has properties whose values are {@link
     * com.arsdigita.persistence.OID oids}, this method returns an iterator over
     * those oids.
     **/
    public Iterator compoundAttributes() {
        List li = new LinkedList();
        Iterator values = m_properties.values().iterator();
        while ( values.hasNext() ) {
            Attribute attr = (Attribute) values.next();
            if ( attr.isCompound() ) {
                li.add(attr.getValue());
            }
        }

        if ( m_collections == null ) return li.iterator();

        Iterator colls = m_collections.values().iterator();
        while ( colls.hasNext() ) {
            Collection coll = (Collection) colls.next();
            for ( Iterator oids = coll.oids(); oids.hasNext(); ) {
                li.add(oids.next());
            }
        }

        return li.iterator();
    }


    // -= VARIOUS UNDO METHODS =-

    public void undoEvent(DataObject op) {
        BigInteger opID = (BigInteger) op.get(ID);
        EventType ev = EventType.getEventType((DataObject) op.get("eventType"));

        // polymorphic dispatch is overrated.
        switch ( ev.intValue() ) {
        case EventType.CREATE_SWITCH:
            undoCreate(op); break;
        case EventType.DELETE_SWITCH:
            undoDelete(op); break;
        case EventType.ADD_SWITCH:
            undoAdd(op); break;
        case EventType.REMOVE_SWITCH:
            undoRemove(op); break;
        case EventType.SET_SWITCH:
            undoSet(op); break;
        default:
            throw new AssertionError("can't get here: " + this);
        }
    }

    private void undoCreate(DataObject op) {
        if (m_state != State.ENQUEUED && m_state != State.UNDEAD ) {
            throw new AssertionError("Can't possibly happen: " + this);
        }

        if ( m_state == State.ENQUEUED ) {
            m_postEnqueued = State.UNBORN;
        }
        m_state = State.UNBORN;
    }

    private void undoDelete(DataObject op) {
        if ( m_state != State.ENQUEUED && m_state != State.UNBORN ) {
            throw new AssertionError("Can't possibly happen: " + this);
        }

        if ( m_state == State.ENQUEUED ) {
            m_postEnqueued = State.UNDEAD;
        }
        m_state = State.UNDEAD;
    }

    private void undoAdd(DataObject op) {
        try {
            remove((String) op.get(ATTRIBUTE), (String) op.get(VALUE));
        } catch (CollectionAttributeException ex) {
            ex.setOperation(op);
            throw ex;
        }
    }

    private void undoRemove(DataObject op) {
        try {
            add((String) op.get(ATTRIBUTE), (String) op.get(VALUE));
        } catch (CollectionAttributeException ex) {
            ex.setOperation(op);
            throw ex;
        }
    }

    private void undoSet(DataObject op) {
        String attr = (String) op.get(ATTRIBUTE);
        Object value = op.get(VALUE);
        Types type = Types.getType((DataObject) op.get(JAVACLASS));
        set(attr, value, type);
    }

    void diff(DataObjectDiff dobjDiff, Difference diff) {
        State st;
        if (dobjDiff == null) {
            st = State.ENQUEUED;
        } else {
            st = dobjDiff.m_state;
        }

        Difference.Change change;

        if (m_state == State.UNBORN && st == State.UNDEAD) {
            change = diff.create(m_oid);
        } else if (m_state == State.UNBORN && st == State.UNBORN) {
            return;
        } else if (m_state == State.UNBORN && st == State.ENQUEUED) {
            change = diff.create(m_oid);
        } else if (m_state == State.UNDEAD && st == State.UNBORN) {
            change = diff.delete(m_oid);
        } else if (m_state == State.UNDEAD && st == State.UNDEAD) {
            return;
        } else if (m_state == State.UNDEAD && st == State.ENQUEUED) {
            change = diff.delete(m_oid);
        } else if (m_state == State.ENQUEUED && st == State.ENQUEUED) {
            change = diff.modify(m_oid);
        } else {
            throw new IllegalStateException("bad state old: " + m_state +
                                            ", new: " + st);
        }

        for (Iterator it = m_properties.keySet().iterator(); it.hasNext(); ) {
            String prop = (String) it.next();
            change.setFrom(prop, get(prop));
        }

        if (dobjDiff != null) {
            for (Iterator it = dobjDiff.m_properties.keySet().iterator();
                 it.hasNext(); ) {
                String prop = (String) it.next();
                change.setTo(prop, dobjDiff.get(prop));
            }
        }

        if (m_collections == null) {
            diff.addChange(change);
            return;
        }

        Iterator colls = m_collections.entrySet().iterator();
        while (colls.hasNext()) {
            Map.Entry collEntry = (Map.Entry) colls.next();
            String propertyName = (String) collEntry.getKey();
            Collection coll = (Collection) collEntry.getValue();

            java.util.Collection added = change.getAdded(propertyName);
            java.util.Collection removed = change.getRemoved(propertyName);
            for (Iterator it = coll.added(); it.hasNext(); ) {
                Attribute attr = (Attribute) it.next();
                added.add(attr.getValue());
            }
            for (Iterator it = coll.removed(); it.hasNext(); ) {
                Attribute attr = (Attribute) it.next();
                removed.add(attr.getValue());
            }

            if (dobjDiff != null
                && dobjDiff.m_collections != null
                && dobjDiff.m_collections.containsKey(propertyName)) {
                Collection dobjDiffColl =
                    (Collection) dobjDiff.m_collections.get(propertyName);
                for (Iterator it = dobjDiffColl.added(); it.hasNext(); ) {
                    Attribute attr = (Attribute) it.next();
                    added.remove(attr.getValue());
                }
                for (Iterator it = dobjDiffColl.removed(); it.hasNext(); ) {
                    Attribute attr = (Attribute) it.next();
                    removed.remove(attr.getValue());
                }
            }
        }

        diff.addChange(change);
    }

    /**
     * Applies this data object diff to the underlying data object.
     **/
    public void apply() {
        if ( m_state == State.UNBORN ) {
            m_state = State.PROCESSED;
            return;
        }

        if ( m_state == State.TERMINAL ) return;

        DataObject dobj = getDataObject();

        // change the single-valued properties
        Iterator props = m_properties.keySet().iterator();
        while ( props.hasNext() ) {
            String prop = (String) props.next();
            Object value = get(prop);
            dobj.set(prop, value);
        }

        if ( m_collections == null ) {
            m_state = State.PROCESSED;
            return;
        }

        Iterator colls = m_collections.entrySet().iterator();
        while ( colls.hasNext() ) {
            Map.Entry collEntry = (Map.Entry) colls.next();
            String propertyName = (String) collEntry.getKey();
            Collection coll = (Collection) collEntry.getValue();
            if ( coll.size() == 0 ) continue;

            DataAssociation da = (DataAssociation) dobj.get(propertyName);
            for (Iterator added = coll.added(); added.hasNext(); ) {
                Attribute attr = (Attribute) added.next();
                da.add((DataObject) attr.getValue());
            }
            for (Iterator removed =coll.removed(); removed.hasNext(); ) {
                Attribute attr = (Attribute) removed.next();
                da.remove((DataObject) attr.getValue());
            }
        }
        m_state = State.PROCESSED;
    }

    public void deleteIfTerminal() {
        if ( m_state == State.TERMINAL ) {
            m_listener.onDelete(getOID());
            m_dataObject.delete();
            m_state = State.PROCESSED;
            return;
        }

        if ( m_state != State.PROCESSED ) {
            throw new AssertionError("Can't possibly happen: " + this);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(LINE_SEP);
        sb.append(getOID()).append(LINE_SEP);
        sb.append("properties=").append(LINE_SEP);
        sb.append(functions.prettyString(m_properties));
        sb.append(LINE_SEP);
        sb.append("collections=").append(LINE_SEP);
        sb.append(functions.prettyStringUnsorted(m_collections));
        sb.append(LINE_SEP);
        sb.append("state: ");
        sb.append(m_state);
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if ( obj == null ) return false;

        DataObjectDiff that = (DataObjectDiff) obj;
        return getOID().equals(that.getOID());
    }

    public int hashCode() {
        return getOID().hashCode();
    }

    /**
     * An attribute has a type, serialized value, and a deserialized one.
     **/
    private static class Attribute {
        private Types m_type;
        private Object m_serializedValue;
        private Object m_deserializedValue;

        /**
         * @param serValue the serialized value of the attribute
         * @param type the attribute type
         **/
        public Attribute(Object serValue, Types type) {
            m_serializedValue = serValue;
            m_type = type;
        }

        /**
         * Returns the deserialized value of the attribute. Note that for
         * compound non-reified attributes this returns an {@link
         * com.arsdigita.persistence.OID}.  Once the compound attributed has
         * been {@link #reify(DiffSet) reified}, this method returns a
         * {@link com.arsdigita.persistence.DataObject}.
         **/
        public Object getValue() {
            if ( Types.VOID.equals(m_type) ) {
                return null;
            }

            if ( Types.BLOB.equals(m_type) ) {
                return m_serializedValue;
            }

            if ( m_deserializedValue == null ) {
                m_deserializedValue =
                    Adapter.deserialize((String) m_serializedValue, m_type);
            }
            return m_deserializedValue;
        }

        public void reify(DiffSet diffSet) {
            m_deserializedValue = diffSet.get((OID) getValue()).getDataObject();
        }

        public boolean isCompound() {
            return Types.OID.equals(m_type);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(m_type).append(":").append(LINE_SEP);
            sb.append("serialized=").append(m_serializedValue).
                append(LINE_SEP);
            sb.append("deserialized=").append(getValue());
            return sb.toString();
        }
    }


    // Not to be confused with java.util.Collection.  Couldn't think of a better
    // name.
    private static class Collection {
        Map m_added;
        Map m_removed;

        public Collection() {
            m_added   = new HashMap();
            m_removed = new HashMap();
        }

        public void add(Attribute attr) throws CollectionAttributeException {
            Assert.truth(attr.isCompound(), "attr.isCompound(): " + attr);
            OID oid = (OID) attr.getValue();
            if ( m_added.containsKey(oid) ) {
                throw new CollectionAttributeException(attr);
            }

            if ( m_removed.containsKey(oid) ) {
                m_removed.remove(oid);
            } else {
                m_added.put(oid, attr);
            }
        }

        public Iterator added() {
            return m_added.values().iterator();
        }

        public void remove(Attribute attr) throws CollectionAttributeException {
            Assert.truth(attr.isCompound(), "attr.isCompound(): " + attr);
            OID oid = (OID) attr.getValue();
            if ( m_removed.containsKey(oid) ) {
                throw new CollectionAttributeException(attr);
            }

            if ( m_added.containsKey(oid) ) {
                m_added.remove(oid);
            } else {
                m_removed.put(oid, attr);
            }
        }

        public Iterator removed() {
            return m_removed.values().iterator();
        }

        public Iterator oids() {
            List result = new LinkedList();
            result.addAll(m_added.keySet());
            result.addAll(m_removed.keySet());
            return result.iterator();
        }

        public Iterator attributes() {
            List result = new LinkedList();
            result.addAll(m_added.values());
            result.addAll(m_removed.values());
            return result.iterator();
        }

        public int size() {
            return m_added.size() + m_removed.size();
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("added=").append(LINE_SEP);
            sb.append(functions.prettyStringUnsorted(m_added));
            sb.append("removed=").append(LINE_SEP);
            sb.append(functions.prettyStringUnsorted(m_removed));
            return sb.toString();
        }
    }

    private static class State {
        String m_name;

        private State() {}

        public final static State ENQUEUED    = new State("enqueued");
        public final static State UNBORN      = new State("unborn");
        public final static State UNDEAD      = new State("undead");
        public final static State REIFIED     = new State("reified");
        public final static State TERMINAL    = new State("terminal");
        public final static State PROCESSED   = new State("processed");

        private State(String name) {
            m_name = name;
        }

        public String toString() {
            return m_name;
        }
    }

    private static class CollectionAttributeException extends VersioningException {
        private final Attribute m_attr;
        private String m_attrName;
        private DataObject m_op;

        public CollectionAttributeException(Attribute attr) {
            m_attr = attr;
        }

        public void setAttrName(String attrName) {
            m_attrName = attrName;
        }

        public void setOperation(DataObject op) {
            m_op = op;
        }

        public String getMessage() {
            StringBuffer sb = new StringBuffer(100);
            sb.append(m_attrName);
            sb.append(" has already been processed.");
            sb.append(LINE_SEP).append("attr: ").append(m_attr);
            sb.append(LINE_SEP).append("operation: ");
            sb.append(functions.prettyString(m_op));
            return sb.toString();
        }
    }

    private static class UnreifiedDataObjectException extends VersioningException {
        private DataObjectDiff m_dobjDiff;

        public UnreifiedDataObjectException(DataObjectDiff dobjDiff) {
            super();
            m_dobjDiff = dobjDiff;
        }

        public String getMessage() {
            return "Not reified: " + m_dobjDiff;
        }
    }
}
