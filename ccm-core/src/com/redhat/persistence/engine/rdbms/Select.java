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

import com.redhat.persistence.Signature;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Query;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

class Select extends Operation {

    public final static String versionId = 
            "$Id: Select.java 738 2005-09-01 12:36:52Z sskracic $" +
            " by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Query m_query;
    private Signature m_sig ;

    public Select(RDBMSEngine engine, Signature sig, Expression expr) {
        this(engine, sig.makeQuery(expr));
        m_sig = sig;
    }

    public Select(RDBMSEngine engine, Query query) {
        super(engine, new Environment(engine, null));
        m_query = query;
    }

    public Query getQuery() {
        return m_query;
    }

    public Signature getSignature() {
        return m_sig;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
