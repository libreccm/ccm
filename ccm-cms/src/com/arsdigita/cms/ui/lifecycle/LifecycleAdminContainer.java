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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.kernel.Party;
import com.arsdigita.toolbox.ui.SecurityContainer;


/**
 * Security container that wraps the canAdministerLifecycles access check
 * around its components.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #8 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class LifecycleAdminContainer extends SecurityContainer {

    public static final String versionId = "$Id: LifecycleAdminContainer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    /**
     * This default constructor should be followed by calls to
     * <code>add</code>.
     */
    public LifecycleAdminContainer() {
        super();
    }

    /**
     * Create a <code>SecurityContainer</code> around a child component.
     *
     * @param c The child component
     */
    public LifecycleAdminContainer(Component c) {
        super(c);
    }

    /**
     * Returns true if the current user can access the child component.
     *
     * @param user The user
     * @param state The page state
     * @return true if the access checks pass, false otherwise
     */
    protected boolean canAccess(Party party, PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        return sm.canAccess(party, SecurityManager.LIFECYCLE_ADMIN);
    }

}
