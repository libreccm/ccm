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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.cms.Folder;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * 
 *
 */
final class ChildUniqueValidationListener implements ParameterListener {

    private final FolderRequestLocal m_parent;

    /**
     * Constructor.
     * @param parent 
     */
    public ChildUniqueValidationListener(final FolderRequestLocal parent) {
        m_parent = parent;
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    public final void validate(final ParameterEvent e)
            throws FormProcessException {
        final PageState state = e.getPageState();
        final String name = (String) e.getParameterData().getValue();

        if (name != null) {
            validateNameUniqueness(m_parent.getFolder(state), name);
        }
    }

    /**
     * 
     * @param parent
     * @param name
     * @throws FormProcessException 
     */
    private void validateNameUniqueness(final Folder parent,
                                        final String name)
            throws FormProcessException {
        final DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.cms.validateUniqueItemName");
        query.setParameter("parentId", parent.getID());
        query.setParameter("name", name);

        if (query.next()) {
            long count = ((BigDecimal) query.get("size")).longValue();
            
            query.close();
            
            if (count > 0) {
                throw new FormProcessException
                    ("An item with this name already exists");
            }
        } else {
            throw new IllegalStateException();
        }
    }
}
