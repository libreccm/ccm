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

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class Sort extends Expression {

    

    public static class Order {
        private Order() {}
    }

    public static final Order ASCENDING = new Order();
    public static final Order DESCENDING = new Order();

    private Expression m_query;
    private Expression m_key;
    private Order m_order;

    public Sort(Expression query, Expression key, Order order) {
        m_query = query;
        m_key = key;
        m_order = order;
    }

    public Sort(Expression query, Expression key) {
        this(query, key, ASCENDING);
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setValues(query.getValues());
        frame.setMappings(query.getMappings());
        frame.setOrder(m_key, m_order == ASCENDING);
        gen.addUses(this, gen.getUses(m_query));
        gen.push(frame);
        try {
            m_key.frame(gen);
            gen.addUses(this, gen.getUses(m_key));
        } finally {
            gen.pop();
        }
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        m_key.hash(gen);
        gen.hash(m_order == ASCENDING);
        gen.hash(getClass());
    }

    String summary() {
        return "sort";
    }

    public String toString() {
        return "sort(" + m_query + ", " + m_key + ")";
    }

}
