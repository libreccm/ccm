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

import com.redhat.persistence.ProtoException;
import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.common.SQLToken;
import com.redhat.persistence.metadata.ObjectType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Static
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class Static extends Expression {

    private static final Logger logger = Logger.getLogger(Static.class);

    public final static String versionId = "$Id: Static.java 1130 2006-04-30 13:40:54Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";

    private SQL m_sql;
    private String[] m_columns;
    private boolean m_map;
    private Map m_bindings;
    private Expression m_scope;
    private List m_expressions = new ArrayList();

    private static final Collection s_functions = new HashSet();
    static {
        logger.debug("Static initalizer starting...");
        String[] functions = {
            /* sql standard functions supported by both oracle and postgres.
             * there is an added caveat that the function uses normal function
             * syntax and not keywords as arguments (e.g. trim(leading 'a'
             * from str), substring('teststr' from 3 for 2))
             */
            "current_date", "current_timestamp",
            "upper", "lower",
            "trim", // only trim(str) syntax is allowed
            // postgres supported oracle-isms
            "substr", "length", "nvl", "last_day"
        };
        for (int i = 0; i < functions.length; i++) {
            s_functions.add(functions[i]);
        }
        logger.debug("Static initalizer finished.");
    }

    private static final boolean isAllowedFunction(String s) {
        return s_functions.contains(s);
    }

    public Static(String sql) {
        this(sql, Collections.EMPTY_MAP);
    }

    public Static(String sql, Map bindings) {
        this(parse(sql), null, true, bindings);
    }

    public Static(SQL sql, String[] columns, boolean map, Map bindings) {
        this(sql, columns, map, bindings, null);
    }

    public Static(SQL sql, String[] columns, boolean map, Map bindings,
                  Expression scope) {
        m_sql = sql;
        m_columns = columns;
        m_map = map;
        m_bindings = bindings;
        m_scope = scope == null ? this : scope;

        int size = size(m_sql);

        for(SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                String image = t.getImage();
                Expression e;
                if (t.isBind()) {
                    e = bind(image);
                } else if (t.isPath()) {
                    All all = new All(image, m_bindings, m_scope, size != 1);
                    if (isAllowedFunction(image) || !m_map) {
                        e = new Choice(all, image);
                    } else {
                        e = new Choice(all, path(image));
                    }
                } else {
                    throw new IllegalStateException
                        ("don't know how to deal with token: " + t);
                }
                m_expressions.add(e);
            }
        }
    }

    private static boolean isExpression(SQLToken tok) {
        return tok.isBind() || tok.isPath();
    }

    private static ThreadLocal s_parsers = new ThreadLocal() {
        protected Object initialValue() {
            return new SQLParser(new StringReader(""));
        }
    };

    private static SQL parse(String sql) {
        SQLParser p = (SQLParser) s_parsers.get();
        p.initialize(new StringReader(sql));

        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL();
    }

    private Expression bind(String image) {
        return expression(Path.get(image), true);
    }

    private Expression path(String image) {
        return expression(Path.get(image), false);
    }

    private Expression expression(Path path, boolean isBind) {
        if (path.getParent() == null) {
            String name = path.getName();
            if (isBind) {
                final String key = name.substring(1);
                if (m_bindings.containsKey(key)) {
                    return new Literal(m_bindings.get(key)) {
                        Object getBindKey(Generator gen) {
                            return new CompoundKey(gen.id(m_scope), key);
                        }
                    };
                }

                // XXX: use real subtype
                throw new ProtoException
                    ("no " + key + " in " + m_bindings, false) {};
            } else {
                return new Variable(name);
            }
        } else {
            return new Get
                (expression(path.getParent(), isBind), path.getName());
        }
    }

    private static int size(SQL sql) {
        int size = 0;
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            String image = t.getImage();
            for (int i = 0; i < image.length(); i++) {
                if (!Character.isWhitespace(image.charAt(i))) {
                    size++;
                    break;
                }
            }
        }
        return size;
    }

    protected ObjectType getType() { return null; }
    protected boolean hasType() { return false; }

    void frame(Generator gen) {
        boolean bool = gen.isBoolean(this) && m_expressions.size() == 1;
        for (Iterator it = m_expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            if (bool) { gen.addBoolean(e); }
            e.frame(gen);
            gen.addUses(this, gen.getUses(e));
        }
        if (hasType()) {
            ObjectType type = getType();
            QFrame frame = gen.frame(this, type);
            frame.setValues(m_columns);
            frame.setTable(this);
        } else if (!gen.isBoolean(this) && m_expressions.size() == 1
                   && size(m_sql) == 1) {
            Expression e = (Expression) m_expressions.get(0);
            if (gen.hasFrame(e)) {
                QFrame child = gen.getFrame(e);
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
                frame.setMappings(child.getMappings());
            }
        }
    }

    Code emit(Generator gen) {
        if (!hasType() && gen.hasFrame(this)) {
            return gen.getFrame(this).emit();
        }

        Code result = new Code();
        int index = 0;
        if (hasType()) { result = result.add("("); }
        for (SQLToken t = m_sql.getFirst(); t != null; t = t.getNext()) {
            if (isExpression(t)) {
                Expression e = (Expression) m_expressions.get(index++);
                result = result.add(e.emit(gen));
            } else if (t.isRaw())  {
                // XXX: ignore escapes for now
                String raw = t.getImage();
                result = result.add(raw.substring(4, raw.length() - 1));
            } else {
                result = result.add(t.getImage());
            }
        }
        if (hasType()) { result = result.add(")"); }
        return result;
    }

    void hash(Generator gen) {
        gen.hash(m_sql);
        if (m_columns != null) {
            for (int i = 0; i < m_columns.length; i++) {
                gen.hash(m_columns[i]);
            }
        }
        List keys = new ArrayList(m_bindings.keySet());
        Collections.sort(keys);
        Object id = gen.id(this);
        List vals = new ArrayList();
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            Object value = m_bindings.get(key);
            vals.clear();
            Literal.convert
                (value, vals, gen.getRoot(), new CompoundKey(id, key));
            gen.hash(key);
            for (int j = 0; j < vals.size(); j++) {
                Code c = (Code) vals.get(j);
                gen.hash(c.getSQL());
                gen.bind(c);
            }
        }
        gen.hash(m_map);
        gen.hash(getClass());
    }

    private class Choice extends Expression {

        private All m_all;
        private Expression m_expression;
        private String m_image;

        Choice(All all, Expression alternative) {
            m_all = all;
            m_expression = alternative;
        }

        Choice(All all, String alternative) {
            m_all = all;
            m_image = alternative;
        }

        void frame(Generator gen) {
            QFrame child = null;
            if (gen.hasType(m_all.getType())) {
                if (gen.isBoolean(this)) { gen.addBoolean(m_all); }
                m_all.frame(gen);
                if (gen.hasFrame(m_all)) {
                    child = gen.getFrame(m_all);
                    gen.addUses(this, gen.getUses(m_all));
                }
            } else if (m_expression != null) {
                if (gen.isBoolean(this)) { gen.addBoolean(m_expression); }
                m_expression.frame(gen);
                if (gen.hasFrame(m_expression)) {
                    child = gen.getFrame(m_expression);
                }
                gen.addUses(this, gen.getUses(m_expression));
            }

            if (child != null) {
                QFrame frame = gen.frame(this, child.getType());
                frame.addChild(child);
                frame.setValues(child.getValues());
                frame.setMappings(child.getMappings());
            }
        }

        Code emit(Generator gen) {
            if (gen.hasFrame(this)) {
                return gen.getFrame(this).emit();
            } else if (gen.hasType(m_all.getType())) {
                return m_all.emit(gen);
            } else if (m_expression != null) {
                return m_expression.emit(gen);
            } else {
                return new Code(m_image);
            }
        }

        void hash(Generator gen) {
            throw new UnsupportedOperationException();
        }

        String summary() { return this.toString(); }

    }

    public String toString() {
        return "sql {" + m_sql + "}";
    }

    String summary() {
        return toString();
    }

}
