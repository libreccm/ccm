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

import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ForeignKey;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.list.SetUniqueList;
import org.apache.log4j.Logger;

/**
 * QFrame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 **/

class QFrame {

    

    private static final Logger s_log = Logger.getLogger(QFrame.class);

    private Generator m_generator;
    private String m_alias;

    private EquiSet m_equisetpool;
    private List m_nonnullpool;
    private List m_valuespool;
    private Map m_columnspool;
    private List m_qvaluespool;

    private List m_children;

    private Expression m_expression;
    private ObjectType m_type;
    private QFrame m_container;

    private boolean m_outer;
    private List m_values;
    private Map m_mappings;
    private String m_table;
    private Expression m_tableExpr;
    private List m_colkeys;
    private Map m_columns;
    private List m_qvalues;
    private QFrame m_parent;
    private Expression m_condition;
    private Expression m_order;
    private boolean m_asc;
    private Expression m_limit;
    private Expression m_offset;
    private boolean m_hoisted;
    private QFrame m_duplicate;
    private EquiSet m_equiset;
    private List m_nonnull;
    private boolean m_equated;

    QFrame(Generator generator) {
        m_generator = generator;
        m_alias = "t" + m_generator.getFrames().size();

        m_equisetpool = new EquiSet(m_generator);
        m_nonnullpool = SetUniqueList.decorate(new ArrayList());
        m_valuespool = new ArrayList();
        m_columnspool = new HashMap();
        m_qvaluespool = new ArrayList();


        m_children = new ArrayList();
        m_colkeys = new ArrayList();
    }

    void init(Expression expression, ObjectType type, QFrame container) {
        m_expression = expression;
        m_type = type;
        m_container = container;

        m_children.clear();
        m_colkeys.clear();

        m_outer = false;
        m_values = null;
        m_mappings = null;
        m_table = null;
        m_tableExpr = null;
        m_columns = null;
        m_qvalues = null;
        m_parent = null;
        m_condition = null;
        m_order = null;
        m_asc = true;
        m_limit = null;
        m_offset = null;
        m_hoisted = false;
        m_duplicate = null;
        m_equiset = null;
        m_nonnull = null;
        m_equated = false;
    }

    Generator getGenerator() {
        return m_generator;
    }

    Expression getExpression() {
        return m_expression;
    }

    ObjectType getType() {
        return m_type;
    }

    QFrame getContainer() {
        return m_container;

    }

    void setOuter(boolean outer) {
        m_outer = outer;
    }

    boolean isOuter() {
        return m_outer || (m_parent != null && m_parent.isOuter());
    }

    void setValues(String[] columns) {
        m_valuespool.clear();
        m_values = m_valuespool;
        for (int i = 0; i < columns.length; i++) {
            m_values.add(getValue(columns[i]));
        }
    }

    void setValues(List values) {
        m_values = values;
    }

    List getValues() {
        return m_values;
    }

    QValue getValue(String column) {
        if (m_columns == null) {
            m_columnspool.clear();
            m_qvaluespool.clear();
            m_columns = m_columnspool;
            m_qvalues = m_qvaluespool;
        }
        QValue v = (QValue) m_columns.get(column);
        if (v == null) {
            v = new QValue(this, column);
            m_columns.put(column, v);
            m_qvalues.add(v);
            m_colkeys.add(column);
        }
        return v;
    }

    QValue getValue(Code sql) {
        return new QValue(this, sql);
    }

    List getColumns() {
        return m_colkeys;
    }

    boolean hasValue(String column) {
        if (m_columns == null) {
            return false;
        } else {
            return m_columns.containsKey(column);
        }
    }

    boolean hasMappings() {
        return m_mappings != null;
    }

    boolean hasMapping(Path p) {
        return hasMappings() && m_mappings.containsKey(p);
    }

    String getMapping(Path p) {
        return (String) m_mappings.get(p);
    }

    void addMapping(Path p, String c) {
        if (m_mappings == null) { m_mappings = new HashMap(); }
        m_mappings.put(p, c);
    }

