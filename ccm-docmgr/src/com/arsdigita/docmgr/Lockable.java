/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;

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
