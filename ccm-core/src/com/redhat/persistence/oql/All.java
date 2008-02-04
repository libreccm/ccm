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
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: All.java 737 2005-09-01 12:27:29Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_type;
    private Map m_bindings;
    private Expression m_scope;
    private boolean m_substitute;

    public All(String type) {
        this(type, Collections.EMPTY_MAP, null, false);
    }

    All(String type, Map bindings, Expression scope, boolean substitute) {
        m_type = type;
        m_bindings = bindings;
        m_scope = scope == null ? this : scope;
        m_substitute = substitute;
    }

    String getType() {
        return m_type;
    }

    void frame(Generator gen) {
        final ObjectType type = gen.getType(m_type);
        ObjectMap map = type.getRoot().getObjectMap(type);
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(type, null);

        if (block == null) {
            QFrame frame = gen.frame(this, type);
            frame.setTable(Code.table(map).getName());
            frame.setValues(columns);
        } else if (m_substitute || gen.isBoolean(this)) {
            Static all = new Static
                (block.getSQL(), null, false, m_bindings, m_scope);
            all.frame(gen);
            gen.setSubstitute(this, all);
        } else {
            QFrame frame = gen.frame(this, type);
            Static all = new Static
                (block.getSQL(), columns, false, m_bindings, m_scope) {
                protected ObjectType getType() { return type; }
                protected boolean hasType() { return true; }
            };
            all.frame(gen);
            QFrame child = gen.getFrame(all);
            frame.addChild(child);
            frame.setValues(child.getValues());
            for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
                Path p = (Path) it.next();
                frame.addMapping(p, block.getMapping(p).getPath());
            }
        }
    }

    Code emit(Generator gen) {
        Expression sub = gen.getSubstitute(this);
        if (sub != null) {
            return sub.emit(gen);
        }
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        ObjectType type = gen.getType(m_type);
        gen.hash(type);
        gen.hash(getClass());
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
