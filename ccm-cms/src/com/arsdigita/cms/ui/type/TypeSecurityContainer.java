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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.kernel.Party;
import com.arsdigita.toolbox.ui.SecurityContainer;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TypeSecurityContainer.java 1942 2009-05-29 07:53:23Z terry $
 */
public final class TypeSecurityContainer extends SecurityContainer {

    private static Logger s_log = Logger.getLogger(TypeSecurityContainer.class);

    public TypeSecurityContainer(final Component c) {
        super(c);
    }

    protected final boolean canAccess(final Party party,
                                      final PageState state) {
        final SecurityManager sm = Utilities.getSecurityManager(state);

        return sm.canAccess(party, SecurityManager.CONTENT_TYPE_ADMIN);
    }
}
