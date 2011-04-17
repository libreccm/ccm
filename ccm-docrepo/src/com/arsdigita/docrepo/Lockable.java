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
package com.arsdigita.docrepo;

import com.arsdigita.kernel.Party;

/**
 * This interface specifies functionality to lock a
 * an object by a user.
 *
 *
 *  <p>
 *    <b>Stability: <font color="red">Experimental</font></b>
 *  </p>
 * @author Stefan Deusch (stefan@arsdigita.com)
 */
public interface Lockable {

    /**
     * Applies a lock to an object
     * owned by the user
     */
    public void lock(Party user);

    /**
     * Party who previously locked
     * object, unlocks it now.
     */
    public void unlock(Party user);

    /**
     * Checks whether the task is locked by a user.
     * @return <code>true</code> if the  task is locked
     * by a user; <code>false</code> otherwise.
     */
    public boolean isLocked();

    /**
     * Retrieves the user who locked the process.
     * @return  the user who locked the process.
     *
     */
    public Party getLockedParty();

}
