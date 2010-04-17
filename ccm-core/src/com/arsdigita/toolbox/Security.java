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
package com.arsdigita.toolbox;

import com.arsdigita.kernel.Party;

/**
 * <p>An interface for making access checks.</p>
 *
 * @author Michael Pih 
 * @version $Id: Security.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface Security {

    /**
     * Determine whether a party has access to a particular action.
     *
     * @param party The party
     * @param action The action
     * @return true if the party has access, false otherwise
     */
    public boolean canAccess(Party party, String action);
}
