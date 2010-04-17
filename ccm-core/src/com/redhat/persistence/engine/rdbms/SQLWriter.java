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

import com.arsdigita.util.WrappedError;
import com.redhat.persistence.Condition;
import com.redhat.persistence.Expression;
import com.redhat.persistence.SQLWriterException;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.common.SQLToken;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.oql.Code;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * SQLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: SQLWriter.java 735 2005-09-01 06:42:59Z sskracic $
 **/

public abstract class SQLWriter {

    private RDBMSEngine m_engine;
    private Operation m_op = null;
    private StringBuffer m_sql = new StringBuffer();
    private ArrayList m_bindings = new ArrayList();
    private ArrayList m_types = new ArrayList();
    private HashSet m_expanded = new HashSet();

    void setEngine(RDBMSEngine engine) {
        m_engine = engine;
    }

    public RDBMSEngine getEngine() {
        return m_engine;
    }

    public void clear() {
        m_op = null;
        m_sql = new StringBuffer();
        m_bindings.clear();
        m_types.clear();
        m_expanded.clear();
    }

    public String getSQL() {
        return m_sql.toString();
    }

    public Collection getBindings() {
        return m_bindings;
    }

    public Collection getTypes() {
        return m_types;
    }

    public Collection getTypeNames() {
        ArrayList result = new ArrayList();

        for (Iterator it = getTypes().iterator(); it.hasNext(); ) {
            Integer type = (Integer) it.next();
            result.add(Column.getTypeName(type.intValue()));
        }

        return result;
    }

    public void bind(PreparedStatement ps, StatementLifecycle cycle) {
        Root root = m_engine.getSession().getRoot();
        for (int i = 0; i < m_bindings.size(); i++) {
            int index = i+1;
            Object obj = m_bindings.get(i);
            int type = ((Integer) m_types.get(i)).intValue();

            try {
                if (cycle != null) { cycle.beginSet(index, type, obj); }
                if (obj == null) {
                    ps.setNull(index, type);
                } else {
                    Adapter ad = root.getAdapter(obj.getClass());
                    ad.bind(ps, index, obj, type);
                }
                if (cycle != null) { cycle.endSet(); }
            } catch (SQLException e) {
                if (cycle != null) { cycle.endSet(e); }
                throw new WrappedError
                    ("SQL error binding [" + (index) + "] to " + obj +
                     " in " + m_sql.toString(), e);
            }
        }
    }

    public void write(String str) {
        m_sql.append(str);
    }

    public void write(Path path) {
        if (m_op == null) {
            throw new IllegalStateException
                ("trying to write path outside of operation");
        }

        if (m_op.isParameter(path)) {
	    if (!m_op.contains(path)) {
		throw new UnboundParameterException(path);
	    }
            Object value = m_op.get(path);
            if (value instanceof Collection) {
                Collection c = (Collection) value;
                m_sql.append("(");
                for (Iterator it = c.iterator(); it.hasNext(); ) {
                    Object o = it.next();
                    writeBind(o, m_op.getType(path));
                    if (it.hasNext()) {
                        m_sql.append(", ");
                    }
                }
                m_sql.append(")");
            } else {
                writeBind(value, m_op.getType(path));
            }
        } else {
            m_sql.append(path);
        }
    }

    void writeBind(Object value, int jdbcType) {
        boolean shouldBind = true;
        Adapter ad = null;
        if (value != null) {
            Root root = m_engine.getSession().getRoot();
            ad = root.getAdapter(value.getClass());
            shouldBind = ad.isBindable();
        }
        if (shouldBind) {
            m_sql.append("?");
            m_bindings.add(value);
            m_types.add(new Integer(jdbcType));
        } else {
            m_sql.append(ad.getLiteralCode(value));
        }

    }

    void write(Code code) {
        write(code.getSQL());
        List bindings = code.getBindings();
        for (int i = 0; i < bindings.size(); i++) {
            Code.Binding b = (Code.Binding) bindings.get(i);
            m_bindings.add(b.getValue());
            m_types.add(new Integer(b.getType()));
        }
    }

