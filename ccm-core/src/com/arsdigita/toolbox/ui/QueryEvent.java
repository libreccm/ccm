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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PageEvent;
import com.arsdigita.persistence.DataQuery;

/**
 * This event is fired by the {@link DataQuery} class
 *
 * @see DataQuery
 * @see QueryListener
 */
public class QueryEvent extends PageEvent {

    public static final String versionId = "$Id: QueryEvent.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private DataQuery m_query;

    public QueryEvent(Component source, PageState s, DataQuery q) {
        super(source, s);
        m_query = q;
    }

    public DataQuery getDataQuery() {
        return m_query;
    }

}
