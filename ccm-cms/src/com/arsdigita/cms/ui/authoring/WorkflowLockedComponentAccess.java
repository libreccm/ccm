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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.toolbox.Security;
import com.arsdigita.toolbox.ui.ComponentAccess;

/**
 * A <code>ComponentAccess</code> implementation that respects workflow
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: WorkflowLockedComponentAccess.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated Use com.arsdigita.cms.workflow.ui.WorkflowLockedComponentAccess
 */
public class WorkflowLockedComponentAccess extends ComponentAccess {

    ItemSelectionModel m_itemModel;

    /**
     * Constructor. 
     * 
     * @param c The component
     */
    public WorkflowLockedComponentAccess(Component c, ItemSelectionModel i) {
        super(c);
        m_itemModel = i;
    }

    /**
     * Constructor. 
     * 
     * @param c The component
     * @param check An access check
     */
    public WorkflowLockedComponentAccess(Component c, 
                                         String check, 
                                         ItemSelectionModel i) {
        super(c,check);
        m_itemModel = i;
    }

    /**
     * Check if this item is locked from the workflow module. In
     * addition check if all the access checks registered to the
     * component pass.
     *
     * @param state The page state
     * @param security The Security implementation that will perform
     *    the access checks
     * @return true if all the access checks pass, false otherwise
     * */
    public boolean canAccess(PageState state, Security security) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        ContentItem item = (ContentItem)m_itemModel.getSelectedObject(state);

        if (super.canAccess(state, security)) {
            return sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM,
                                item);
        }

        return false;
    }
}