    public void write(Operation op) {
        // XXX: this is a hack, for binding to work properly we need to call
        // the Operation version of write.
        Operation old = m_op;
        try {
            m_op = op;
            op.write(this);
        } finally {
            m_op = old;
        }
    }

    public void write(SQL sql) {
        write(sql.getFirst(), null);
    }

    public void write(SQL sql, boolean map) {
        write(sql.getFirst(), null, map);
    }

    public void write(SQLToken start, SQLToken end) {
        write(start, end, false);
    }

    public void write(SQLToken start, SQLToken end, boolean map) {
        Root r = m_engine.getSession().getRoot();

        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isRaw()) {
                // XXX: ignore escapes for now
                String raw = t.getImage();
                write(raw.substring(4, raw.length() - 1));
                continue;
            }

            if (t.isBind()) {
                write(Path.get(t.getImage()));
                continue;
            }

            if (t.isPath() && r.hasObjectType(t.getImage())) {
                ObjectMap om = r.getObjectMap
                    (r.getObjectType(t.getImage()));
                SQLBlock b = om.getRetrieveAll();
                if (b != null) {
                    write(b.getSQL());
                    continue;
                }
            } else if (t.isPath() && map) {
                Path p = Path.get(t.getImage());
                if (m_op.getMapping(p) != null) {
                    write(Expression.variable(p));
                } else {
                    write(t.getImage());
                }
            } else {
                write(t.getImage());
            }
        }
    }

    public void write(StaticOperation sop) {
        SQLBlock block = sop.getSQLBlock();
        SQL sql = block.getSQL();

        boolean first = true;
        boolean execute = false;
        SQLToken written = sql.getFirst();
        SQLToken firstBegin = null;

        for (Iterator it = block.getAssigns().iterator(); it.hasNext(); ) {
            SQLBlock.Assign assign = (SQLBlock.Assign) it.next();
            boolean keep = true;
            Collection bindings = sql.getBindings
                (assign.getBegin(), assign.getEnd());
            for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
                Path p = (Path) iter.next();
                if (!sop.contains(p)) {
                    keep = false;
                }
            }

            if (first) {
                first = false;
                firstBegin = assign.getBegin();
            }

            if (keep) {
                if (execute) {
                    write(",");
                } else {
                    write(sql.getFirst(), firstBegin);
                }
                execute = true;
                write(assign.getBegin(), assign.getEnd());
            }

            written = assign.getEnd();
        }

        if (execute || block.getAssigns().size() == 0) {
            write(written, null);
        }
    }

    public void write(Join join) {
        join.write(this);
    }

    private final Expression.Switch m_esw = new Expression.Switch() {
        public void onCondition(Condition c) { write(c); }
        public void onVariable(Expression.Variable v) { write(v); }
        public void onValue(Expression.Value v) { write(v); }
        public void onPassthrough(Expression.Passthrough p) { write(p); }
    };

    public void write(Expression expr) {
        expr.dispatch(m_esw);
    }

    private final Condition.Switch m_csw = new Condition.Switch() {
        public void onAnd(Condition.And a) { write(a); }
        public void onOr(Condition.Or o) { write(o); }
        public void onNot(Condition.Not n) { write(n); }
        public void onEquals(Condition.Equals e) { write(e); }
        public void onIn(Condition.In i) { write(i); }
        public void onContains(Condition.Contains c) { write(c); }
    };

    public void write(Condition cond) {
        cond.dispatch(m_csw);
    }

    public void write(Expression.Variable v) {
        if (m_expanded.contains(v)) {
            write(v.getPath());
        } else {
            Path[] cols = m_op.getMapping(v.getPath());
            if (cols == null) { throw new Error("no mapping: " + v); }
            if (cols.length != 1) {
                throw new Error("expands to wrong multiplicity");
            }
            write(cols[0]);
        }
    }

    public void write(Expression.Value v) {
        int type = RDBMSEngine.getType
            (getEngine().getSession().getRoot(), v.getValue());
        writeBind(v.getValue(), type);
    }

    public void write(Expression.Passthrough e) {
        SQLParser p = new SQLParser(new StringReader(e.getExpression()));
        try {
            p.sql();
        } catch (ParseException pe) {
            throw new WrappedError(pe);
        }

        write(p.getSQL(), true);
    }

    public void write(Condition.And cond) {
        write(cond.getLeft());
        write(" and ");
        write(cond.getRight());
    }

    public void write(Condition.Or cond) {
        write(cond.getLeft());
        write(" or ");
        write(cond.getRight());
    }

    public void write(Condition.Not cond) {
        write("not ");
        write(cond.getExpression());
    }

    public void write(Condition.In cond) {
        write(cond.getLeft());
        write(" in (");
        write(cond.getRight());
        write(")");
    }


    private boolean isExpandable(Expression expr) {
        return !m_expanded.contains(expr)
            && expr instanceof Expression.Variable
            || expr instanceof Expression.Value;
    }

    private Path[] expand(Expression expr) {
        final Path[][] result = { null };

        expr.dispatch(new Expression.Switch() {
            public void onVariable(Expression.Variable v) {
                if (m_op.isParameter(v.getPath())) {
                    result[0] = new Path[] { v.getPath() };
                } else {
                    result[0] = m_op.getMapping(v.getPath());
                    if (result[0] == null) {
                        throw new IllegalStateException
                            ("no expansion for expr: " + v);
                    }
                }
            }
            public void onValue(Expression.Value v) {
                throw new Error("not implemented");
            }
            public void onPassthrough(Expression.Passthrough p) {
                throw new Error("not implemented");
            }
            public void onCondition(Condition c) {
                throw new Error("not implemented");
            }
        });

        return result[0];
    }

    private Expression expand(Expression left, Expression right) {
        if (!isExpandable(left)) {
            throw new IllegalArgumentException("not expandable: " + left);
        }
        if (!isExpandable(right)) {
            throw new IllegalArgumentException("not expandable: " + right);
        }

        Expression result = null;

        Path[] leftCols = expand(left);
        Path[] rightCols = expand(right);
        if (leftCols.length != rightCols.length) {
            throw new SQLWriterException
                ("left and right of different lengths\n" +
                 "left expression: " + left +
                 "; columns: " + Arrays.asList(leftCols) + "\n" +
                 "right expression: " + right +
                 "; columns: " + Arrays.asList(rightCols));
        }

        for (int i = 0; i < leftCols.length; i++) {
            Expression l = Expression.variable(leftCols[i]);
            Expression r = Expression.variable(rightCols[i]);
            m_expanded.add(l);
            m_expanded.add(r);
            Expression eq = Condition.equals(l, r);
            if (result == null) {
                result = eq;
            } else {
                result = Condition.and(result, eq);
            }
        }

        return result;
    }

    private void writeLogicalEquals(Expression left, Expression right) {
        if (isExpandable(left) && isExpandable(right)) {
            write(expand(left, right));
        } else {
            writeEquals(left, right);
        }
    }

    void writeEquals(Expression left, Expression right) {
        write(left);
        write(" = ");
        write(right);
    }

    public void write(Condition.Equals cond) {
        writeLogicalEquals(cond.getLeft(), cond.getRight());
    }

    public void write(Condition.Contains cond) {
        writeLogicalEquals(cond.getLeft(), cond.getRight());
    }

    public abstract void write(Select select);
    public abstract void write(Insert insert);
    public abstract void write(Update update);
    public abstract void write(Delete delete);

    public abstract void write(StaticJoin join);
    public abstract void write(SimpleJoin join);
    public abstract void write(InnerJoin join);
    public abstract void write(LeftJoin join);
    public abstract void write(RightJoin join);
    public abstract void write(CrossJoin join);

}

