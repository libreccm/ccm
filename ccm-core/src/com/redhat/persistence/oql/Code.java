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

import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLToken;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Constraint;
import com.redhat.persistence.metadata.JoinFrom;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.metadata.Static;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Code
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 * @version $Id: Code.java 737 2005-09-01 12:27:29Z sskracic $
 **/

public class Code {

    private static final Logger s_log = Logger.getLogger(Code.class);

    public static class Binding {

        private Object m_key;
        private Object m_value;
        private int m_type;

        Binding(Object key, Object value, int type) {
            m_key = key;
            m_value = value;
            m_type = type;
        }

        public Object getKey() {
            return m_key;
        }

        public Object getValue() {
            return m_value;
        }

        public int getType() {
            return m_type;
        }

        public int hashCode() {
            return m_value.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof Binding) {
                Binding b = (Binding) o;
                return m_value.equals(b.m_value);
            } else {
                return super.equals(o);
            }
        }

        public String toString() {
            return "(" + m_value.toString() + ": " + m_value.getClass() +
                ", " + Column.getTypeName(m_type) + ")";
        }

    }

    static final Code TRUE = new Code("1 = 1");
    static final Code FALSE = new Code("1 = 0");
    static final Code NULL = new Code("null");
    static final Code EMPTY = new Code();

    private StringBuffer m_sql;
    private int m_lower;
    private int m_upper;
    private int m_hash = 0;
    private List m_bindings;

    private Code(StringBuffer sql, List bindings) {
        m_sql = sql;
        m_bindings = bindings;
    }

    Code(String sql, List bindings) {
        m_sql = new StringBuffer();
        m_lower = 0;
        append(sql);
        m_bindings = bindings;
    }

    Code(String sql) {
        this(sql, Collections.EMPTY_LIST);
    }

    Code() {
        this("");
    }

    private String m_str = null;

    public String getSQL() {
        if (m_str == null) {
            m_str = m_sql.substring(m_lower, m_upper);
        }

        return m_str;
    }

    public List getBindings() {
        return m_bindings;
    }

    boolean isTrue() {
        return equals(TRUE);
    }

    boolean isFalse() {
        return equals(FALSE);
    }

    boolean isNull() {
        return equals(NULL);
    }

    boolean isEmpty() {
        return equals(EMPTY);
    }

    Code add(String sql) {
        Code result = new Code(m_sql, m_bindings);
        result.m_lower = m_lower;
        result.append(sql);
        return result;
    }

    Code add(Code code) {
        List bindings;
        if (m_bindings.isEmpty()) {
            bindings = code.m_bindings;
        } else if (code.m_bindings.isEmpty()) {
            bindings = m_bindings;
        } else {
            bindings = new ArrayList();
            bindings.addAll(m_bindings);
            bindings.addAll(code.m_bindings);
        }
        Code result = new Code(m_sql, bindings);
        result.m_lower = m_lower;
        result.append(code.m_sql, code.m_lower, code.m_upper);
        return result;
    }

    private void append(String str) {
        for (int i = 0; i < str.length(); i++) {
            append(str.charAt(i));
        }
        m_upper = m_sql.length();
    }

    private void append(StringBuffer src, int lower, int upper) {
        for (int i = lower; i < upper; i++) {
            append(src.charAt(i));
        }
        m_upper = m_sql.length();
    }

    private void append(char c) {
        m_hash *= 31;
        m_hash += c;
        m_sql.append(c);
    }

    private int size() {
        return m_upper - m_lower;
    }

    private char charAt(int i) {
        return m_sql.charAt(m_lower + i);
    }

    public int hashCode() {
        return m_hash ^ getBindings().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Code)) {
            return super.equals(o);
        }

        Code c = (Code) o;

        if (this == c) {
            return true;
        } else if (this.size() != c.size()) {
            return false;
        } else if (this.hashCode() != c.hashCode()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (charAt(i) != c.charAt(i)) {
                return false;
            }
        }

        return getBindings().equals(c.getBindings());
    }

    Code resolve(Map bindings, Root root) {
        Code result = new Code("", new ArrayList());
        int index = 0;
        boolean escape = false;
        for (int i = m_lower; i < m_upper; i++) {
            char c = m_sql.charAt(i);
            if (!escape && c == '?') {
                Code.Binding b = (Code.Binding) m_bindings.get(index++);
                Object value;
                int type;
                if (b.getKey() == null) {
                    value = b.getValue();
                    type = b.getType();
                } else {
                    value = bindings.get(b.getKey());
                    if (value == null) {
                        throw new IllegalStateException
                            ("no value for key: " + b.getKey());
                    }
                    Adapter ad = root.getAdapter(value.getClass());
                    type = ad.defaultJDBCType();
                }
                result.m_bindings.add(new Binding(b.getKey(), value, type));
            } else if (!escape && c == '\'') {
                escape = true;
            } else if (escape && c == '\'') {
                escape = false;
            }

            result.append(c);
        }
        result.m_upper = result.m_sql.length();
        return result;
    }

    public String toString() {
        return "<" + getSQL() + ": " + getBindings() + ">";
    }

    static Code join(Collection parts, String sep) {
        Code result = null;
        for (Iterator it = parts.iterator(); it.hasNext(); ) {
            Code part = (Code) it.next();
            if (result == null) {
                result = part;
            } else {
                result = result.add(sep).add(part);
            }
        }
        return result;
    }

    /**
     * Random static utility methods used by other oql classes.
     **/

    private static String[] names(Column[] columns, String alias) {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Column col = columns[i];
            if (alias == null) {
                result[i] = col.getName();
            } else {
                result[i] = alias + "." + col.getName();
            }
        }
        return result;
    }

    static Mapping getMapping(Property prop) {
        Root root = prop.getRoot();
        if (root == null) {
            throw new IllegalStateException("null root: " + prop);
        }
        ObjectMap om = root.getObjectMap(prop.getContainer());
        if (om == null) {
            throw new IllegalStateException("null object map: " + prop);
        }
        Mapping m = om.getMapping(Path.get(prop.getName()));
        if (m == null) {
            throw new IllegalStateException("null mapping: " + prop);
        }
        return m;
    }

    private static boolean isStaticAttribute(Mapping m) {
        if (m instanceof Static) { return true; }
        ObjectMap map = m.getObjectMap();
        if (map.getTable() == null && map.getRetrieveAll() != null) {
            return true;
        }

        return false;
    }

    static String[] columns(final Property prop, final String alias) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            return columns(prop.getType(), alias);
        }

        if (isStaticAttribute(m)) {
            ObjectMap map = m.getObjectMap();
            SQLBlock block = map.getRetrieveAll();
            if (block == null && m instanceof Static) {
                throw new MetadataException
                    (map.getRoot(), map,
                     "Specify metadata for property " + prop.getName()
                     + " in type " + prop.getContainer().getQualifiedName()
                     + " or include a retrieve all for the type");
            }
            return columns(paths(prop.getType(), m.getPath()), block);
        }

        final String[][] result = new String[][] { null };

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = names(new Column[] {v.getColumn()}, alias);
            }
            public void onJoinTo(JoinTo j) {
                result[0] = columns(j.getKey(), prop.getType(), alias);
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = columns
                    (j.getKey().getTable().getPrimaryKey(),
                     prop.getType(),
                     alias);
            }
            public void onJoinThrough(JoinThrough jt) {
                result[0] = columns(jt.getTo(), prop.getType(), alias);
            }
            public void onStatic(Static s) {
                throw new IllegalStateException();
            }
        });

        return result[0];
    }

    static String[] columns(Property prop, QFrame frame) {
        List result = new ArrayList();
        if (columns(prop, frame, null, result)) {
            return (String[]) result.toArray(new String[result.size()]);
        } else {
            return null;
        }
    }

    private static boolean columns(Property prop, QFrame frame, Path prefix,
                                   List result) {
        prefix = Path.add(prefix, prop.getName());
        Collection props = properties(prop.getType());
        if (props.isEmpty()) {
            if (frame.hasMapping(prefix)) {
                result.add(frame.getMapping(prefix));
                return true;
            } else {
                return false;
            }
        }

        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (!columns(p, frame, prefix, result)) { return false; }
        }

        return true;
    }

    static void equals(String[] left, String[] right, StringBuffer buf) {
        if (left.length != right.length) {
            throw new IllegalArgumentException
                ("left size does not match right size" +
                 "\nleft = " + Arrays.asList(left) +
                 "\nright= " + Arrays.asList(right));
        }
        for (int i = 0; i < left.length; i++) {
            buf.append(left[i]);
            buf.append(" = ");
            buf.append(right[i]);
            if (i < left.length - 1) {
                buf.append(" and ");
            }
        }
    }

    static String table(final Property prop) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            return table(prop.getType());
        }

        if (isStaticAttribute(m)) {
            return null;
        }

        final String[] result = new String[] { null };

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = v.getTable().getName();
            }
            public void onJoinTo(JoinTo j) {
                result[0] = j.getTable().getName();
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = j.getKey().getTable().getName();
            }
            public void onJoinThrough(JoinThrough jt) {
                result[0] = jt.getFrom().getTable().getName();
            }
            public void onStatic(Static s) {
                throw new IllegalStateException();
            }
        });

        return result[0];
    }

    static String table(ObjectType type) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (om.getRetrieveAll() != null) {
            return "(" + om.getRetrieveAll().getSQL() + ")";
        } else {
            return table(om).getName();
        }
    }

    /**
     * @pre map.getRetrieveAll() == null
     */
    static Table table(ObjectMap map) {
        for (ObjectMap om = map; om != null; om = om.getSuperMap()) {
            Table table = om.getTable();
            if (table != null) { return table; }
        }

        throw new MetadataException
            (map.getRoot(), map,
             "No retrieve all or mapping metadata for type "
             + map.getObjectType().getQualifiedName());
    }

    static void bind(SQL sql, Map values, StringBuffer buf) {
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            if (t.isBind()) {
                Path key = Path.get(t.getImage().substring(1));
                String value = (String) values.get(key);
                if (value == null) {
                    throw new IllegalStateException
                        ("no value for: " + key + " in " + values);
                }
                buf.append(value);
            } else {
                buf.append(t.getImage());
            }
        }
    }

    static Path[] paths(ObjectType type, Path parent) {
        ArrayList result = new ArrayList();
        paths(type, parent, result);
        return (Path[]) result.toArray(new Path[result.size()]);
    }

    static void paths(ObjectType type, Path parent, Collection result) {
        Collection props = properties(type);
        if (props.isEmpty()) {
            if (parent != null) {
                result.add(parent);
            }
            return;
        }
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            paths(prop.getType(), Path.add(parent, prop.getName()), result);
        }
    }

    static Map map(Path[] keys, String[] values) {
        Map result = new HashMap();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }

    static String[] columns(Path[] paths, SQLBlock block) {
        String[] result = new String[paths.length];
        for (int i = 0; i < result.length; i++) {
            Path column = block.getMapping(paths[i]);
            if (column == null) {
                throw new IllegalStateException
                    ("no mapping for path: " + paths[i]);
            }
            result[i] = column.getPath();
        }
        return result;
    }

    static String[] columns(ObjectType type, String alias) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (om.getRetrieveAll() != null) {
            String[] columns = columns(paths(type, null), om.getRetrieveAll());
            if (alias == null) {
                return columns;
            } else {
                return concat(alias + ".", columns);
            }
        } else {
            return names(cols(type), alias);
        }
    }

    static String[] columns(Constraint c, ObjectType type, String alias) {
        return names(cols(c, type), alias);
    }

    private static Column[] cols(Constraint c, ObjectType type) {
        Column[] cols = c.getColumns();
        Root root = type.getRoot();
        ObjectType basetype = type.getBasetype();
        ObjectMap map = root.getObjectMap(basetype);
        Column[] currentOrder = table(map).getPrimaryKey().getColumns();
        Column[] desiredOrder = cols(basetype);
        return sort(cols, currentOrder, desiredOrder);
    }

    private static Column[] cols(ObjectType type) {
        Root root = type.getRoot();
        ObjectMap map = root.getObjectMap(type);
        if (type.getSupertype() == null) {
            Collection props = map.getKeyProperties();
            final ArrayList result = new ArrayList();

            for (Iterator it = props.iterator(); it.hasNext(); ) {
                final Property prop = (Property) it.next();
                Mapping mapping = map.getMapping(Path.get(prop.getName()));
                mapping.dispatch(new Mapping.Switch() {
                    public void onValue(Value m) {
                        result.add(m.getColumn());
                    }
                    public void onJoinTo(JoinTo m) {
                        result.addAll
                            (Arrays.asList(cols(m.getKey(), prop.getType())));
                    }
                    public void onJoinFrom(JoinFrom m) {
                        throw new UnsupportedOperationException();
                    }
                    public void onJoinThrough(JoinThrough m) {
                        throw new UnsupportedOperationException();
                    }
                    public void onStatic(Static m) {
                        throw new UnsupportedOperationException();
                    }
                });
            }

            return (Column[]) result.toArray(new Column[] {});
        } else {
            return cols(table(map).getPrimaryKey(), type);
        }
    }

    private static Column[] sort(Column[] toSort,
                                 Column[] currentOrder,
                                 Column[] desiredOrder) {
        final int length = toSort.length;
        if (length != currentOrder.length || length != desiredOrder.length) {
            throw new IllegalArgumentException("length of arrays differs");
        }

        Column[] result = new Column[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (desiredOrder[i].equals(currentOrder[j])) {
                    result[i] = toSort[j];
                    break;
                }
            }
            if (result[i] == null) {
                throw new IllegalStateException();
            }
        }

        return result;
    }

    static String[] concat(String prefix, String[] strs) {
        String[] result = new String[strs.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = prefix + strs[i];
        }
        return result;
    }

    static int span(ObjectType type) {
        Collection props = properties(type);
        if (props.isEmpty()) {
            return 1;
        } else {
            int result = 0;
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                result += span(prop.getType());
            }
            return result;
        }
    }

    static Collection properties(ObjectType type) {
        Collection props = type.getKeyProperties();
        if (props.isEmpty()) {
            return type.getImmediateProperties();
        } else {
            return props;
        }
    }

}
