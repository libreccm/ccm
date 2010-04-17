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

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Expression.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public abstract class Expression {

    public static abstract class Switch {

        public abstract void onCondition(Condition c);
        public abstract void onVariable(Variable r);
        public abstract void onValue(Value v);
        public abstract void onPassthrough(Passthrough p);

    }

    public abstract void dispatch(Switch sw);

    public static class Variable extends Expression {
        private Path m_path;

        private Variable(Path path) {
            m_path = path;
        }

        public void dispatch(Switch sw) {
            sw.onVariable(this);
        }

        public Path getPath() {
            return m_path;
        }

        public String toString() {
            return "" + m_path;
        }

    }

    public static class Value extends Expression {

        private Object m_value;

        private Value(Object value) {
            m_value = value;
        }

        public void dispatch(Switch sw) {
            sw.onValue(this);
        }

        public Object getValue() {
            return m_value;
        }

        public String toString() {
            return "" + m_value;
        }

    }

    public static class Passthrough extends Expression {

        private String m_expr;

        private Passthrough(String expr) {
            m_expr = expr;
        }

        public void dispatch(Switch sw) {
            sw.onPassthrough(this);
        }

        public String getExpression() {
            return m_expr;
        }

        public String toString() {
            return m_expr;
        }

    }

    public static final Variable variable(Path path) {
        return new Variable(path);
    }

    public static final Value value(Object value) {
        return new Value(value);
    }

    public static final Passthrough passthrough(String expr) {
        return new Passthrough(expr);
    }

}
