/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver;

import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.kernel.Kernel;

/**
 * Wraps a Link so that it becomes invisible if
 * there is no user logged in.
 */
public class LoggedInLinkWrapper extends Link {

    public LoggedInLinkWrapper(PrintListener l) {
        super(l);
    }

    public boolean isVisible(PageState state) {
        if ( Kernel.getContext().getParty() == null ) {
            return false;
        } else {
            return true;
        }
    }

}
