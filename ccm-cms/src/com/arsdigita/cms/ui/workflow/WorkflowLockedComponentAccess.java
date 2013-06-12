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
import com.arsdigita.toolbox.Security;
import com.arsdigita.toolbox.ui.ComponentAccess;
import org.apache.log4j.Logger;

/**
 * A <code>ComponentAccess</code> implementation that respects workflow
 *
 * @author Stanislav Freidin
 * @author Uday Mathur
 * @version $Id: WorkflowLockedComponentAccess.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class WorkflowLockedComponentAccess extends ComponentAccess {

    private static final Logger s_log =
            Logger.getLogger(WorkflowLockedComponentAccess.class);
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
        super(c, check);
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
    @Override
    public boolean canAccess(PageState state, Security security) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        ContentItem item = (ContentItem) m_itemModel.getSelectedObject(state);


        if (isVisible(state) == true) {
            if (super.canAccess(state, security)) {
                boolean smCheck = sm.canAccess(state.getRequest(), 
                                               SecurityManager.EDIT_ITEM,
                        item);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Superclass security check passed. "
                                + "SecurityManager check is " + smCheck);
                }
                return smCheck;
            }
        }

        return false;
    }

    /**
     * Override this method to change visiblity of action link created by
     * SecurityPropertyEditor add-method. If this method returns false, the
     * link will be hidden, p.ex. to hide a delete link if the component is
     * already empty.
     *
     * @param state The page state
     * @return true for default behavior
     */
    public boolean isVisible(PageState state) {
        return true;
    }
}