    void addMappings(Map mappings) {
        if (mappings == null) { return; }
        for (Iterator it = mappings.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            addMapping((Path) me.getKey(), (String) me.getValue());
        }
    }

    void setMappings(Map mappings) {
        m_mappings = mappings;
    }

    Map getMappings() {
        return m_mappings;
    }

    void setTable(String table) {
        m_table = table;
    }

    String getTable() {
        return m_table;
    }

    void setTable(Expression expr) {
        m_tableExpr = expr;
    }

    void addChild(QFrame child) {
        m_children.add(child);
        child.m_parent = this;
    }

    void addChild(int index, QFrame child) {
        m_children.add(index, child);
        child.m_parent = this;
    }

    QFrame getChild(int index) {
        return (QFrame) m_children.get(index);
    }

    List getChildren() {
        return m_children;

    }

    QFrame getParent() {
        return m_parent;
    }

    QFrame getRoot() {
        if (m_parent == null) {
            return this;
        } else {
            return m_parent.getRoot();
        }
    }

    void setCondition(Expression condition) {
        m_condition = condition;
    }

    Expression getCondition() {
        return m_condition;
    }

    void setOrder(Expression order, boolean asc) {
        m_order = order;
        m_asc = asc;
    }

    void setLimit(Expression limit) {
        m_limit = limit;
    }

    Expression getLimit() {
        return m_limit;
    }

    void setOffset(Expression offset) {
        m_offset = offset;
    }

    Expression getOffset() {
        return m_offset;
    }

    String alias() {
        if (m_duplicate != null) { return m_duplicate.alias(); }
        return m_alias;
    }

    EquiSet getEquiSet() {
        return m_equiset;
    }

    Code emit() {
        return emit(true, true);
    }

    private List m_orders = new ArrayList();
    private List m_where = new ArrayList();

    Code emit(boolean select, boolean range) {
        m_where.clear();
        Code join = null;
        if (!m_hoisted) {
            join = render(m_where);
            if (join != null && join.isEmpty()) { join = null; }
        }

        Code result = new Code();
        if (select) {
            if (join != null) {
                result = result.add("(select ");
            } else if (m_values.size() > 1) {
                result = result.add("(");
            }
            for (int i = 0; i < m_values.size(); i++) {
                QValue v = (QValue) m_values.get(i);
                result = result.add(v.emit());
                if (i < m_values.size() - 1) {
                    result = result.add(", ");
                }
            }
            if (m_values.isEmpty()) {
                result = result.add("1");
            }
        }

        if (select && join != null) {
            result = result.add("\nfrom ");
        }

        if (join != null) {
            result = result.add(join);
        }

        for (int i = 0; i < m_where.size(); i++) {
            if (i == 0) {
                result = result.add("\nwhere ");
            } else {
                result = result.add(" and ");
            }
            result = result.add((Code) m_where.get(i));
        }

        m_orders.clear();
        addOrders(m_orders);
        if (!m_orders.isEmpty()) {
            result = result.add("\norder by ");
        }
        for (int i = 0; i < m_orders.size(); i++) {
            Code key = (Code) m_orders.get(i);
            result = result.add(key);
            if (i < m_orders.size() - 1) {
                result = result.add(", ");
            }
        }

        if (range) {
            // XXX: nested offsets and limits are ignored
            if (m_offset != null) {
                result = result.add("\noffset ");
                result = result.add(m_offset.emit(m_generator));
            }

            if (m_limit != null) {
                result = result.add("\nlimit ");
                result = result.add(m_limit.emit(m_generator));
            }
        }

        if (select && (join != null || m_values.size() > 1)) {
            result = result.add(")");
        }

        return result;
    }

