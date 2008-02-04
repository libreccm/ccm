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
 * This is a no-op adapter for the {@link RollbackListener} interface. This
 * class is provided as a convenience for easily creating rollback listeners by
 * extending this class and overriding only the methods of interest.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-28
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/
class RollbackAdapter implements RollbackListener {

    public void onAdd(OID oid, String property, Object value) {}
    public void onApplyFinish() {}
    public void onApplyFinish(OID oid) {}
    public void onApplyStart() {}
    public void onApplyStart(OID oid, String state) {}
    public void onCreate(OID oid) {}
    public void onDelete(OID oid) {}
    public void onDequeue(OID oid) {}
    public void onDiffFinish() {}
    public void onDiffStart() {}
    public void onEnqueue(OID oid) {}
    public void onFinish() {}
    public void onReifyFinish() {}
    public void onReifyFinish(OID oid, String postState) {}
    public void onReifyStart() {}
    public void onReifyStart(OID oid, String preState) {}
    public void onRemove(OID oid, String property, Object value) {}
    public void onSet(OID oid, String property, Object value) {}
    public void onStart() {}
    public void onTerminalFinish() {}
    public void onTerminalStart() {}
    public void onUndoAdd(OID oid, String property, Object value) {};
    public void onUndoRemove(OID oid, String property, Object value) {};
    public void onUndoSet(OID oid, String property, Object value) {};
}
