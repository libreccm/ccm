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
package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Alias;
import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.oql.All;
import com.redhat.persistence.oql.And;
import com.redhat.persistence.oql.Define;
import com.redhat.persistence.oql.Equals;
import com.redhat.persistence.oql.Exists;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Filter;
import com.redhat.persistence.oql.Get;
import com.redhat.persistence.oql.Join;
import com.redhat.persistence.oql.Literal;
import com.redhat.persistence.oql.Variable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * A Session object provides the primary means for client Java code to
 * interact with the persistence layer. This code is either Java code using
 * the persistence layer to implement object persistence, or Java code working
 * with persistent objects.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #18 $ $Date: 2004/08/16 $
 **/

public class Session {

    public final static String versionId = "$Id: Session.java 1393 2006-11-28 09:12:32Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static final Logger LOG = Logger.getLogger(Session.class);

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    // XXX: link attribute hack
    public static final String LINK_ASSOCIATION = "assoc";

    private final Root m_root;
    private final Engine m_engine;
    private final QuerySource m_qs;

    private HashMap m_odata = new HashMap();

    private EventStream m_events = new EventStream(this);

    private Set m_violations = new HashSet();

    private final Set m_beforeDelete = new HashSet();
    private final Set m_afterActivate = new HashSet();
    private final Set m_beforeFlush = new HashSet();
    private final Set m_afterFlush = new HashSet();

    private Event m_beforeFlushMarker = null;

    public Session(Root root, Engine engine, QuerySource source) {
        m_root = root;
        m_engine = engine;
        m_engine.setSession(this);
        m_qs = source;
        m_qs.setSession(this);
    }

    public Root getRoot() {
        return m_root;
    }

    Engine getEngine() {
        return m_engine;
    }

    public QuerySource getQuerySource() {
        return m_qs;
    }

    EventStream getEventStream() { return m_events; }

    public Object retrieve(PropertyMap keys) {
        ObjectData odata = getObjectData(keys);

        if (odata != null) {
            if (odata.isDeleted()) { return null; }

            ObjectType requested = keys.getObjectType();
            ObjectType current = getObjectType(odata.getObject());

            // this duplicates logic in RecordSet.load [ashah]
            if (current.isSubtypeOf(requested)) {
                return odata.getObject();
            } else if (!requested.isSubtypeOf(current)) {
                return null;
            }
        }

        Cursor c = getDataSet(keys).getCursor();
        if (c.next()) {
            Object result = c.get();
            if (c.next()) {
                throw new IllegalStateException
                    ("query returned more than one row");
            }
            return result;
        } else {
            return null;
        }
    }

    public Object get(final Object obj, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("get", new Object[] {obj, prop.getName()});
        }

        final Object[] result = {null};

        if (!getObjectType(obj).hasKey()) {
            result[0] = getProperties(obj).get(prop);
        } else {
            prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    PropertyData pd = fetchPropertyData(obj, role);
                    result[0] = pd.get();
                }

                public void onAlias(Alias alias) {
                    result[0] = get(obj, alias.getTarget());
                }

