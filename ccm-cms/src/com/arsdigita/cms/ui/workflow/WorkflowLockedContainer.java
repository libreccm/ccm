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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.kernel.Party;
import com.arsdigita.toolbox.ui.SecurityContainer;


public class WorkflowLockedContainer extends SecurityContainer {
    ItemSelectionModel m_itemModel;
    public WorkflowLockedContainer(ItemSelectionModel m) {
        super();
        m_itemModel = m;
    }

    public WorkflowLockedContainer(Component c, ItemSelectionModel m) {
        super(c);
        m_itemModel = m;
    }

    public void setItemModel(ItemSelectionModel m) {
        m_itemModel = m;
    }

    protected boolean canAccess(Party party, PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        ContentItem item = (ContentItem)m_itemModel.getSelectedObject(state);

        return sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM,
                            item);
    }
}
