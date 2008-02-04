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
package com.redhat.persistence.oql;

import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.Classes;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.jdbc.Connections;
import com.arsdigita.xml.XML;
import com.redhat.persistence.Engine;
import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.QuerySource;
import com.redhat.persistence.Session;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.engine.rdbms.ConnectionSource;
import com.redhat.persistence.engine.rdbms.PostgresWriter;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Constraint;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.pdl.PDL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * QuerySuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class QuerySuite extends TestSuite {

    public final static String versionId = "$Id: QuerySuite.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public QuerySuite() {}

    public QuerySuite(Class theClass) {
        super(theClass);
    }

    public QuerySuite(String name) {
        super(name);
    }

    private Root m_root = null;
    private Connection m_conn = null;

    public Root getRoot() {
        return m_root;
    }

    public Connection getConnection() {
        return m_conn;
    }

    private void init() {
        m_root = new Root();
        m_conn = Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());
        DbHelper.setDatabase(DbHelper.getDatabase(m_conn));

        PDL pdl = new PDL();
        pdl.loadResource("com/redhat/persistence/oql/test.pdl");
        pdl.emit(m_root);

        m_constraints = new ArrayList();
        Collection tables = m_root.getTables();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            for (Iterator iter = table.getConstraints().iterator();
                 iter.hasNext(); ) {
                Constraint con = (Constraint) iter.next();
                if (con.isDeferred()) { m_constraints.add(con); }
            }
        }
    }

    private Collection m_constraints = null;

    private void setup() throws SQLException {
        if (m_root == null) { init(); }
        Statement stmt = m_conn.createStatement();

        Collection tables = m_root.getTables();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            String sql = table.getSQL();
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage() + "\n\n" + sql);
            }
        }

        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            stmt.execute("alter table " + con.getTable().getName() +
                         " add " + con.getSQL());
        }
        stmt.close();

        ConnectionSource src = new ConnectionSource() {
            public Connection acquire() { return m_conn; }
            public void release(Connection conn) {}
        };
        Engine engine = new RDBMSEngine(src, new PostgresWriter());
        Session ssn = new Session(m_root, engine, new QuerySource());

        DataLoader loader = new DataLoader(ssn);
        XML.parseResource("com/redhat/persistence/oql/data.xml", loader);
        ssn.flush();
    }

    private boolean commit = false;

    private void teardown() throws SQLException {
        if (m_root == null) { init(); }
        Statement stmt = m_conn.createStatement();
        try {
            for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
                Constraint con = (Constraint) it.next();
                try {
                    stmt.execute("alter table " + con.getTable().getName() +
                                 " drop constraint " + con.getName());
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }

            Collection tables = m_root.getTables();
            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                try {
                    stmt.execute("drop table " + table.getName());
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        } finally {
            stmt.close();
            if (commit) {
                m_conn.commit();
            } else {
                m_conn.rollback();
            }
            m_conn.close();
            m_constraints = null;
            m_conn = null;
            m_root = null;
        }
    }

    public static Test suite() {
        final QuerySuite suite = new QuerySuite();
        TestLoader loader = new TestLoader(suite);
        XML.parseResource("com/redhat/persistence/oql/queries.xml", loader);
        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws SQLException {
                suite.setup();
            }
            protected void tearDown() throws SQLException {
                suite.teardown();
            }
        };
        return wrapper;
    }

    private static class DataLoader extends DefaultHandler {

        private Session m_ssn;
        private ObjectType m_type = null;
        private Object m_obj = null;

        public DataLoader(Session ssn) {
            m_ssn = ssn;
        }

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals("data")) {
                return;
            } else if (m_type != null) {
                Property prop = m_type.getProperty(name);
                if (prop == null) {
                    throw new IllegalStateException
                        ("no such property: " + name);
                }
                Map values = values(attrs);
                Object value = decode(prop.getType(), null, values);
                m_ssn.add(m_obj, prop, value);
            } else {
                m_type = m_ssn.getRoot().getObjectType(name);
                if (m_type == null) {
                    throw new IllegalStateException("no such type: " + name);
                }

                Map values = values(attrs);

                m_obj = decode(m_type, null, values);
                Collection props = m_type.getProperties();
                for (Iterator it = props.iterator(); it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    if (prop.isCollection()) { continue; }
                    Object value = decode
                        (prop.getType(), Path.get(prop.getName()), values);
                    m_ssn.set(m_obj, prop, value);
                }
            }
        }

        public void endElement(String uri, String name, String qn) {
            if (m_type != null && name.equals(m_type.getQualifiedName())) {
                m_type = null;
                m_obj = null;
            }
        }

        private Map values(Attributes attrs) {
            Map values = new HashMap();
            for (int i = 0; i < attrs.getLength(); i++) {
                Path path = Path.get(attrs.getLocalName(i));
                values.put(path, attrs.getValue(i));
            }
            return values;
        }

        private static Class[] STRING = new Class[] { String.class };

        private Object decode(ObjectType type, Path key, Map values) {
            Class klass = type.getJavaClass();
            Adapter ad = m_ssn.getRoot().getAdapter(klass);

            Collection props = type.getKeyProperties();

            if (props.isEmpty()) {
                Object value = values.get(key);
                if (value == null) {
                    return null;
                } else {
                    return Classes.newInstance
                        (klass, STRING, new Object[] { value });
                }
            } else {
                PropertyMap pmap = new PropertyMap(type);
                for (Iterator it = props.iterator(); it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    Object value = decode
                        (prop.getType(), Path.add(key, prop.getName()),
                         values);
                    pmap.put(prop, value);
                }
                if (pmap.isNull()) {
                    return null;
                } else {
                    Object obj = m_ssn.retrieve(pmap);
                    if (obj == null) {
                        obj = ad.getObject(type, pmap);
                        m_ssn.create(obj);
                    }
                    return obj;
                }
            }
        }

    }

    private static class TestLoader extends DefaultHandler {

        private QuerySuite m_suite;
        private String m_name = null;
        private List m_tests = null;
        private List m_fetched = null;
        private String m_variant = null;
        private boolean m_ordered = false;
        private StringBuffer m_query = null;
        private Integer m_subselectCount = null;
        private Integer m_joinCount = null;
        private Integer m_innerCount = null;
        private Integer m_outerCount = null;
        private List m_results = null;
        private ExpectedError m_error = null;

        public TestLoader(QuerySuite suite) {
            m_suite = suite;
        }

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals("test")) {
                m_tests = new ArrayList();
                m_fetched = new ArrayList();
                m_name = attrs.getValue(uri, "name");
            } else if (name.equals("query")) {
                m_query = new StringBuffer();
                for (int i = 0; i < attrs.getLength(); i++) {
                    String attr = attrs.getLocalName(i);
                    String value = attrs.getValue(i);
                    if (attr.equals("name")) {
                        m_variant = value;
                    } else if (attr.equals("ordered")) {
                        m_ordered = "true".equalsIgnoreCase(value);
                    } else if (attr.equals("subselects")) {
                        m_subselectCount = new Integer(value);
                    } else if (attr.equals("joins")) {
                        m_joinCount = new Integer(value);
                    } else if (attr.equals("inners")) {
                        m_innerCount = new Integer(value);
                    } else if (attr.equals("outers")) {
                        m_outerCount = new Integer(value);
                    } else if (attr.equals("fetched")) {
                        m_fetched.add(value);
                    } else {
                        throw new IllegalStateException
                            ("unrecognized attribute for query: " + attr);
                    }
                }
            } else if (name.equals("results")) {
                m_results = new ArrayList();
            } else if (name.equals("row")) {
                Map row = new HashMap();
                for (int i = 0; i < attrs.getLength(); i++) {
                    String value = attrs.getValue(i);
                    if (value.equals("null")) { value = null; }
                    row.put(attrs.getLocalName(i), value);
                }
                m_results.add(row);
            } else if (name.equals("exception")) {
                String type = attrs.getValue("type");
                String msg = attrs.getValue("msg");
                m_error = new ExpectedError(type, msg);
            }
        }

        public void characters(char[] ch, int start, int length) {
            if (m_query != null) {
                m_query.append(ch, start, length);
            }
        }

        public void endElement(String uri, String name, String qn) {
            if (name.equals("query")) {
                String query = m_query.toString();
                m_query = null;
                String tname = m_variant == null ? query : m_variant;
                if (m_name != null) {
                    tname = m_name + "[" + tname + "]";
                }
                QueryTest test =
                    new QueryTest(m_suite, tname, query, m_ordered);
                m_ordered = false;
                test.setSubselectCount(m_subselectCount);
                m_subselectCount = null;
                test.setJoinCount(m_joinCount);
                m_joinCount = null;
                test.setInnerCount(m_innerCount);
                m_innerCount = null;
                test.setOuterCount(m_outerCount);
                m_outerCount = null;
                m_tests.add(test);
                if (m_fetched.size() < m_tests.size()) {
                    m_fetched.add(null);
                }
            } else if (name.equals("results")) {
                // do nothing
            } else if (name.equals("test")) {
                for (int i = 0; i < m_tests.size(); i++) {
                    QueryTest test = (QueryTest) m_tests.get(i);
                    String fetched = (String) m_fetched.get(i);
                    test.setResults(filter(m_results, fetched));
                    test.setError(m_error);
                    m_suite.addTest(test);
                }
                m_tests = null;
                m_fetched = null;
                m_results = null;
                m_error = null;
            }
        }

    }

    private static List filter(List rows, String fetched) {
        if (fetched == null) { return rows; }

        Set fetchSet = new HashSet();
        String[] parts = StringUtils.split(fetched, ',');
        for (int i = 0; i < parts.length; i++) {
            fetchSet.add(parts[i]);
        }

        List result = new ArrayList(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            Map row = (Map) rows.get(i);
            Map filtered = new HashMap();
            for (Iterator it = row.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                if (fetchSet.contains(me.getKey())) {
                    filtered.put(me.getKey(), me.getValue());
                }
            }
            result.add(filtered);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        QuerySuite suite = new QuerySuite();
        suite.commit = true;
        String cmd = "setup";
        if (args.length > 0) {
            cmd = args[0];
        }
        if (cmd.equals("setup")) {
            suite.setup();
        } else if (cmd.equals("teardown")) {
            suite.teardown();
        } else {
            System.err.println("unknown command: " + cmd);
            return;
        }
        Connection conn = suite.getConnection();
        if (conn != null) { conn.commit(); }
    }

}