    private void addOrders(List result) {
        if (m_order != null) {
            Code order = m_order.emit(m_generator);
            if (!m_asc) {
                order = order.add(" desc");
            }
            result.add(order);
        }
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            child.addOrders(result);
        }
    }

    private static class JFrame {

        static JFrame leaf(Code table, QFrame frame, QFrame oroot) {
            return new JFrame(table, frame, oroot);
        }

        static JFrame cross(JFrame left, JFrame right) {
            if (!left.oroot.equals(right.oroot)) {
                throw new IllegalStateException
                    ("can't cross joins from different oroots");
            }
            Code join = left.join.add(" cross join ").add(right.join);
            JFrame result = new JFrame(join, left, right);
            result.oroot = left.oroot;
            return result;
        }

        static JFrame join(JFrame left, JFrame right, Code on) {
            Code join = left.join;
            if (left.oroot.equals(right.oroot)) {
                join = join.add("\njoin ");
            } else {
                join = join.add("\nleft join ");
            }
            join = join.add(right.join).add(" on ").add(on);
            JFrame result = new JFrame(join, left, right);
            result.froot = right.oroot;
            result.oroot = left.oroot;
            return result;
        }

        Code join = null;
        Set defined = null;
        QFrame froot = null;
        QFrame oroot = null;

        private JFrame(Code table, QFrame frame, QFrame oroot) {
            join = table;
            defined = Collections.singleton(frame);
            this.oroot = oroot;
        }

        private JFrame(Code join, JFrame left, JFrame right) {
            this.join = join;
            defined = new HashSet();
            defined.addAll(left.defined);
            defined.addAll(right.defined);
        }

        public String toString() {
            return "jframe: " + join;
        }

    }

    String trace(LinkedList joins) {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = joins.iterator(); it.hasNext(); ) {
            buf.append("\n  ");
            buf.append(it.next());
        }
        return buf.toString();
    }

    private Set m_emitted = new HashSet();

    private Code render(List where) {
        LinkedList joins = new LinkedList();
        m_emitted.clear();
        render(joins, where, this, this, m_emitted);
        Code code = null;
        for (Iterator it = joins.iterator(); it.hasNext(); ) {
            JFrame frame = (JFrame) it.next();
            if (code == null) {
                code = frame.join;
            } else {
                code = code.add("\ncross join ").add(frame.join);
            }
        }
        return code;
    }

    private Set m_used = new HashSet();

    private void render(LinkedList joins, List where, QFrame oroot,
                        QFrame root, Set emitted) {
        // If the first non empty frame is outer we treat it as inner.
        if (m_outer && !joins.isEmpty()) {
            oroot = this;
        }

        Code table = null;
        if (m_table != null && m_duplicate == null) {
            table = new Code(m_table).add(" ").add(alias());
        } else if (m_tableExpr != null && m_duplicate == null) {
            table = m_tableExpr.emit(m_generator).add(" ").add(alias());
        }

        if (table != null) {
            joins.addFirst(JFrame.leaf(table, this, oroot));
        }

        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            child.render(joins, where, oroot, root, emitted);
        }

        if (m_condition != null) {
            Code c = m_condition.emit(m_generator);
            if (!c.isTrue() && !emitted.contains(c)) {
                m_used.clear();
                frames(m_condition, m_used);
                boolean join = false;
                for (Iterator it = joins.iterator(); it.hasNext(); ) {
                    JFrame frame = (JFrame) it.next();
                    boolean modified = m_used.removeAll(frame.defined);
                    if (m_used.isEmpty()) {
                        // We default to putting things in the where
                        // clause here because oracle won't resolve
                        // external variable references correctly when
                        // they appear in join conditions.
                        if (oroot.equals(root)) {
                            where.add(c);
                        } else if (frame.froot != null
                                   && oroot.equals(frame.froot)) {
                            frame.join = frame.join.add(" and ").add(c);
                        } else {
                            throw new IllegalStateException
                                ("unable to place condition: " + m_condition +
                                 " " + c + trace(joins));
                        }
                    } else if (modified) {
                        join = true;
                        break;
                    }
                }
                if (join) {
                    JFrame right = (JFrame) joins.removeFirst();

                    if (joins.isEmpty()) {
                        throw new IllegalStateException
                            ("unresolved variable in condition: " +
                             m_condition + " " + c + trace(joins));
                    }

                    LinkedList skipped = null;
                    JFrame left = (JFrame) joins.removeFirst();
                    while (true) {
                        m_used.clear();
                        frames(m_condition, m_used);
                        m_used.removeAll(right.defined);
                        boolean cross = m_used.removeAll(left.defined);
                        if (m_used.isEmpty()) {
                            joins.addFirst(JFrame.join(left, right, c));
                            break;
                        } else if (joins.isEmpty()) {
                            throw new IllegalStateException
                                ("unresolved variable in condition: " +
                                 m_condition + " " + c + trace(joins));
                        } else if (cross) {
                            JFrame lefter = (JFrame) joins.removeFirst();
                            left = JFrame.cross(lefter, left);
                        } else {
                            if (skipped == null) {
                                skipped = new LinkedList();
                            }
                            skipped.addLast(left);
                            left = (JFrame) joins.removeFirst();
                        }
                    }

                    if (skipped != null) {
                        while (!skipped.isEmpty()) {
                            joins.addFirst(skipped.removeLast());
                        }
                    }
                }

                emitted.add(c);
            }
        }
    }

    void frames(Expression e, Set result) {
        frames(m_generator.getUses(e), result);
    }

    void frames(List values, Set result) {
        for (int i = 0; i < values.size(); i++) {
            QValue value = (QValue) values.get(i);
            QFrame frame = value.getFrame().getDuplicate();
            if (frame.getRoot().equals(getRoot())) {
                result.add(frame);
            }
        }
    }

    void addConditions(List result) {
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            child.addConditions(result);
        }
        if (m_condition != null) {
            result.add(m_condition);
        }
    }

    boolean isSubframe(QFrame f) {
        QFrame root = getRoot();
        for (QFrame c = f.getContainer(); c != null; c = c.getContainer()) {
            if (c.getRoot().equals(root)) {
                return true;
            }
        }
        return false;
    }

    boolean isDescendant(QFrame frame) {
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            if (child.equals(frame)) { return true; }
            if (child.isDescendant(frame)) { return true; }
        }
        return false;
    }

    boolean isSelect() {
        if (m_hoisted) {
            return false;
        } else {
            m_where.clear();
            return render(m_where) != null;
        }
    }

    boolean hoist() {
        // XXX: Rather than this m_select business we could construct
        // another QFrame, copy children, condition, etc to it and
        // remove our own children.
        QFrame frame = m_generator.getConstraining(this);
        if (frame == null) { return false; }
        if (m_parent != null) {
            m_parent.m_children.remove(this);
        }
        frame.addChild(this);
        m_hoisted = true;
        setOuter(true);
        return true;
    }

    void addInnerConditions(List result) {
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            if (!child.m_outer) {
                child.addInnerConditions(result);
            }
        }
        if (m_condition != null) {
            result.add(m_condition);
        }
    }

    private List m_equals = new ArrayList();
    private List m_from = new ArrayList();
    private List m_to = new ArrayList();

    void mergeOuter() {
        if (!m_outer) { return; }
        m_equals.clear();
        if (addEquals(m_equals)) {
            m_from.clear();
            m_to.clear();
            m_generator.split(this, m_equals, m_from, m_to);
            if (isConnected(m_to, m_from)) {
                QFrame target = ((QValue) m_to.get(0)).getFrame();
                if (target.getRoot().equals(getRoot())) {
                    // At this point barring the possibility of from
                    // being a nullable unique key we know merging is
                    // ok, so we're going to move this frame to be a
                    // child of the to frame so that we can later
                    // merge its equiset with its new parent.

                    // XXX: consider moving the frame directly to
                    // its final destination in hoist rather than
                    // moving it in two steps
                    m_parent.m_children.remove(this);
                    target.addChild(this);

                    if (!isNullable(m_to)) {
                        m_outer = false;
                    }
                }
            }
        }
    }

    void equifill() {
        if (m_equiset == null) {
            m_equisetpool.clear();
            m_equiset = m_equisetpool;
        }
        if (m_nonnull == null) {
            m_nonnullpool.clear();
            m_nonnull = m_nonnullpool;
        }

        EquiSet shared = m_generator.getSharedFrames();

        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            if (child.m_outer) {
                m_equals.clear();
                if (child.addEquals(m_equals)) {
                    m_from.clear();
                    m_to.clear();
                    m_generator.split(child, m_equals, m_from, m_to);
                    if (isConnected(m_to, m_from)) {
                        shared.equate(this, child);
                        child.m_equiset = m_equiset;
                    }
                }
                child.equifill();
            } else {
                shared.equate(this, child);
                child.m_equiset = m_equiset;
                child.m_nonnull = m_nonnull;
                child.equifill();
            }
        }

        if (m_condition != null) {
            List nn  = m_generator.getNonNull(m_condition);
            for (int i = 0; i < nn.size(); i++) {
                m_nonnull.add(nn.get(i));
            }
        }

        if (m_columns != null) {
            for (int i = 0; i < m_qvalues.size(); i++) {
                QValue qv = (QValue) m_qvalues.get(i);
                if (!isNullable(Collections.singletonList(qv))) {
                    m_nonnull.add(qv);
                }
            }
        }
    }

    boolean innerize(Set collapse, Map canon) {
        boolean modified = false;

        if (!m_outer && m_parent != null && m_equiset != m_parent.m_equiset) {
            if (merge(this, m_parent)) {
                collapse.add(this.m_equiset);
                modified = true;
            }
        }

        if (!m_outer && m_parent != null && m_nonnull != m_parent.m_nonnull) {
            m_parent.m_nonnull.addAll(m_nonnull);
            m_nonnull = m_parent.m_nonnull;
            modified = true;
        }

        if (m_condition != null) {
            if (!m_equated) {
                m_generator.equate(m_equiset, m_condition);
                collapse.add(m_equiset);
                m_equated = true;
                modified = true;
            }
        }

        if (m_columns != null) {
            modified |= innerizeAncestors(m_qvalues);
        }

        modified |= merge(collapse, canon);

        if (m_outer) {
            m_equals.clear();
            if (addEquals(m_equals)) {
                m_from.clear();
                m_to.clear();
                m_generator.split(this, m_equals, m_from, m_to);

                // XXX: compound keys
                if (m_to.size() == 1) {
                    QValue target = (QValue) m_to.get(0);
                    List vals = m_parent.m_equiset.get(target);
                    if (vals != null) {
                        QValue key = (QValue) m_from.get(0);
                        String table = key.getTable();
                        String column = key.getColumn();
                        if (table != null && column != null) {
                            for (int i = 0; i < vals.size(); i++) {
                                QValue qv = (QValue) vals.get(i);
                                if (table.equals(qv.getTable())
                                    && column.equals(qv.getColumn())
                                    && m_parent.nn(qv)) {
                                    m_outer = false;
                                    modified = true;
                                    // Our join condition is
                                    // equivalent to an inner join
                                    // condition
                                }
                            }
                        }
                    }
                }
            }
        }

        return modified;
    }

    private QFrame max(QFrame a, QFrame b) {
        if (a == null) { return b; }
        if (b == null) { return a; }
        if (a.m_equiset.size() > b.m_equiset.size()) {
            return a;
        } else {
            return b;
        }
    }

    private QFrame max(List frames) {
        QFrame result = null;
        for (int i = 0; i < frames.size(); i++) {
            QFrame frame = (QFrame) frames.get(i);
            result = max(result, frame);
        }
        return result;
    }

    private boolean merge(QFrame a, QFrame b) {
        if (a.m_equiset == b.m_equiset) { return false; }
        EquiSet shared = m_generator.getSharedFrames();
        shared.equate(a, b);
        List from = shared.get(a);
        QFrame to = max(from);
        boolean modified = false;
        for (int i = 0; i < from.size(); i++) {
            QFrame qf = (QFrame) from.get(i);
            if (qf.m_equiset != to.m_equiset) {
                to.m_equiset.addAll(qf.m_equiset);
                qf.m_equiset = to.m_equiset;
                modified = true;
            }
        }
        return modified;
    }

    private boolean merge(Set collapse, Map canon) {
        m_equals.clear();
        if (!addEquals(m_equals)) { return false; }

        m_from.clear();
        m_to.clear();
        m_generator.split(this, m_equals, m_from, m_to);
        Object key = key(m_to);
        if (key == null) { return false; }
        for (int i = 0; i < m_from.size(); i++) {
            QValue qv = (QValue) m_from.get(i);
            String t = qv.getTable();
            if (t == null) { return false; }
            String c = qv.getColumn();
            if (c == null) { return false; }
            key = new CompoundKey(new CompoundKey(key, t), c);
        }
        QFrame qf = (QFrame) canon.get(key);
        if (qf == null) {
            canon.put(key, this);
            return false;
        } else if (merge(this, qf)) {
            collapse.add(this.m_equiset);
            return true;
        } else {
            return false;
        }
    }

    private Object key(List qvalues) {
        if (qvalues.isEmpty()) { return null; }
        QFrame target = ((QValue) qvalues.get(0)).getFrame();
        if (!target.getRoot().equals(getRoot())) { return null; }
        EquiSet eq = target.m_equiset;
        Object key = eq;
        for (int i = 0; i < qvalues.size(); i++) {
            QValue qv = (QValue) qvalues.get(i);
            Integer p = eq.partition(qv);
            if (p == null) {
                key = new CompoundKey(key, qv);
            } else {
                key = new CompoundKey(key, p);
            }
        }
        return key;
    }

    private List m_econds = new ArrayList();

    private boolean addEquals(List equals) {
        m_econds.clear();
        addInnerConditions(m_econds);
        for (int i = 0; i < m_econds.size(); i++) {
            Expression c = (Expression) m_econds.get(i);
            if (!m_generator.isSufficient(c)) {
                return false;
            }
            equals.addAll(m_generator.getEqualities(c));
        }
        return true;
    }

    private boolean nn(QValue qv) {
        List p = m_equiset.get(qv);
        for (int i = 0; i < m_nonnull.size(); i++) {
            QValue nn = (QValue) m_nonnull.get(i);
            if (nn.equals(qv) || p != null && p == m_equiset.get(nn)) {
                return true;
            }
        }
        if (m_parent == null) { return false; }
        return m_parent.nn(qv);
    }

    private boolean isNullable(List qvalues) {
        Column[] cols = columns(qvalues);
        if (cols == null) { return true; }
        return isNullable(cols);
    }

    private boolean isNullable(Column[] cols) {
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].isNullable()) {
                return true;
            }
        }
        return false;
    }

    private boolean isConnected(List from, List to) {
        Column[] fcols = columns(from);
        if (fcols == null) { return false; }
        Column[] tcols = columns(to);
        if (tcols == null) { return false; }
        return isConnected(fcols, tcols);
    }

    private Column[] columns(List qvalues) {
        if (qvalues.isEmpty()) { return null; }
        Column[] result = new Column[qvalues.size()];
        for (int i = 0; i < result.length; i++) {
            QValue v = (QValue) qvalues.get(i);
            Table t = m_generator.getRoot().getTable(v.getTable());
            if (t == null) { return null; }
            Column c = t.getColumn(v.getColumn());
            if (c == null) { return null; }
            result[i] = c;
        }
        return result;
    }

    private boolean isConnected(Column[] from, Column[] to) {
        if (Arrays.equals(from, to)) { return true; }
        ForeignKey fk = from[0].getTable().getForeignKey(from);
        if (fk == null) { return false; }
        UniqueKey uk = to[0].getTable().getUniqueKey(to);
        if (uk == null) { return false; }
        return isConnected(fk, uk);
    }

    private boolean isConnected(ForeignKey from, UniqueKey to) {
        UniqueKey uk = from.getUniqueKey();
        if (uk.equals(to)) { return true; }
        ForeignKey fk = uk.getTable().getForeignKey(uk.getColumns());
        if (fk == null) { return false; }
        else { return isConnected(fk, to); }
    }

    boolean innerizeAncestors(List values) {
        boolean modified = false;
        if (m_outer) {
            for (int i = 0; i < values.size(); i++) {
                QValue v = (QValue) values.get(i);
                if (m_parent.nn(v)) {
                    m_outer = false;
                    modified = true;
                }
            }
        }
        if (m_parent != null) {
            modified |= m_parent.innerizeAncestors(values);
        }
        return modified;
    }

    boolean contains(QValue value) {
        return contains(value.getFrame());
    }

    boolean contains(QFrame frame) {
        if (frame == null) { return false; }
        else if (frame.equals(this)) { return true; }
        else { return contains(frame.getParent()); }
    }

    boolean isConstrained(Set columns) {
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            if (!child.isConstrained(columns)) { return false; }
        }
        if (m_table != null) {
            if (!m_generator.isConstrained(m_table, columns)) {
                return false;
            }
        }
        if (m_tableExpr != null) { return false; }
        return true;
    }

    private Map m_canonframes = new HashMap();

    void shrink() {
        if (m_parent == null) {
            m_canonframes.clear();
            shrink(m_canonframes);
        }
    }

    private void shrink(Map canon) {
        if (m_table != null) {
            List framesets = m_equiset.getFrameSets();
            QFrame[] frames = (QFrame[]) canon.get(m_equiset);
            if (frames == null) {
                frames = new QFrame[framesets.size()];
                canon.put(m_equiset, frames);
            }

            QFrame dup = null;

            for (int i = 0; i < framesets.size(); i++) {
                List partition = (List) framesets.get(i);
                if (partition.contains(this)) {
                    dup = frames[i];
                    if (dup == null) {
                        dup = this;
                        frames[i] = dup;
                    }
                }
            }

            if (dup != null && !dup.equals(this)) {
                setDuplicate(dup);
            }
        }

        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            child.shrink(canon);
        }
    }

    private void setDuplicate(QFrame dup) {
        m_duplicate = dup;
    }

    QFrame getDuplicate() {
        if (m_duplicate == null) { return this; }
        return m_duplicate.getDuplicate();
    }

    public String toString() {
        return toString(0);
    }

    private static void indent(StringBuffer buf, int depth) {
        for (int i = 0; i < depth; i++) {
            buf.append("  ");
        }
    }

    private String toString(int depth) {
        StringBuffer result = new StringBuffer();
        indent(result, depth);
        result.append("frame ");
        result.append(isOuter() ? "O" : "I");
        result.append(m_outer ? "o" : "i");
        result.append(" ");
        result.append(m_expression.summary());
        result.append(" ");
        result.append(m_type);
        if (m_table != null) {
            result.append(" ");
            result.append(m_table);
            result.append(" ");
            result.append(alias());
        }
        if (m_values != null) {
            result.append(" ");
            result.append(m_values);
        }
        if (m_condition != null) {
            result.append(" cond ");
            result.append(m_condition);
        }
        if (m_nonnull != null) {
            if (m_parent == null || m_nonnull != m_parent.m_nonnull) {
                if (!m_nonnull.isEmpty()) {
                    result.append("\n");
                    indent(result, depth);
                }
                result.append(" nn ");
                result.append(m_nonnull);
            }
        }
        if (m_equiset != null) {
            if (m_parent == null || m_equiset != m_parent.m_equiset) {
                if (!m_equiset.isEmpty()) {
                    result.append("\n");
                    indent(result, depth);
                }
                result.append(" eq ");
                result.append(m_equiset);
            }
        }
        if (getChildren().isEmpty()) {
            return result.toString();
        }
        result.append(" {");
        List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            QFrame child = (QFrame) children.get(i);
            result.append("\n");
            result.append(child.toString(depth + 1));
        }
        result.append("\n");
        indent(result, depth);
        result.append("}");
        return result.toString();
    }

}
