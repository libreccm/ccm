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
package com.arsdigita.versioning;

import com.arsdigita.persistence.OID;

// new versioning

/**
 * This is an observer that gets notified of events that occur in the process of
 * rolling back a data object.
 *
 * @see Versions#computeDifferences(OID, java.math.BigInteger,
 * RollbackListener)
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/
interface RollbackListener {

    /**
     * Called when the rollback process starts.
     **/
    void onStart();

    /**
     * Called when the rollback process is finished.
     **/
    void onFinish();

    /**
     * Called when a data object is traversed and added to the queue of objects
     * to be rolled back.
     **/
    void onEnqueue(OID oid);

    /**
     * Called when a proxy data object is dequeued in order to be
     * processed.
     **/
    void onDequeue(OID oid);

    /**
     * Called when is a SET event is found in the versioning log.
     **/
    void onUndoSet(OID oid, String property, Object value);

    /**
     * Called when an ADD event is found in the versioning log.
     **/
    void onUndoAdd(OID oid, String property, Object value);

    /**
     * Called when a REMOVE event is found in the versioning log.
     **/
    void onUndoRemove(OID oid, String property, Object value);


    /**
     * Called when the process of computing the diff is about to start.
     **/
    void onDiffStart();

    /**
     * Called when the diff computation is finished.
     **/
    void onDiffFinish();

    /**
     * Called when the reification of proxy data objects is about to start.
     **/
    void onReifyStart();

    /**
     * Called when the reification of proxy data objects is finished.
     **/
    void onReifyFinish();

    /**
     * Called when a proxy data object is about to be reified.
     **/
    void onReifyStart(OID oid, String preState);

    /**
     * Called after a proxy data object has been reified.
     **/
    void onReifyFinish(OID oid, String postState);

    /**
     * Called when the process of apply the diff is about to start.
     **/
    void onApplyStart();

    /**
     * Called after the diff has been applied.
     **/
    void onApplyFinish();

    void onApplyStart(OID oid, String state);

    void onApplyFinish(OID oid);

    void onSet(OID oid, String property, Object value);

    void onAdd(OID oid, String property, Object value);

    void onRemove(OID oid, String property, Object value);

    void onTerminalStart();

    void onTerminalFinish();

    void onDelete(OID oid);

    void onCreate(OID oid);
}
