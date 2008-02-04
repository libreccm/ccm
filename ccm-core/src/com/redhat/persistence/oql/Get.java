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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLToken;
import com.redhat.persistence.metadata.Constraint;
import com.redhat.persistence.metadata.JoinFrom;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Qualias;
import com.redhat.persistence.metadata.Static;
import com.redhat.persistence.metadata.Value;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: Get.java 737 2005-09-01 12:27:29Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Get.class);

    private Expression m_expr;
    private String m_name;

    public Get(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = frame(gen, expr, m_name, this);
        frame.addChild(0, expr);
        Property prop = expr.getType().getProperty(m_name);
        if (!prop.isCollection()) {
            List children = frame.getChildren();
            for (int i = 1; i < children.size(); i++) {
                QFrame child = (QFrame) children.get(i);
                child.setOuter(true);
            }
        }
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_expr.hash(gen);
        gen.hash(m_name);
        gen.hash(getClass());
    }

    private static ThreadLocal s_parsers = new ThreadLocal() {
        protected Object initialValue() {
            return new OQLParser(new StringReader(""));
        }
    };

    static QFrame frame(Generator gen, QFrame expr, String name,
                        Expression result) {
        ObjectType type = expr.getType();
        Property prop = type.getProperty(name);
        if (prop == null) {
            throw new IllegalStateException("no such property: " + name);
        }
        QFrame frame = gen.frame(result, prop.getType());

        // Handle qualii
        if (prop.getRoot() != null) {
            Mapping m = Code.getMapping(prop);
            if (m instanceof Qualias) {
                Qualias q = (Qualias) m;
                String query = q.getQuery();
                OQLParser p = (OQLParser) s_parsers.get();
                p.ReInit(new StringReader(query));
                // XXX: namespace
                Expression e;
                try {
                    e = p.expression();
                } catch (ParseException pe) {
                    throw new IllegalStateException(pe.getMessage());
                }
                gen.level++;
                This ths = new This(expr);
                ths.frame(gen);
                gen.push(expr);
                gen.push(gen.getFrame(ths));
                try {
                    e.frame(gen);
                    QFrame qualias = gen.getFrame(e);
                    frame.addChild(qualias);
                    frame.setValues(qualias.getValues());
                    gen.addUses(result, frame.getValues());
                    return frame;
                } finally {
                    gen.level--;
                    gen.pop();
                    gen.pop();
                }
            }
        }

        if (expr.hasMappings()) {
            Path path = Path.get(prop.getName());
            for (Iterator it = expr.getMappings().entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                Path key = (Path) me.getKey();
                String value = (String) me.getValue();
                if (path.isAncestor(key)) {
                    frame.addMapping(Path.relative(path, key), value);
                }
            }
        }

        Collection props = Code.properties(prop.getContainer());
        if (!props.contains(prop)) {
            String[] columns = null;
            String table = null;
            if (expr.hasMappings()) {
                columns = Code.columns(prop, expr);
            }
            if (columns == null) {
                columns = Code.columns(prop, (String) null);
                table = Code.table(prop);
            }

            if (table == null) {
                QFrame stframe = ((QValue) expr.getValues().get(0)).getFrame();
                List values = new ArrayList();
                for (int i = 0; i < columns.length; i++) {
                    values.add(stframe.getValue(columns[i]));
                }
                frame.setValues(values);
            } else {
                TableAll tall = new TableAll
                    (table, columns, prop.getType().getQualifiedName());
                tall.frame(gen);
                QFrame tbl = gen.getFrame(tall);
                PropertyCondition cond =
                    new PropertyCondition(expr, prop, tbl);
                cond.frame(gen);
                tbl.setCondition(cond);
                frame.addChild(tbl);
                frame.setValues(tbl.getValues());
            }
        } else {
            int lower = 0;
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property p = (Property) it.next();
                if (p.equals(prop)) {
                    break;
                }
                lower += Code.span(p.getType());
            }
            List values = expr.getValues();
            int upper = lower + Code.span(prop.getType());
            frame.setValues(values.subList(lower, upper));
        }

        gen.addUses(result, frame.getValues());

        return frame;
    }

    private static class PropertyCondition extends Condition {

        private QFrame m_expr;
        private Property m_property;
        private QFrame m_frame;
        private This m_this;
        private Key m_key = null;

        PropertyCondition(QFrame expr, Property property, QFrame frame) {
            m_expr = expr;
            m_property = property;
            m_frame = frame;
            m_this = new This(m_expr);
            conditions();
        }

        void frame(Generator gen) {
            if (m_key == null) {
                gen.addUses(this, m_expr.getValues());
                gen.addUses(this, m_frame.getValues());
                return;
            } else {
                m_this.frame(gen);
                m_key.frame(gen);
                QFrame ths = gen.getFrame(m_this);
                QFrame key = gen.getFrame(m_key);
                gen.addUses(this, ths.getValues());
                gen.addUses(this, key.getValues());
                Equals.equate(gen, this, ths, key);
            }
        }

        Code emit(Generator gen) {
            if (m_key == null) {
                return emitStatic();
            } else {
                return Equals.emit(gen, m_this, m_key);
            }
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return toString();
        }

        private Code emitStatic() {
            Mapping m = Code.getMapping(m_property);
            List values = m_expr.getValues();
            String[] from = new String[values.size()];
            for (int i = 0; i < from.length; i++) {
                from[i] = "" + values.get(i);
            }

            Path[] paths = Code.paths
                (m_property.getType(), Path.get(m_property.getName()));
            String[] cols = Code.columns(paths, m.getRetrieve());
            Map bindings = Code.map
                (Code.paths(m_property.getContainer(), null), from);
            String[] to =
                Code.columns(m_property.getType(), m_frame.alias());

            StringBuffer in = new StringBuffer();
            in.append("(");
            for (int i = 0; i < to.length; i++) {
                in.append(to[i]);
                if (i < to.length - 1) {
                    in.append(", ");
                }
            }
            in.append(") in (");
            in(m.getRetrieve().getSQL(), cols, bindings, in);
            in.append(")");
            return new Code(in.toString());
        }

        static boolean is(SQLToken t, String image) {
            return t.getImage().trim().equalsIgnoreCase(image);
        }

        static void in(SQL sql, String[] columns, Map values,
                       StringBuffer buf) {
            SQLToken t;
            boolean select = false;
            int depth = 0;
            Map selections = new HashMap();
            StringBuffer selection = new StringBuffer();
            String prefix = null;
            for (t = sql.getFirst(); t != null; t = t.getNext()) {
                if (depth == 0 && is(t, "select")) {
                    select = true;
                } else if (depth == 0 && select &&
                           (is(t, ",") || is(t, "from"))) {
                    String sel = selection.toString();
                    String match = null;
                    for (int i = 0; i < columns.length; i++) {
                        if (sel.trim().endsWith(columns[i])) {
                            if (match == null) {
                                match = columns[i];
                            } else if (columns[i].length() > match.length()) {
                                match = columns[i];
                            }
                            selections.put(match, sel);
                        }
                    }
                    if (match == null) {
                        String trimmed = sel.trim();
                        if (trimmed.equals("*")) {
                            // do nothing
                        } else if (trimmed.endsWith("*")) {
                            if (prefix != null) {
                                throw new IllegalStateException
                                    ("multiple prefixes: " + sql);
                            }
                            prefix =
                                trimmed.substring(0, trimmed.indexOf('.'));
                        }
                    }
                    if (is(t, "from")) {
                        break;
                    }
                    selection = new StringBuffer();
                } else {
                    if (is(t, "(")) {
                        depth++;
                    } else if (is(t, ")")) {
                        depth--;
                    }
                    selection.append(t.getImage());
                }
            }

            buf.append("select ");
            for (int i = 0; i < columns.length; i++) {
                String sel = (String) selections.get(columns[i]);
                if (sel == null) {
                    if (prefix != null) {
                        buf.append(prefix);
                        buf.append(".");
                    }
                    buf.append(columns[i]);
                } else {
                    buf.append(sel);
                }
                if (i < columns.length - 1) {
                    buf.append(", ");
                }
            }
            buf.append(" ");

            for (; t != null; t = t.getNext()) {
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

        private void conditions() {
            Mapping m = Code.getMapping(m_property);
            if (m.getRetrieve() != null) {
                return;
            }

            m.dispatch(new Mapping.Switch() {
                public void onValue(Value v) {
                    conditions(v.getTable().getPrimaryKey());
                }
                public void onJoinTo(JoinTo j) {
                    conditions(j.getTable().getPrimaryKey());
                }
                public void onJoinFrom(JoinFrom j) {
                    conditions(j.getKey());
                }
                public void onJoinThrough(JoinThrough jt) {
                    conditions(jt.getFrom());
                }
                public void onStatic(Static s) {}
            });
        }

        private void conditions(Constraint c) {
            String[] columns = Code.columns(c, m_expr.getType(), null);
            m_key = new Key(m_frame, columns);
        }

        public String toString() {
            return emit(m_frame.getGenerator()).getSQL();
        }

    }

    private static class Key extends Expression {

        private QFrame m_frame;
        private String[] m_columns;

        Key(QFrame frame, String[] columns) {
            m_frame = frame;
            m_columns = columns;
        }

        void frame(Generator gen) {
            QFrame frame = gen.frame(this, null);
            List values = new ArrayList();
            for (int i = 0; i < m_columns.length; i++) {
                values.add(m_frame.getValue(m_columns[i]));
            }
            frame.setValues(values);
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return toString();
        }

        public String toString() {
            return m_frame.alias() + Arrays.asList(m_columns);
        }

    }

    private static class This extends Expression {

        private QFrame m_frame;

        This(QFrame frame) {
            m_frame = frame;
        }

        void frame(Generator gen) {
            QFrame ths =
                gen.frame(this, Define.define("this", m_frame.getType()));
            ths.setValues(m_frame.getValues());
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return "this";
        }

    }

    private static class TableAll extends Expression {

        private String m_table;
        private String[] m_columns;
        private String m_type;

        TableAll(String table, String[] columns, String type) {
            m_table = table;
            m_columns = columns;
            m_type = type;
        }

        void frame(Generator gen) {
            QFrame frame = gen.frame(this, gen.getType(m_type));
            frame.setValues(m_columns);
            frame.setTable(m_table);
        }

        Code emit(Generator gen) {
            return gen.getFrame(this).emit();
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() {
            return "tall: " + m_table;
        }

    }

    public String toString() {
        if (m_expr instanceof Condition) {
            return "(" + m_expr + ")." + m_name;
        } else {
            return m_expr + "." + m_name;
        }
    }

    String summary() { return "get " + m_name; }

}
