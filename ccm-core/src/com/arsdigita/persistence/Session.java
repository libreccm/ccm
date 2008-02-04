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
package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.arsdigita.developersupport.DeveloperSupportProfiler;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.CreateEvent;
import com.redhat.persistence.DeleteEvent;
import com.redhat.persistence.Engine;
import com.redhat.persistence.Event;
import com.redhat.persistence.EventProcessor;
import com.redhat.persistence.PropertyEvent;
import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.ProtoException;
import com.redhat.persistence.QuerySource;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.engine.rdbms.OracleWriter;
import com.redhat.persistence.engine.rdbms.PostgresWriter;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.profiler.rdbms.StatementProfiler;
import com.redhat.persistence.profiler.rdbms.CompoundProfiler;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * <p>All persistence operations take place within the context of a session.
 * The operational persistence methods operate on the object types and
 * associations defined in the Persistence Definition Langauge (PDL) files.
 * The Session object has the operational methods for creating and
 * retrieving data objects.  The APIs that operate on the PDL-defined
 * metadata are in the {@link com.arsdigita.persistence.metadata} package.
 * The Session object can be retrieved from the static
 * {@link com.arsdigita.persistence.SessionManager#getSession()} method.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #38 $ $Date: 2004/08/16 $
 * @see com.arsdigita.persistence.SessionManager
 **/
public class Session {

    private static final Logger LOG = Logger.getLogger(Session.class);

    private MetadataRoot m_root;
    private ConnectionSource m_source;
    private int m_database;
    private TransactionContext m_ctx;
    private PSession m_ssn;
    private final RDBMSEngine m_engine;
    private final QuerySource m_qs;

    private List m_dataObjects = new ArrayList();
    FlushEventProcessor m_beforeFP;
    FlushEventProcessor m_afterFP;
    private final StatementProfiler m_prof = new StatementProfiler();

    Session(MetadataRoot root, ConnectionSource source, int database) {
        m_root = root;
        m_source = source;
        m_database = database;

        com.redhat.persistence.engine.rdbms.ConnectionSource src =
            new com.redhat.persistence.engine.rdbms.ConnectionSource() {

                private Connection m_conn = null;

                public Connection acquire() {
                    m_conn = m_source.acquire(m_conn);
                    return m_conn;
                }

                public void release(Connection conn) {
                    m_source.release(conn);
                }
            };

        CompoundProfiler profs = new CompoundProfiler();
        profs.add(m_prof);
        profs.add(new DeveloperSupportProfiler());

        switch (m_database) {
        case DbHelper.DB_ORACLE:
            m_engine = new RDBMSEngine(src, new OracleWriter(), profs);
            break;
        case DbHelper.DB_POSTGRES:
            m_engine = new RDBMSEngine(src, new PostgresWriter(), profs);
            break;
        default:
            DbHelper.unsupportedDatabaseError("persistence");
            m_engine = null;
            break;
        }

        m_qs = new QuerySource();
        m_ssn = this.new PSession(m_root.getRoot(), m_engine, m_qs);
        m_ctx = new TransactionContext(this);

        m_ssn.addAfterActivate(new AfterActivate());

        m_beforeFP = new FlushEventProcessor(true);
        m_afterFP = new FlushEventProcessor(false);
        m_ssn.addBeforeFlush(m_beforeFP);
        m_ssn.addAfterFlush(m_afterFP);

        m_ssn.addBeforeDelete(new EventProcessor() {
            protected void cleanUp(boolean isCommit) { }
            protected void flush() { }
            protected void write(Event ev) {
                if (ev instanceof DeleteEvent) {
                    BeforeDeleteEvent bde =
                        new BeforeDeleteEvent((DataObjectImpl) ev.getObject());
                    bde.fire();
                }
            }
        });

        Root r = m_root.getRoot();
        synchronized (r) {
            Adapter ad = r.getAdapter(DataObjectImpl.class);
            if (ad == null ||
                !(ad instanceof DataObjectAdapter)) {
                ad = new DataObjectAdapter();
                r.addAdapter(DataObjectImpl.class, ad);
                r.addAdapter(PropertyMap.class, ad);
                r.addAdapter(null, ad);
            }
        }
    }

    public MetadataRoot getMetadataRoot() {
        return m_root;
    }

    public void startProfiling() {
        m_prof.start();
    }

    public void stopProfiling() {
        m_prof.stop();
    }

    Root getRoot() {
        return m_ssn.getRoot();
    }

    public int getDatabase() {
        return m_database;
    }

    com.redhat.persistence.Session getProtoSession() {
        return m_ssn;
    }

    RDBMSEngine getEngine() {
        return m_engine;
    }

    /**
     * Retrieves the {@link TransactionContext}
     * associated with this Session. Every Session has exactly one
     * TransactionContext object.  The transaction context can be
     * obtained as in this example:
     *   <pre>
     *   Session ssn = SessionManager.getSession();
     *   TransactionContext txn = ssn.getTransactionContext();
     *   </pre>
     *
     * The TransactionContext can be used to:
     *
     * <ul>
     *   <li>Begin a transaction.
     *     <pre>
     *     txn.beginTxn();
     *     </pre>
     *   <li>Commit a transaction.
     *     <pre>
     *     txn.commitTxn();
     *     </pre>
     *   <li>Abort a transaction.
     *     <pre>
     *     txn.abortTxn();
     *     </pre>
     *   <li>Check whether a transaction is currently in progress
     *     <pre>
     *     if (txn.inTxn()) {
     *         System.out.println("Currently in a transaction.");
     *     }
     *     </pre>
     *   <li>Check the isolation level of the current transaction.
     *     <pre>
     *     if (txn.getTransactionIsolation() ==
     *         java.sql.Connection.TRANSACTION_NONE) {
     *         System.err.println("Transaction isolation level is too low.");
     *     }
     *     </pre>
     *   <li>Set the isolation level of the current transaction.
     *     <pre>
     *  txn.setTransactionIsolation
     *     (java.sql.Connection.TRANSACTION_READ_UNCOMMITTED);
     *     </pre>
     * </ul>
     *
     * @see SessionManager
     * @see java.sql.Connection
     *
     * @return The transaction context for this Session.
     **/

    public TransactionContext getTransactionContext() {
        return m_ctx;
    }


    /**
     * Returns the JDBC connection associated with this session.
     *
     * @return The JDBC connection used by this Session object.
     **/

    public Connection getConnection() {
        return m_engine.getConnection();
    }

    /**
     * Creates and returns a DataObject of the given type. All fields of
     * this object are initially set to null, and it is not persisted until
     * {@link DataObject#save()}
     * is called.
     *
     * <p>Because of the initial null values, this method should only be
     * used for creating new objects.  The method {@link #retrieve(OID)}
     * is suitable for creating objects that will then be populated with
     * information from the database (e.g. objects that are being retrieved
     * rather than created as new).
     *
     * @param type The type of the object to be created.
     * @return A persistent object of the specified type.
     * @see #create(String)
     **/
    public DataObject create(ObjectType type) {
	if (type == null) {
	    throw new IllegalArgumentException
		("type must be non null");
	}
        DataObjectImpl result = new DataObjectImpl(type);
        result.setSession(m_ssn);
        return result;
    }


    /**
     * Creates and returns an empty DataObject of the given type. The
     * properties in the data object may then be initialized using
     * {@link DataObject#set(String,Object)}.
     * Once this is done the object may be persisted using
     * {@link DataObject#save()}.
     * An example:
     *
     *   <pre>
     *   Session ssn = SessionManager.getSession();
     *   DataObject employee = ssn.create("com.dotcom.Employee");
     *
     *   employee.set("name", "John Doughnut");
     *   employee.set("id", new BigInteger(12345));
     *   employee.set("title", "Developer");
     *
     *   employee.save();
     *   </pre>
     *
     * @see SessionManager
     *
     * @param typeName The qualified name of the type of object to be
     * created.
     *
     * @return A persistent object of the type identified by
     * <i>typeName</i>.
     **/

    public DataObject create(String typeName) {
	ObjectType type = m_root.getObjectType(typeName);
	if (type == null) {
	    throw new PersistenceException
		("no such type: " + typeName);
	}
        return create(type);
    }


    /**
     * Creates a new DataObject with the type of the given oid and initializes
     * the key properties to the values specified in the oid.
     *
     * @param oid The OID that specifies the type of and key properties for
     *        the resulting DataObject.
     **/

    public DataObject create(OID oid) {
        DataObject result = new DataObjectImpl(oid);
        try {
            m_ssn.create(result);
        } catch (ProtoException e) {
            throw PersistenceException.newInstance(e);
        }
        return result;
    }


    /**
     * Retrieves the DataObject specified by <i>oid</i>.  If there is
     * no object of the given type with the given OID, then null is returned.
     * The retrieval will be executed with the persistence mechanism
     * assoicated with this session.  Null is also returned if any
     * of the statements in the "Retrieve" event for the data object failed.
     *
     * @param oid The id of the object to be retrieved.
     *
     * @return A persistent object of the type specified by the oid.
     **/

    public DataObject retrieve(OID oid) {
        return (DataObject) m_ssn.retrieve(C.pmap(getRoot(), oid));
    }


    /**
     *  Deletes the persistent object of the given type with the given oid.
     *
     * @param oid The id of the object to be deleted.
     *
     * @return True of an object was deleted, false otherwise.
     **/

    public boolean delete(OID oid) {
        DataObject dobj = retrieve(oid);
        boolean result = m_ssn.delete(dobj);
        m_ssn.flush();
        m_ssn.assertFlushed(dobj);
        return result;
    }


    /**
     * Retrieves a collection of objects of the specified objectType.
     * This method executes the <code>retrieveAll</code> event defined
     * in the PDL and then returns a DataCollection.  This data collection
     * can be filtered and iterated over to retrieve data for the object.
     *
     * @param type The type of the persistent collection.
     * @return A DataCollection of the specified type.
     * @see Session#retrieve(String)
     **/

    public DataCollection retrieve(ObjectType type) {
        return new DataCollectionImpl
            (this, m_ssn.getDataSet(getRoot().getObjectType
                                    (type.getQualifiedName())));
    }


    /**
     * <p>Retrieves a collection of objects of the specified objectType.
     * This method executes the <code>retrieveAll</code> event defined
     * in the PDL and then returns a DataCollection.  This data collection
     * can be filtered and iterated over to retrieve data for the object.
     * </p>
     * The <code>retrieveAll</code> event can be defined as in this
     * example:
     * <pre>
     * retrieveAll {
     *   do {
     *     select *
     *     from users
     *   } map {
     *     firstName=users.first_name;
     *     lastName=users.last_name;
     *   }
     * }
     * </pre>
     *
     * From Java, you can retrieve all of the users as a DataCollection,
     * and add filters.
     *
     * <pre>
     * DataCollection allUsers = session.retrieve("users");
     * allUsers.addEqualsFilter("firstName", "Smith")
     * while (allUsers.next()) {
     *   System.out.println(allUsers.get("firstName") +
     *     allUsers.get("lastName") +
     *     allUsers.get("groupName"));
     * }
     * </pre>
     * It is also possible to instantiate a data object from a DataCollection,
     * using {@link DataCollection#getDataObject()}.
     *
     * @param typeName The qualified name of the type of the object to be
     * created.
     * @return A DataCollection populated by the specified object type's
     * <code>retrieveAll</code> event..
     * @see Session#retrieve(ObjectType)
     **/

    public DataCollection retrieve(String typeName) {
        ObjectType ot = m_root.getObjectType(typeName);
        if (ot == null) {
            throw new PersistenceException("no such type: " + typeName);
        }
        return retrieve(ot);
    }


    /**
     * <p>Retrieves a persistent query object based on the named query.
     * The query must be defined with the specified name in the
     * the PDL.</p>
     * <p>
     * DataQuery objects can be used to access fields from several data
     * objects (representing columns in separate database tables) in a
     * lightweight fashion.  The example belows show you can use a DataQuery
     * to access information about users and groups.</p>
     *
     * <pre>
     * query UsersGroups {
     * do {
     *   select *
     *   from users, groups, membership
     *   where users.user_id = membership.member_id
     *   and membership.group_id = groups.group_id
     * } map {
     *   firstName=users.first_name;
     *   lastName=users.last_name;
     *   groupName=groups.group_name;
     * }
     * </pre>
     *
     * You can use this query and filter it further. Let's say I wanted to
     * get all users whose first name is "Smith":
     *
     * <pre>
     * DataQuery query = session.retrieveQuery("UsersGroups");
     * query.addEqualsFilter("firstName", "Smith")
     * while (query.next()) {
     * System.out.println(query.get("firstName") + query.get("lastName") +
     *   query.get("groupName"));
     * }
     * </pre>
     *
     * The filter will add the necessary "where" clause to the SQL.  The
     * DataQuery can then be iterated over to display the appropriate data.
     *
     * @param name The name of the query.
     * @return A new DataQuery object.
     **/

    public DataQuery retrieveQuery(String name) {
        com.redhat.persistence.metadata.ObjectType ot
            = getRoot().getObjectType(name);
        if (ot == null) {
            throw new PersistenceException("no such query: " + name);
        }
        return new DataQueryImpl(this, m_ssn.getDataSet(ot));
    }

    /**
     * <p>
     * Retrieves a DML data operation based on the named query.
     * A DataOperation is used to perform an operation on the data,
     * such as a delete or an update.  The example belows
     * shows how it can be used to delete a set
     * of categories.</p>
     *
     *<pre>
     * data operation deleteCategories {
     *   delete from cat_categories
     *   where enabled_p = 0
     * }
     *</pre>
     * <p>
     * The data operation defined in the SQL is accessed with the
     * {@link DataOperation} object.
     *</p>
     * <pre>
     * Sessions session = SessionManager.getSession();
     * DataOperation dop = session.retreiveDataOperation("deleteCategories");
     * dop.execute();
     * </pre>
     *
     * @param name The name of the data operation defined in the PDL.
     *
     * @return A DataOperation object corresponding to the definition
     * in the PDL.
     *
     **/

    public DataOperation retrieveDataOperation(String name) {
        com.redhat.persistence.metadata.DataOperation op
            = getRoot().getDataOperation(Path.get(name));
        if (op == null) {
            throw new PersistenceException("no such data operation: " + name);
        }
        return new DataOperation(this, op.getSQL());
    }

    /**
     * Force all outstanding changes to be flushed to the database. This is
     * approximately the equivalent of calling save on every data object
     * associated with this session.
     *
     * @throws FlushException when all changes can not be flushed
     **/
    public void flushAll() {
        try {
            getProtoSession().flushAll();
        } catch (ProtoException pe) {
            throw PersistenceException.newInstance(pe);
        }
    }

    private void addDataObject(DataObject obj) {
        m_dataObjects.add(new WeakReference(obj));
    }

    void invalidateDataObjects(boolean connectedOnly, boolean error) {
        for (Iterator it = m_dataObjects.iterator(); it.hasNext(); ) {
            WeakReference ref = (WeakReference) it.next();
            DataObjectImpl obj = (DataObjectImpl) ref.get();
            if (obj != null) {
                obj.invalidate(connectedOnly, error);
            }
        }

        m_dataObjects.clear();
    }


    private static class AfterActivate extends EventProcessor {
        protected void write(Event ev) {
            if (!(ev.getObject() instanceof DataObjectImpl)) { return; }

            if (ev instanceof PropertyEvent) {
                PropertyEvent pe = (PropertyEvent) ev;
                if (pe.getProperty().getName().charAt(0) == '~') {
                    return;
                }

                DataObjectImpl doi = (DataObjectImpl) ev.getObject();
                if (doi.isDeleted()) { return; }
            }

            ev.dispatch(new Event.Switch() {
                public void onCreate(CreateEvent e) { }

                public void onDelete(DeleteEvent e) { }

                public void onSet(com.redhat.persistence.SetEvent e) {
                    new SetEvent((DataObjectImpl) e.getObject(),
                                 e.getProperty().getName(),
                                 e.getPreviousValue(),
                                 e.getArgument()).fire();
                }

                public void onAdd(com.redhat.persistence.AddEvent e) {
                    new AddEvent((DataObjectImpl) e.getObject(),
                                 e.getProperty().getName(),
                                 (DataObjectImpl) e.getArgument()).fire();
                }

                public void onRemove
                    (com.redhat.persistence.RemoveEvent e) {
                    new RemoveEvent((DataObjectImpl) e.getObject(),
                                    e.getProperty().getName(),
                                    (DataObjectImpl) e.getArgument()).fire();
                }
            });
        }

        protected void flush() { }
        protected void cleanUp(boolean isCommit) { }
    }

    static class FlushEventProcessor extends EventProcessor {
        final private boolean m_before;
        private List m_events = new ArrayList();
        private List m_toFire = new LinkedList();

        FlushEventProcessor(boolean before) { m_before = before; }

        protected void cleanUp(boolean isCommit) {
            if (isCommit && m_events.size() > 0) {
                LOG.error("unfired data events: " + m_events);
                throw new IllegalStateException
                    ("unfired data events: " + m_events);
            }
            m_events.clear();
            if (isCommit && m_toFire.size() > 0) {
                LOG.error("unfired data events: " + m_toFire);
                throw new IllegalStateException
                    ("unfired data events: " + m_toFire);
            }
            m_toFire.clear();
        }

        protected void write(Event e) {
            if (e.getObject() instanceof DataObjectImpl) {
                if (e instanceof PropertyEvent
                    && ((PropertyEvent) e).getProperty().getName().charAt(0)
                        == '~') {
                    return;
                }
                m_events.add(e);
            }
        }

        protected void flush() {
            Set objs = new HashSet();
            List events = new LinkedList();
            for (int i = m_events.size() - 1; i >= 0; i--) {
                Event e = (Event) m_events.get(i);
                DataObjectImpl obj = (DataObjectImpl) e.getObject();

                if (!objs.contains(obj)) {
                    objs.add(obj);
                    DataEvent toFire;
                    if (e instanceof DeleteEvent) {
                        if (m_before) {
                            toFire = new BeforeDeleteEvent(obj);
                        } else {
                            toFire = new AfterDeleteEvent(obj);
                        }
                    } else {
                        if (m_before) {
                            toFire = new BeforeSaveEvent(obj);
                        } else {
                            toFire = new AfterSaveEvent(obj);
                        }
                    }

                    events.add(0, toFire);
                }
            }

            m_events.clear();

            if (LOG.isDebugEnabled()) {
                if (events.size() > 0) {
                    LOG.debug((m_before ? "before flush:" : "after flush: ")
                              + events);
                }
            }

            m_toFire.addAll(events);

            for (Iterator it = events.iterator(); it.hasNext(); ) {
                DataEvent e = (DataEvent) it.next();
                if (e instanceof BeforeDeleteEvent) { continue; }
                e.schedule();
            }

            for (Iterator it = events.iterator(); it.hasNext(); ) {
                DataEvent e = (DataEvent) it.next();
                if (m_toFire.remove(e)) {
                    if (e instanceof BeforeDeleteEvent) { continue; }
                    e.fire();
                }
            }
        }

        void fireNow(DataEvent e) {
            m_toFire.remove(e);
            e.fire();
        }
    }

    private static class DataObjectAdapter extends Adapter {
        public void setSession
            (Object obj, com.redhat.persistence.Session ssn) {
            DataObjectImpl dobj = (DataObjectImpl) obj;
            dobj.setSession(ssn);
            getSessionFromProto(ssn).addDataObject(dobj);
        }

        public Object getObject
            (com.redhat.persistence.metadata.ObjectType type,
             PropertyMap props) {
            if (!type.isKeyed()) {
                return props;
            }

            com.redhat.persistence.metadata.ObjectType sp = type;

            if (type.hasProperty("objectType")) {
                Property p = type.getProperty("objectType");
                if (p.getType().getQualifiedName().equals
                    ("global.String")) {
                    String qname = (String) props.get(p);
                    Root root = type.getRoot();
                    if (qname != null && root.hasObjectType(qname)) {
                        sp = root.getObjectType(qname);
                    }
                }
            }

            MetadataRoot old = MetadataRoot.getMetadataRoot(sp.getRoot());
            OID oid = new OID(old.getObjectType(sp.getQualifiedName()));
            for (Iterator it = sp.getKeyProperties().iterator();
                 it.hasNext(); ) {
                Property prop = (Property) it.next();
                oid.set(prop.getName(), props.get(prop));
            }
            return new DataObjectImpl(oid);
        }

        public PropertyMap getProperties(Object obj) {
            if (obj instanceof PropertyMap) {
                return (PropertyMap) obj;
            }

            final DataObjectImpl dobj = (DataObjectImpl) obj;

            if (dobj.p_pMap == null) {
                final PropertyMap pMap = new PropertyMap(getObjectType(obj));
                final Iterator it =
                    dobj.getOID().getProperties().entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    pMap.put(getObjectType(obj)
                             .getProperty((String) me.getKey()), me.getValue());
                }
                dobj.p_pMap = pMap;
            }
            return dobj.p_pMap;
        }

        public com.redhat.persistence.metadata.ObjectType
            getObjectType(Object obj) {

            if (obj instanceof PropertyMap) {
                return ((PropertyMap) obj).getObjectType();
            }

            final DataObjectImpl dobj = (DataObjectImpl) obj;
            if (dobj.p_objectType== null) {
                dobj.p_objectType =
                    C.type(this.getRoot(), dobj.getObjectType());
            }

            return dobj.p_objectType;
        }
    }

    private class PSession extends com.redhat.persistence.Session {
        PSession(Root root, Engine engine, QuerySource source) {
            super(root, engine, source);
        }
        private Session getOldSession() { return Session.this; }
    }

    static Session getSessionFromProto(com.redhat.persistence.Session ssn) {
        try {
            return ((PSession) ssn).getOldSession();
        } catch (ClassCastException cce) {
            return null;
        }
    }

}
