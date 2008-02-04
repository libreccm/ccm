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
 */

package com.arsdigita.london.util.ui.event;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PageEvent;

public class DomainObjectActionEvent extends PageEvent {
    
    private DomainObject m_dobj;
    private String m_action;
    
    public DomainObjectActionEvent(Object source,
                                   PageState state,
                                   DomainObject dobj,
                                   String action) {
        super(source, state);

        m_dobj = dobj;
        m_action = action;
    }
    
    public DomainObject getObject() {
        return m_dobj;
    }

    public String getAction() {
        return m_action;
    }
}
