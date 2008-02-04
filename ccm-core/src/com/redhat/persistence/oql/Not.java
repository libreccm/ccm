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

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class Not extends UnaryCondition {

    public final static String versionId = "$Id: Not.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public Not(Expression expr) {
        super(expr);
    }

    void frame(Generator gen) {
        gen.addBoolean(m_operand);
        super.frame(gen);
        gen.addNulls(this, gen.getNonNull(m_operand));
        gen.addNonNulls(this, gen.getNull(m_operand));
    }

    Code emit(Generator gen) {
        Code sql = m_operand.emit(gen);
        if (sql.isTrue()) {
            return Code.FALSE;
        } else if (sql.isFalse()) {
            return Code.TRUE;
        } else {
            return new Code("not (").add(sql).add(")");
        }
    }

    public String toString() {
        return "not (" + m_operand + ")";
    }

    String summary() { return "not"; }

}