                public void onLink(Link link) {
		    DataSet ds = getDataSet(obj, link);
                    if (!link.isCollection()) {
                        Cursor c = ds.getCursor();
                        if (c.next()) {
                            result[0] = c.get(LINK_ASSOCIATION);

                            if (c.next()) {
                                throw new IllegalStateException
                                    ("Link query returned too many rows");
                            }
                        }
                    } else {
                        result[0] = POS.getPersistentCollection
                            (Session.this, ds);
                    }
                }
            });
        }

        if (LOG.isDebugEnabled()) {
            untrace("get", result[0]);
        }

        return result[0];
    }


    public DataSet getDataSet(ObjectType type) {
        Expression objs = new All(type.getQualifiedName());
        Signature sig = new Signature(type);
        return new DataSet(this, sig, objs);
    }

    public DataSet getDataSet(PropertyMap keys) {
        ObjectType type = keys.getObjectType();
        Expression expr = new All(type.getQualifiedName());
        Signature sig = new Signature(type);

        ObjectMap map = type.getRoot().getObjectMap(type);
        Collection keyProps = map.getKeyProperties();

        Expression filter = null;

        for (Iterator it = keyProps.iterator(); it.hasNext(); ) {
            Property keyProp = (Property) it.next();
            Object key = keys.get(keyProp);
            Expression propFilt = new Equals
                (new Variable(keyProp.getName()), new Literal(key));

            if (filter == null) {
                filter = propFilt;
            } else {
                filter = new And(filter, propFilt);
            }
        }

        expr = new Filter(expr, filter);
        return new DataSet(this, sig, expr);
    }

    // These should probably be changed to take type signatures.
    public DataSet getDataSet(Object obj) {
        ObjectType type = getObjectType(obj);
        Signature sig = new Signature(type);
        Expression objs = new Define(new All(type.getQualifiedName()), "this");
        Expression filter = new Equals(new Variable("this"), new Literal(obj));
        Expression expr = new Get(new Filter(objs, filter), "this");
        // XXX: Expression expr = new Literal(obj);
        return new DataSet(this, sig, expr);
    }

    public DataSet getDataSet(final Object obj, Property prop) {
        final DataSet[] result = new DataSet[1];
        prop.dispatch(new Property.Switch() {
            public void onRole(Role role) {
                result[0] = getDataSet(obj, role);
            }

            public void onAlias(Alias alias) {
                throw new Error("not implemented yet");
            }

            public void onLink(Link link) {
                result[0] = getDataSet(obj, link);
            }
        });

        return result[0];
    }

    private DataSet getDataSet(Object obj, Role role) {
        ObjectType propType = role.getType();
        DataSet ds = getDataSet(obj);
        if (propType.isKeyed()) {
            Signature sig = new Signature(propType);
            Expression expr = ds.getExpression();
            if (!role.isCollection()) {
                expr = new Filter
                    (expr, new Exists(new Variable(role.getName())));
            }
            expr = new Get(expr, role.getName());
            return new DataSet(this, sig, expr);
        } else {
            ds.getSignature().addPath(role.getName());
            return ds;
        }
    }

    private DataSet getDataSet(Object obj, Link link) {
	return getDataSet(obj, link, null);
    }

    private DataSet getDataSet(Object obj, Link link, Object value) {
        // join(link = all(linkType), all(targetType),
        //      target == link.target)
        ObjectType targetType = link.getTo().getType();
        Expression targets = new Define
            (new All(targetType.getQualifiedName()), LINK_ASSOCIATION);
        Expression links = new Define
            (new All(link.getLinkType().getQualifiedName()), "link");

        Variable linkVar = new Variable("link");

        Expression cond = null;
        for (Iterator it = targetType.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property key = (Property) it.next();
            String name = key.getName();
            Expression e = new Equals
                (new Get(new Variable(LINK_ASSOCIATION), name),
                 new Get(new Get(linkVar, link.getTo().getName()), name));
            if (cond == null) {
                cond = e;
            } else {
                cond = new And(e, cond);
            }
        }
        Expression expr = new Join(targets, links, cond);
        Expression from = new Literal(obj);
        expr = new Filter(expr, new Equals
                          (new Get(linkVar, link.getFrom().getName()), from));

        Signature sig = new Signature();
        sig.addSource(targetType, Path.get(LINK_ASSOCIATION));
        sig.addSource(link.getLinkType(), Path.get("link"));

	if (value != null) {
	    Expression to = new Literal(value);
            expr = new Filter
                (expr, new Equals(new Variable(LINK_ASSOCIATION) , to));
	}

        return new DataSet(this, sig, expr);
    }

    Object get(Object start, Path path) {
        Object value;

        if (path.getParent() == null) {
            value = start;
        } else {
            value = get(start, path.getParent());
        }

        ObjectType type = getObjectType(start);
        Property prop = type.getProperty(path.getName());
        if (prop == null) {
            throw new IllegalArgumentException
                (value + ": no such property: " + path.getName());
        }

        return get(start, prop);
    }


    public void create(Object obj) {
        try {
            if (LOG.isDebugEnabled()) {
                trace("create", new Object[] { obj });
            }
            Expander e = new Expander(this);
            e.expand(new CreateEvent(this, obj));
            activate(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("create");
            }
        }
    }

    public boolean delete(Object obj) {
        try {
            if (LOG.isDebugEnabled()) {
                trace("delete", new Object[] { obj });
            }

            if (isDeleted(obj)) {
                return false;
            }

            Expander e = new Expander(this);
            e.expand(new DeleteEvent(this, obj));
            activate(e);
            return true;
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("delete");
            }
        }
    }

    public void set(final Object obj, Property prop, final Object value) {
        try {
            if (LOG.isDebugEnabled()) {
                trace("set", new Object[] { obj, prop, value });
            }

            final Expander e = new Expander(this);

            prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    e.expand(new SetEvent(Session.this, obj, role, value));
                }

                public void onAlias(Alias alias) {
                    e.expand(new SetEvent
                             (Session.this, obj, alias.getTarget(), value));
                }

                public void onLink(Link link) {
                    Object oldValue = get(obj, link);
                    PropertyMap pmap = new PropertyMap(link.getLinkType());
                    pmap.put(link.getFrom(), obj);

                    if (oldValue != null) {
                        pmap.put(link.getTo(), oldValue);
                        e.expand(new DeleteEvent
                                 (Session.this, getObject(pmap)));
                    }

                    pmap.put(link.getTo(), value);
                    e.expand(new CreateEvent(Session.this, getObject(pmap)));
                }
            });

            activate(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("set");
            }
        }
    }

    public Object add(final Object obj, Property prop, final Object value) {
	final Object[] result = { null };

        try {
            if (LOG.isDebugEnabled()) {
                trace("add", new Object[] { obj, prop, value });
            }

            final Expander e = new Expander(this);

            prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    e.expand(new AddEvent(Session.this, obj, role, value));
                }

                public void onAlias(Alias alias) {
                    e.expand(new AddEvent
                             (Session.this, obj, alias.getTarget(), value));
                }

                public void onLink(Link link) {
                    PropertyMap pmap = new PropertyMap(link.getLinkType());
                    pmap.put(link.getFrom(), obj);
                    pmap.put(link.getTo(), value);
                    result[0] = getObject(pmap);
                    e.expand(new CreateEvent(Session.this, result[0]));
                }
            });

            activate(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("add", result[0]);
            }
        }

        return result[0];
    }

    public void remove(final Object obj, Property prop, final Object value) {
        try {
            if (LOG.isDebugEnabled()) {
                trace("remove", new Object[] { obj, prop, value } );
            }
            Expander e = new Expander(this);
            remove(obj, prop, value, e);
            activate(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("remove");
            }
        }
    }


    private void remove(final Object obj, Property prop, final Object value,
                        final Expander e) {
        prop.dispatch(new Property.Switch() {
            public void onRole(Role role) {
                e.expand(new RemoveEvent(Session.this, obj, role, value));
            }

            public void onAlias(Alias alias) {
                e.expand(new RemoveEvent
                         (Session.this, obj, alias.getTarget(), value));
            }

            public void onLink(Link link) {
                Cursor c = getDataSet(obj, link, value).getCursor();
                while (c.next()) {
                    e.expand(new DeleteEvent(Session.this, c.get("link")));
                }
            }
        });
    }

    public void clear(Object obj, Property prop) {
        try {
            if (LOG.isDebugEnabled()) {
                trace("clear", new Object[] { obj, prop });
            }
            final Expander e = new Expander(this);
            PersistentCollection pc = (PersistentCollection) get(obj, prop);
            Cursor c = pc.getDataSet().getCursor();
            while (c.next()) {
                remove(obj, prop, c.get(), e);
            }
            activate(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("clear");
            }
        }
    }

    public Object getObject(PropertyMap pmap) {
	Adapter ad = m_root.getAdapter(pmap.getObjectType());
	return ad.getObject(pmap.getObjectType(), pmap);
    }

    public ObjectType getObjectType(Object obj) {
        return getAdapter(obj).getObjectType(obj);
    }

    Adapter getAdapter(Object obj) {
        return m_root.getAdapter(obj.getClass());
    }

    public ObjectMap getObjectMap(Object obj) {
        ObjectType type = getAdapter(obj).getObjectType(obj);
        return type.getRoot().getObjectMap(type);
    }

    public PropertyMap getProperties(Object obj) {
        return getAdapter(obj).getProperties(obj);
    }

    /**
     * @return true iff this session has outstanding events
     **/
    public boolean isFlushed() {
        return m_events.size() == 0;
    }

    public boolean isNew(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isNew();
    }

    public boolean isDeleted(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isDeleted();
    }

    public boolean isModified(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isModified();
    }

    public boolean isFlushed(Object obj) {
        if (!hasObjectData(obj)) {
            return true;
        }

        ObjectData od = getObjectData(obj);
        return od.isFlushed();
    }

    public void assertFlushed(Object obj) {
        if (!isFlushed(obj)) {
            // could derive which violations are relevant for this object
            throw new FlushException(obj, m_violations);
        }
    }

    public boolean isFlushed(final Object obj, Property prop) {
	if (!hasObjectData(obj)) {
	    return true;
	}

	final boolean[] result = { true };

	prop.dispatch(new Property.Switch() {
		public void onRole(Role r) {
		    PropertyData pd = getObjectData(obj).getPropertyData(r);
		    if (pd == null) {
			result[0] = true;
		    } else {
			result[0] = pd.isFlushed();
		    }
		}

		public void onAlias(Alias a) {
		    result[0] = isFlushed(obj, a.getTarget());
		}

		public void onLink(Link l) {
		    result[0] = isFlushed(obj, l.getFrom().getReverse());
		}
	    });

	return result[0];
    }

    public boolean isPersisted(Object obj) {
        return hasObjectData(obj) && !getObjectData(obj).isInfantile();
    }

    private static final Integer s_zero = new Integer(0);

    private void process(Collection processors, Collection events) {
        for (Iterator it = processors.iterator(); it.hasNext(); ) {
            EventProcessor ep = (EventProcessor) it.next();

            if( LOG.isDebugEnabled() ) {
                trace( "Event processor: " + ep.getClass().getName(), events.toArray() );
            }

            for (Iterator it2 = events.iterator(); it2.hasNext(); ) {
                Event ev = (Event) it2.next();
                ep.write(ev);
            }

            ep.flush();

            if( LOG.isDebugEnabled() ) {
                untrace( "Event processor: " + ep.getClass().getName() );
            }
        }
    }

    private void computeFlushability() {
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            ((Event) it.next()).m_flushable = true;
        }

        // find unflushable events
        // the source of unflushability is not null properties that
        // have not been set
        List queue = new LinkedList();
        for (Iterator pds = m_violations.iterator(); pds.hasNext(); ) {
            PropertyData pd = (PropertyData) pds.next();
            for (Iterator evs = pd.getDependentEvents(); evs.hasNext(); ) {
                queue.add(evs.next());
            }
        }

        // recursively mark reachable events as unflushable
        while (queue.size() != 0) {
            Event ev = (Event) queue.remove(0);
            if (ev.m_flushable) {
                ev.m_flushable = false;
                for (Iterator evs = ev.getDependentEvents(); evs.hasNext(); ) {
                    queue.add(evs.next());
                }
            }
        }
    }

    /**
     * Performs all operations queued up by the session. This is automatically
     * called when necessary in order to insure that queries performed by the
     * datastore are consistent with the contents of the in memory data cache.
     **/
    public void flush() {
        try {
            if (LOG.isDebugEnabled()) {
                trace("flush", new Object[] {});
            }

            flushInternal();
        } finally {
            if (LOG.isDebugEnabled()) {
                untrace("flush");
            }
        }
    }

    public void flushAll() {
        flush();
        if (!isFlushed()) {
            throw new FlushException(m_violations);
        }
    }

    private void flushInternal() {
        boolean flushed = m_beforeFlushMarker != null;
        List events = m_events.getEvents();
        for (int i = 0; i < events.size(); i++) {
            Event ev = (Event) events.get(i);
            if (flushed) {
                if (m_beforeFlushMarker.equals(ev)) { flushed = false; }
                continue;
            }

            m_beforeFlushMarker = ev;

            for (Iterator eps = m_beforeFlush.iterator(); eps.hasNext(); ) {
                EventProcessor ep = (EventProcessor) eps.next();
                ep.write(ev);
            }
        }

        for (Iterator it = m_beforeFlush.iterator(); it.hasNext(); ) {
            EventProcessor ep = (EventProcessor) it.next();
            ep.flush();
        }

        List written = new LinkedList();

        Event prev = null;
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            if (ev.m_flushable) {
                m_engine.write(ev);
                written.add(ev);
                it.remove();
                if (ev.equals(m_beforeFlushMarker)) {
                    m_beforeFlushMarker = prev;
                }
            } else {
                prev = ev;
            }
        }

        m_engine.flush();

        for (Iterator it = written.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            ev.sync();
        }

        computeFlushability();

        process(m_afterFlush, written);

        // If we still have events left and there aren't any errors then an
        // after flush event created a new, unflushed event. Try flushing
        // again
        if( m_violations.size() == 0 && m_events.size() > 0 ) flush();

        if (LOG.isInfoEnabled()) {
            if (m_events.size() > 0) {
                LOG.info("unflushed: " + m_events.size());
            }
        }

        if (LOG.isDebugEnabled()) {
            if (m_events.size() > 0) {
                LOG.debug("events: " + m_events.getEvents());
                LOG.debug("violations: " + m_violations);
            }
        }
    }

    private void clear(boolean isCommit) {
        m_odata.clear();
        m_events.clear();
        m_violations.clear();
        m_beforeFlushMarker = null;
        if (LOG.isDebugEnabled()) { setLevel(0); }
        cleanUpEventProcessors(isCommit);
    }

    /**
     * Renders all changes made within the transaction permanent and ends the
     * transaction.
     **/

    public void commit() {
        boolean success = false;
        try {
            flushAll();
            clear(true);
            m_engine.commit();
            success = true;
        } finally {
            if (!success) { clear(false); }
        }
    }

    /**
     * This does not actually commit the transaction, but does the rest of the
     * work associated with commit.
     */
    void testCommit() {
        boolean success = false;
        try {
            flushAll();
            clear(true);
            success = true;
        } finally {
            if (!success) { clear(false); }
        }
    }

    private void cleanUpEventProcessors(boolean isCommit) {
        for (Iterator eps = m_afterActivate.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.cleanUp(isCommit);
        }
        for (Iterator eps = m_beforeFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.cleanUp(isCommit);
        }
        for (Iterator eps = m_afterFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.cleanUp(isCommit);
        }
    }

    /**
     * Reverts all changes made within the transaction and ends the
     * transaction.
     **/

    public void rollback() {
        try {
            m_engine.rollback();
        } finally {
            clear(false);
        }
    }

    void load(Object obj, Property prop, Object value) {
        if (LOG.isDebugEnabled()) {
            trace("load", new Object[] {obj, prop.getName(), value});
        }

        ObjectData od = getObjectData(obj);
        if (od == null) {
            od = new ObjectData(this, obj, od.NUBILE);
        }

        PropertyData pd = od.getPropertyData(prop);
        if (pd == null) {
            pd = new PropertyData(od, prop, value);
        } else {
            pd.setValue(value);
        }

        if (LOG.isDebugEnabled()) {
            untrace("load");
        }
    }

    private void activate(Expander e) {
        List pending = e.finish();
        if (e.didDelete()) {
            process(m_beforeDelete, pending);
        }

        List activated = new ArrayList();
        for (Iterator it = pending.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            if (e.didDelete()) {
                ObjectData od = getObjectData(ev.getObject());
                if (od != null && od.isDeleted()) {
                    continue;
                }

                if (ev instanceof PropertyEvent) {
                    PropertyEvent pev = (PropertyEvent) ev;
                    Object arg = pev.getArgument();
                    if (arg != null) {
                        ObjectData pod = getObjectData(arg);
                        if (pod != null && pod.isDeleted()) {
                            continue;
                        }
                    }
                }
            }

            ev.activate();
            activated.add(ev);
        }

        computeFlushability();

        process(m_afterActivate, activated);
    }

    Object getSessionKey(PropertyMap pmap) {
        ObjectType ot = pmap.getObjectType();
        Adapter ad = m_root.getAdapter(ot);
        return ad.getSessionKey(ot, pmap);
    }

    Object getSessionKey(Object obj) {
        Adapter ad = getAdapter(obj);
        return ad.getSessionKey(obj);
    }

    /**
     * Forces this session to release references to the specified object from
     * its internal caches. Events that point to this object continue to do
     * so.
     *
     * @param obj the object to release. It should not be nul
     **/
    public void releaseObject(Object obj) {
        ObjectData od = getObjectData(obj);
        if (od != null && od.getObject() == obj) {
            Adapter ad = getAdapter(obj);
            ObjectType type = ad.getObjectType(obj);
            Object newObj = ad.getObject(type, ad.getProperties(obj));
            use(newObj);
        }
    }

    void use(Object obj) {
        Adapter ad = getAdapter(obj);
        ObjectType type = ad.getObjectType(obj);
        if (type.isKeyed()) { ad.setSession(obj, this); }

        ObjectData odata = getObjectData(obj);
        if (odata != null) {
            odata.setObject(obj);
        }
    }

    Object getObject(Object key) {
        if (m_odata.containsKey(key)) {
            return ((ObjectData) m_odata.get(key)).getObject();
        } else {
            return null;
        }
    }

    boolean hasObjectData(Object obj) {
        return m_odata.containsKey(getSessionKey(obj));
    }

    ObjectData getObjectData(PropertyMap pmap) {
        return (ObjectData) m_odata.get(getSessionKey(pmap));
    }

    ObjectData getObjectData(Object obj) {
        return (ObjectData) m_odata.get(getSessionKey(obj));
    }

    void removeObjectData(Object obj) {
        m_odata.remove(getSessionKey(obj));
    }

    void addObjectData(ObjectData odata) {
        m_odata.put(getSessionKey(odata.getObject()), odata);
    }

    PropertyData getPropertyData(Object obj, Property prop) {
        ObjectData od = getObjectData(obj);
        if (od != null) {
            return od.getPropertyData(prop);
        } else {
            return null;
        }
    }

    ObjectData fetchObjectData(Object obj) {
        if (!hasObjectData(obj)) {
            RecordSet rs = getDataSet(obj).getCursor().execute();
            // Cache non-existent objects
            if (!rs.next()) {
                m_odata.put(obj, null);
            } else {
                do {
                    rs.load(this);
                } while (rs.next());
            }
        }

        ObjectData od = getObjectData(obj);
        if (od != null && od.isDeleted()) {
            return null;
        } else {
            return od;
        }
    }

    PropertyData fetchPropertyData(Object obj, Property prop) {
        ObjectData od = fetchObjectData(obj);
        if (od == null) {
            throw new IllegalArgumentException("No such object: " + obj);
        }

        PropertyData pd;
        if (od.hasPropertyData(prop)) {
            pd = od.getPropertyData(prop);
        } else if (prop.isCollection()) {
            pd = new PropertyData(od, prop, POS.getPersistentCollection
                                  (this, getDataSet(obj, prop)));
        } else if (od.isInfantile()){
            pd = new PropertyData(od, prop, null);
        } else {
            RecordSet rs = getDataSet(obj, prop).getCursor().execute();
            Map values = null;
            if (rs.next()) {
                values = rs.load(this);

                if (rs.next()) {
                    throw new IllegalStateException
                        ("Query returned too many rows");
                }
            }

            if (prop.getType().isKeyed()) {
		if (values == null) {
		    load(obj, prop, null);
		} else {
		    load(obj, prop, values.get(null));
		}
            } else if (values == null) {
                throw new MetadataException
                    (prop.getRoot(), prop,
                     "Query for: " + prop.getContainer() + "." + prop
                     + " failed to return rows" );
	    }

            pd = od.getPropertyData(prop);
            if (pd == null) {
                throw new IllegalStateException
                    ("Query failed to retrieve property");
            }
        }

        return pd;
    }

    void addViolation(PropertyData pd) { m_violations.add(pd); }

    void removeViolation(PropertyData pd) { m_violations.remove(pd); }

    private void check(EventProcessor ep) {
        if (ep == null) {
            throw new IllegalArgumentException("null event processor");
        }
    }

    /**
     * Before delete event processors are sent events associated with deletes
     * and removes that cause deletes. They have some constraints. It is
     * illegal to add objects to roles that are being deleted by the set of
     * events being activated. In other words, don't put an object in a
     * position in the object graph that would imply that it should be deleted
     * as a consequence of any delete event being activated.
     */
    public void addBeforeDelete(EventProcessor ep) {
        check(ep);
        m_beforeDelete.add(ep);
    }

    public void addAfterActivate(EventProcessor ep) {
        check(ep);
        m_afterActivate.add(ep);
    }

    public void addBeforeFlush(EventProcessor ep) {
        check(ep);
        m_beforeFlush.add(ep);
    }

    public void addAfterFlush(EventProcessor ep) {
        check(ep);
        m_afterFlush.add(ep);
    }

    void dump() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        dump(pw);
        pw.flush();
        LOG.debug(sw.toString());
    }

    void dump(PrintWriter out) {
        for (Iterator it = m_odata.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();

            Object key = me.getKey();
            out.print(key);
            out.println(":");

            ObjectData od = (ObjectData) me.getValue();
            od.dump(out);
        }
    }

    private static final ThreadLocal LEVEL = new ThreadLocal() {
            protected Object initialValue() {
                return new Integer(0);
            }
        };

    static final int getLevel() {
        Integer level = (Integer) LEVEL.get();
        return level.intValue();
    }

    static final void setLevel(int level) {
        if (level < 0) { level = 0; }
        LEVEL.set(new Integer(level));
    }

    static final void trace(String method, Object[] args) {
        if (LOG.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();
            int level = getLevel();
            for (int i = 0; i < level; i++) {
                msg.append("  ");
            }
            msg.append(method);
            msg.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    msg.append(", ");
                }
                msg.append(args[i]);
            }
            msg.append(") {");
            LOG.debug(msg.toString());

            setLevel(level + 1);
        }
    }

    static final void untrace(String method) {
        untrace(method, null);
    }

    static final void untrace(String method, boolean result) {
        untrace(method, result ? Boolean.TRUE : Boolean.FALSE);
    }

    static final void untrace(String method, Object result) {
        if (LOG.isDebugEnabled()) {
            untrace(method, " -> " + result);
        }
    }

    static final void untrace(String method, String msg) {
        if (LOG.isDebugEnabled()) {
            int level = getLevel();
            setLevel(level - 1);

            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < level - 1; i++) {
                buf.append("  ");
            }
            buf.append("} (");
            buf.append(method);
            buf.append(")");
            if (msg != null) {
                buf.append(msg);
            }
            LOG.debug(buf.toString());
        }
    }
}
