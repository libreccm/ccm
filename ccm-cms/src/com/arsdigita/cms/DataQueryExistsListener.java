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
package com.arsdigita.cms;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.persistence.DataQuery;


/**
 * <p>Verifies that a specified
 * {@link com.arsdigita.persistence.DataQuery data query} has no results.
 * This is useful for making sure emails are unique in the database.</p>
 *
 * <p>Users of this class must override the method {@link #getDataQuery} which 
 * specifies the data query to check.</p>
 *
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: DataQueryExistsListener.java 2090 2010-04-17 08:04:14Z pboy $
 */
public abstract class DataQueryExistsListener 
        implements FormValidationListener {

    /** */
    protected String m_errorMsg;


    /**
     * @param msg An error message
     */
    public DataQueryExistsListener(String msg) {
        m_errorMsg = msg;
    }

    /**
     * 
     * @param event
     * @return 
     */
    public abstract DataQuery getDataQuery(FormSectionEvent event);

    /**
     * 
     * @param event
     * @throws FormProcessException 
     */
    @Override
    public void validate(FormSectionEvent event) throws FormProcessException {
        DataQuery dq = getDataQuery(event);
        if ( dq.next() ) {
            dq.close();
            throw new FormProcessException(m_errorMsg);
        }
        dq.close();
    }
}
