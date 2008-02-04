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
package com.redhat.persistence.engine.rdbms;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.WrappedError;
import com.redhat.persistence.DataSet;
import com.redhat.persistence.Engine;
import com.redhat.persistence.Event;
import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.QuerySource;
import com.redhat.persistence.RecordSet;
import com.redhat.persistence.SQLWriterException;
import com.redhat.persistence.SetEvent;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Query;
import com.redhat.persistence.oql.Size;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #22 $ $Date: 2004/08/16 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: RDBMSEngine.java 1327 2006-09-25 08:10:48Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);

    public static final String OPTION_WINDOW_SIZE = "option:window_size";

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);
    private Event m_event = null;
    private HashMap m_environments = new HashMap();
    private ArrayList m_mutations = new ArrayList();
    private ArrayList m_mutationTypes = new ArrayList();

    private static int s_windowSize =
        RuntimeConfig.getConfig().getResultSetWindowSize();

    private ConnectionSource m_source;
    private Connection m_conn = null;
    private int m_connUsers = 0;

    private SQLWriter m_writer;
    private RDBMSProfiler m_profiler;

    private Map m_options;

    public RDBMSEngine(ConnectionSource source, SQLWriter writer) {
        this(source, writer, null);
    }

    public RDBMSEngine(ConnectionSource source, SQLWriter writer,
                       RDBMSProfiler profiler) {
        m_source = source;
        m_writer = writer;
        m_profiler = profiler;

        m_writer.setEngine(this);
    }

    public Connection getConnection() {
        acquire();
        return m_conn;
    }

    void acquire() {
        if (m_conn == null) {
            m_conn = m_source.acquire();
        }
        m_connUsers++;
    }

    void release() {
        if (m_conn == null) {
            return;
        }

        m_connUsers--;

        if (m_connUsers == 0) {
            m_source.release(m_conn);
            m_conn = null;
        }
    }

    void releaseAll() {
        if (m_conn != null) {
            m_source.release(m_conn);
            m_conn = null;
            m_connUsers = 0;
        }
    }

    void addOperation(Object obj, DML dml) {
        Object key = new CompoundKey(obj, dml.getTable());
        if (dml instanceof Delete) {
            DML prev = (DML) m_operationMap.get(key);
            if (prev != null) {
        	removeOperation(obj, prev);
            }
        }
        m_operationMap.put(key, dml);
        addOperation(dml);
    }

    void removeOperation(Object obj, DML dml) {
        Object key = new CompoundKey(obj, dml.getTable());
        m_operationMap.remove(key);
        m_operations.remove(dml);
    }

    DML getOperation(Object obj, Table table) {
        Object key = new CompoundKey(obj, table);
        DML result = (DML) m_operationMap.get(key);
        if (m_profiler != null && result != null) {
            result.addEvent(m_event);
        }
        return result;
    }

    void clearUpdates(Object obj) {
        m_operationMap.remove(obj);
    }

    void removeUpdates(Object obj) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing updates for: " + obj);
        }
        ArrayList ops = (ArrayList) m_operationMap.get(obj);
        if (ops != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("found: " + ops);
            }
            for (Iterator it = ops.iterator(); it.hasNext(); ) {
                Operation op = (Operation) it.next();
        	if (op instanceof DML) {
        	    removeOperation(obj, (DML) op);
        	} else {
        	    m_operations.remove(op);
        	}
                it.remove();
            }
        }
        clearUpdates(obj);
    }

    void markUpdate(Object obj) {
        if (!hasUpdates(obj)) {
            m_operationMap.put(obj, new ArrayList());
        }
    }

    void markUpdate(Object obj, Operation op) {
        markUpdate(obj);
        ArrayList ops = (ArrayList) m_operationMap.get(obj);
        ops.add(op);
    }

    boolean hasUpdates(Object obj) {
        return m_operationMap.containsKey(obj);
    }

    void addOperation(Operation op) {
        if (m_profiler != null) {
            op.addEvent(m_event);
        }
        m_operations.add(op);
    }

    Environment getEnvironment(Object obj) {
        Environment result = (Environment) m_environments.get(obj);
        if (result == null) {
            result = new Environment(this, getSession().getObjectMap(obj));
            m_environments.put(obj, result);
        }
        return result;
    }

    void scheduleMutation(SetEvent e, int type) {
        m_mutations.add(e);
        m_mutationTypes.add(new Integer(type));
    }

    void clear() {
        m_aggregator.clear();
        clearOperations();
        m_mutations.clear();
        m_mutationTypes.clear();
    }

    void clearOperations() {
        m_operationMap.clear();
        m_operations.clear();
        m_environments.clear();
    }

    protected void commit() {
        acquire();
        try {
            m_conn.commit();
        } catch (SQLException e) {
            throw new WrappedError(e);
        } finally {
            releaseAll();
        }
    }

    protected void rollback() {
        acquire();
        try {
            m_conn.rollback();
        } catch (SQLException e) {
            throw new WrappedError(e);
        } finally {
            releaseAll();
            clear();
        }
    }

    public RecordSet execute(Signature sig, Expression expr) {
        return execute(sig, expr, Collections.EMPTY_MAP);
    }

    public RecordSet execute(Signature sig, Expression expr, Map options) {
        m_options = options;
        Select sel = new Select(this, sig, expr);

        if (LOG.isInfoEnabled()) {
            LOG.info("Executing " + sel.getQuery());
        }

        return new RDBMSRecordSet(sig, this, execute(sel));
    }

    public long size(Expression expr) {
        Query q = new Query(new Size(expr));
        Select sel = new Select(this, q);

        if (LOG.isInfoEnabled()) {
            LOG.info("Executing " + sel.getQuery());
        }

        ResultCycle rc = execute(sel);
        if (rc == null) {
            throw new IllegalStateException
                ("null result set");
        }
        ResultSet rs = rc.getResultSet();
        StatementLifecycle cycle = rc.getLifecycle();
        try {
            long result;
            if (rc.next()) {
                try {
                    if (cycle != null) { cycle.beginGet("1"); }
                    result = rs.getLong(1);
                    if (cycle != null) { cycle.endGet(new Long(result)); }
                } catch (SQLException e) {
                    if (cycle != null) { cycle.endGet(e); }
                    throw new RDBMSException(e.getMessage()) {};
                }
            } else {
                throw new IllegalStateException("count returned no rows");
            }

            if (rc.next()) {
                throw new IllegalStateException
                    ("count returned too many rows");
            }

            if (LOG.isInfoEnabled()) {
                LOG.info("size = " + result);
            }
            return result;
        } finally {
            rc.close();
        }
    }

    private Aggregator m_aggregator = new Aggregator();

    public void write(Event ev) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(ev);
        }
        ev.dispatch(m_aggregator);
    }

    private void generate() {
        Collection nodes = m_aggregator.getNodes();
        HashSet generated = new HashSet();
        int before;
        do {
            before = generated.size();

            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Node nd = (Node) it.next();
                if (generated.containsAll(nd.getDependencies())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Generating node: " + nd);
                    }
                    int ops = m_operations.size();
                    Collection events = nd.getEvents();
                    for (Iterator iter = events.iterator(); iter.hasNext(); ) {
                        Event ev = (Event) iter.next();
                        if (generated.contains(ev)) {
                            throw new IllegalStateException
                                ("event generated twice: " + ev);
                        }
                        m_event = ev;
                        ev.dispatch(m_switch);
                        m_event = null;
                        generated.add(ev);
                    }
                    for (int i = ops; i < m_operations.size(); i++) {
                        Operation op = (Operation) m_operations.get(i);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("GENERATED: " + op);
                            LOG.debug("ENV: " + op.getEnvironment());
                        }
                    }
                    it.remove();
                    m_operationMap.clear();
                    m_environments.clear();
                }
            }
        } while (generated.size() > before);

        if (nodes.size() > 0) {
            StringBuffer msg = new StringBuffer();
            msg.append("unable to generate all events:");
            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Node nd = (Node) it.next();
                msg.append("\n\nnode {");
                msg.append("\n  events {");
                for (Iterator iter = nd.getEvents().iterator();
                     iter.hasNext(); ) {
                    msg.append("\n    ");
                    msg.append(iter.next());
                }
                msg.append("\n  }\n");
                msg.append("\n  unresolved dependencies {");
                for (Iterator iter = nd.getDependencies().iterator();
                     iter.hasNext(); ) {
                    Event ev = (Event) iter.next();
                    if (!generated.contains(ev)) {
                        msg.append("\n    ");
                        msg.append(ev);
                    }
                }
                msg.append("\n  }");
                msg.append("\n}");
            }
            throw new IllegalStateException(msg.toString());
        }
    }

    public void flush() {
        try {
            generate();

            for (Iterator it = m_operations.iterator(); it.hasNext(); ) {
                Operation op = (Operation) it.next();
                it.remove();
                ResultCycle rc = execute(op);
                if (rc != null) { rc.close(); }
            }

            for (int i = 0; i < m_mutations.size(); i++) {
                SetEvent e = (SetEvent) m_mutations.get(i);
                int jdbcType = ((Integer) m_mutationTypes.get(i)).intValue();
                Property prop = e.getProperty();
                DataSet ds = getSession().getDataSet(e.getObject(), prop);
                QuerySource qs = getSession().getQuerySource();
                RDBMSRecordSet rs = (RDBMSRecordSet) execute
                    (ds.getSignature(), ds.getExpression());
                Adapter ad = prop.getRoot().getAdapter(prop.getType());
                try {
                    if (rs.next()) {
                        ad.mutate(rs.getResultSet(),
                                  rs.getColumn(Path.get(prop.getName())),
                                  e.getArgument(),
                                  jdbcType);
                    } else {
                        throw new IllegalStateException
                            ("cannot update blob");
                    }
                } catch (SQLException se) {
                    LOG.error("This is the error" + se.getMessage(), se);
                    throw new WrappedError(se);
                } finally {
                    rs.close();
                }
            }
        } finally {
            clear();
        }
    }

    private ResultCycle execute(Operation op) {
        return execute(op, m_writer);
    }

    private ResultCycle execute(Operation op, SQLWriter w) {
        try {
            try {
                w.write(op);
            } catch(RDBMSException re) {
                w.clear();
                LOG.warn("failed operation: " + op);
                throw re;
            } catch (SQLWriterException ex) {
                throw new UncheckedWrapperException
                    ("failed operation: " + op.toSafeString(), ex);
            }

            String sql = w.getSQL();

            if (LOG.isInfoEnabled()) {
                logQueryDetails(Priority.INFO, sql, w, op);
            }

            if (sql.equals("")) {
                return null;
            }

            acquire();

            StatementLifecycle cycle = null;
            if (m_profiler != null) {
                RDBMSStatement stmt = new RDBMSStatement(sql);
                if (op instanceof Select) {
                    // XXX: better way of profiling
                    stmt.setSignature(((Select) op).getSignature());
                }
                for (Iterator it = op.getEvents().iterator(); it.hasNext(); ) {
                    stmt.addEvent((Event) it.next());
                }
                cycle = m_profiler.getLifecycle(m_conn, stmt);
            }

            PreparedStatement ps;

            try {
                if (cycle != null) { cycle.beginPrepare(); }
                ps = m_conn.prepareStatement(sql);
                if (cycle != null) { cycle.endPrepare(); }
            } catch (SQLException e) {
                if (cycle != null) { cycle.endPrepare(e); }
                logQueryDetails(Priority.ERROR, sql, w, op, e);
                throw new RDBMSException(e.getMessage()) {};
            }

            w.bind(ps, cycle);

            try {
                if (cycle != null) { cycle.beginExecute(); }
                int windowSize = 0;
                if (m_options != null
                    &&  m_options.containsKey(OPTION_WINDOW_SIZE)) {
                    windowSize = ( (Integer) m_options.get(OPTION_WINDOW_SIZE)).intValue();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Overridden WINDOW_SIZE for query to: " + windowSize);
                    }
                } else {
                    windowSize = s_windowSize;
                }
                if (windowSize > 0) {
                    ps.setFetchDirection(ResultSet.FETCH_FORWARD);
                    ps.setFetchSize(windowSize);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PreparedStatement ps is " + ps.toString());
                }
                if (ps.execute()) {
                    if (cycle != null) { cycle.endExecute(0); }
                    return new ResultCycle(this, ps.getResultSet(), cycle);
                } else {
                    int updateCount = ps.getUpdateCount();
                    if (cycle != null) { cycle.endExecute(updateCount); }

                    if (LOG.isInfoEnabled()) {
                        LOG.info(updateCount + " rows affected");
                    }

                    try {
                        if (cycle != null) { cycle.beginClose(); }
                        ps.close();
                        if (cycle != null) { cycle.endClose(); }
                    } catch (SQLException e) {
                        if (cycle != null) { cycle.endClose(e); }
                        logQueryDetails(Priority.ERROR, sql, w, op, e);
                        throw new RDBMSException(e.getMessage()) {};
                    }

                    return null;
                }
            } catch (SQLException e) {
                if (cycle != null) { cycle.endExecute(e); }
                logQueryDetails(Priority.ERROR, sql, w, op, e);
                release();
                throw new RDBMSException(e.getMessage()) {};
            } catch (RuntimeException e) {
                logQueryDetails(Priority.ERROR, sql, w, op, e);
                throw e;
            }
        } finally {
            w.clear();
        }
    }

    private void logQueryDetails(final Priority priority, final String sql, final SQLWriter w, final Operation op) {
        logQueryDetails(priority, sql, w, op, null);
    }

    private void logQueryDetails(final Priority priority, final String sql, final SQLWriter w, final Operation op, final Throwable error) {
        if (error == null) {
            LOG.log(priority, sql);
        } else {
            LOG.log(priority, sql, error);
        }
        LOG.log(priority, w.getBindings());
        LOG.log(priority, w.getTypeNames());
        LOG.log(priority, op.getEnvironment());
    }

    public void execute(SQLBlock sql, Map parameters) {
        Environment env = new Environment(this, null);
        for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            env.set((Path) me.getKey(), me.getValue());
        }
        Operation op = new StaticOperation(this, sql, env, false);
        SQLWriter w = new RetainUpdatesWriter();
        w.setEngine(this);
        execute(op, w);
    }

    private HashMap collToMap(Collection c) {
        Iterator iter = c.iterator();
        HashMap map = new HashMap();
        for (int i=1; iter.hasNext(); i++) {
            map.put(new Integer(i), iter.next());
        }

        return map;
    }

    static final Path[] getKeyPaths(ObjectType type, Path prefix) {
        return getPaths(type, prefix, false);
    }

    static final Path[] getImmediatePaths(ObjectType type, Path prefix) {
        return getPaths(type, prefix, true);
    }

    private static final Path[] getPaths(ObjectType type, Path prefix,
                                 boolean immediate) {
        LinkedList result = new LinkedList();
        LinkedList stack = new LinkedList();
        stack.add(prefix);

        while (stack.size() > 0) {
            Path p = (Path) stack.removeLast();

            ObjectType ot = type.getType(Path.relative(prefix, p));
            Collection props;
            if (immediate) {
                props = ot.getImmediateProperties();
            } else {
                props = ot.getKeyProperties();
            }
            if (props.size() == 0) {
                result.add(p);
                continue;
            }

            ArrayList revProps = new ArrayList(props.size());
            revProps.addAll(props);
            Collections.reverse(revProps);

            for (Iterator it = revProps.iterator(); it.hasNext(); ) {
                Property key = (Property) it.next();
                stack.add(Path.add(p, key.getName()));
            }
        }

        return (Path[]) result.toArray(new Path[0]);
    }

    Object get(Object obj, Path path) {
        if (path == null) {
            return obj;
        }

        Object o = get(obj, path.getParent());
        if (o == null) {
            return null;
        }

        PropertyMap props = getSession().getProperties(o);
        return props.get(props.getObjectType().getProperty(path.getName()));
    }

    final static int getType(Root root, Object obj) {
        if (obj == null) {
            return Types.INTEGER;
        } else {
            return getType(root, obj.getClass());
        }
    }

    final static int getType(Root root, Class klass) {
        return root.getAdapter(klass).defaultJDBCType();
    }

}
