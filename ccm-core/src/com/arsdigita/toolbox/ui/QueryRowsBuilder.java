/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RowSequenceBuilder;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;

/** 
 *  Adapter around Session.  A closure fixating the queryName argument
 *  to Session.retrieveQuery */
public class QueryRowsBuilder implements RowSequenceBuilder {

    private String m_typeName = null;

    public QueryRowsBuilder(String typeName) {
        m_typeName = typeName;
    }

    /** Build a row sequence as a DataCollection.  The {@index
     * com.arsdigita.infrastructure.RowSequence} interface mandates a
     * {@index com.arsdigita.bebop.PageState} argument because some
     * builder might use it.  */
    public DataQuery makeRowSequence(PageState state) {
        return SessionManager.getSession().retrieveQuery(m_typeName);
    }
}
