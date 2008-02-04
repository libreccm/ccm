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

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: Literal.java 737 2005-09-01 12:27:29Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    void frame(Generator gen) {
        QFrame frame = gen.frame(this, null);
        List result = new ArrayList();
        Object key = gen.level > 0 ? null : getBindKey(gen);
        convert(m_value, result, gen.getRoot(), key);
        if (result.size() == 0) {
            throw new IllegalStateException
                ("unable to convert value: " + m_value);
        }
        List values = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            Code c = (Code) result.get(i);
            QValue v = frame.getValue(c);
            values.add(v);
            if (c.isNull()) {
                gen.addNull(this, v);
            } else {
                gen.addNonNull(this, v);
            }
        }
        frame.setValues(values);
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        List values = new ArrayList();
        convert(m_value, values, gen.getRoot(), getBindKey(gen));
        for (int i = 0; i < values.size(); i++) {
            Code c = (Code) values.get(i);
            gen.hash(c.getSQL());
            gen.bind(c);
        }
        gen.hash(getClass());
    }

    Object getBindKey(Generator gen) {
        return gen.id(this);
    }

    static void convert(Object value, List result, Root root, Object key) {
        convert(value, result, root, key, 0);
    }

    static int convert(Object value, List result, Root root, Object key,
                       int bindcount) {
        if (value == null) {
            result.add(Code.NULL);
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            Code sql = new Code("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                List single = new ArrayList();
                bindcount = convert(it.next(), single, root, key, bindcount);
                if (single.size() != 1) {
                    throw new IllegalStateException
                        ("can't deal with collection of compound objects");
                }
                sql = sql.add((Code) single.get(0));
                if (it.hasNext()) {
                    sql = sql.add(",");
                } else {
                    sql = sql.add(")");
                }
            }
            result.add(sql);
        } else {
            Adapter ad = root.getAdapter(value.getClass());
            PropertyMap pmap = ad.getProperties(value);
            if (pmap.getObjectType().isCompound()) {
                bindcount = convert(pmap, result, root, key, bindcount);
            } else {
                if (ad.isBindable()) {
                    Object k = key == null ? null :
                        new CompoundKey(key, new Integer(bindcount));
                    Code.Binding b = new Code.Binding
                        (k, value, ad.defaultJDBCType());
                    result.add(new Code("?", Collections.singletonList(b)));
                    bindcount++;
                } else {
                    result.add(new Code(ad.getLiteralCode(value)));
                }
            }
        }

        return bindcount;
    }

    private static int convert(PropertyMap pmap, List result, Root root,
                               Object key, int bindcount) {
        Collection props = Code.properties(pmap.getObjectType());
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            bindcount = convert(pmap.get(prop), result, root, key, bindcount);
        }

        return bindcount;
    }

    public String toString() {
        if (m_value instanceof String) {
            return "\"" + m_value + "\"";
        } else {
            return "" + m_value;
        }
    }

    String summary() {
        return "" + this;
    }

}
