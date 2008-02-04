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

import com.redhat.persistence.Condition;

/**
 * CompoundJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

abstract class CompoundJoin extends Join {

    public final static String versionId = "$Id: CompoundJoin.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static class Type {}

    public static final Type INNER = new Type() {
            public String toString() {
                return "join";
            }
        };
    public static final Type CROSS = new Type() {
            public String toString() {
                return "cross join";
            }
        };
    public static final Type LEFT = new Type() {
            public String toString() {
                return "left join";
            }
        };
    public static final Type RIGHT = new Type() {
            public String toString() {
                return "right join";
            }
        };

    private Join m_left;
    private Type m_type;
    private Join m_right;
    private Condition m_condition;

    public CompoundJoin(Join left, Type type, Join right,
                        Condition condition) {
        m_left = left;
        m_type = type;
        m_right = right;
        m_condition = condition;
    }

    public Join getLeft() {
        return m_left;
    }

    public Type getType() {
        return m_type;
    }

    public Join getRight() {
        return m_right;
    }

    public Condition getCondition() {
        return m_condition;
    }

}

class CrossJoin extends CompoundJoin {

    public CrossJoin(Join left, Join right) {
        super(left, CompoundJoin.CROSS, right, null);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class InnerJoin extends CompoundJoin {

    public InnerJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.INNER, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class LeftJoin extends CompoundJoin {

    public LeftJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.LEFT, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}

class RightJoin extends CompoundJoin {

    public RightJoin(Join left, Join right, Condition cond) {
        super(left, CompoundJoin.RIGHT, right, cond);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
