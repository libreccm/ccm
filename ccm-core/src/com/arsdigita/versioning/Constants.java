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

// new versioning

/**
 * Shared string constants and such.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-04-22
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/
interface Constants {

    String PDL_MODEL = "com.arsdigita.versioning";

    // Txn attributes
    String MOD_USER     = "modUser";
    String TIMESTAMP    = "timestamp";
    String MODIFYING_IP = "modifyingIP";

    // DataObjectChange attributes
    String CHANGE_DATA_TYPE = PDL_MODEL + ".DataObjectChange";
    String ID               = "id";
    String OBJ_ID           = "oid";
    String OPERATIONS       = "operations";
    String TXN              = "txn";

    // Operation attributes
    String ATTRIBUTE  = "attribute";
    String CHANGESET  = "changeset";
    String EVENT_TYPE = "eventType";
    String JAVACLASS  = "javaclass";
    String VALUE      = "value";
    String SUBTYPE    = "subtype";

    // possible operation subtypes
    OpType GENERIC_OPERATION = new OpType(PDL_MODEL + ".GenericOperation", 1);
    OpType CLOB_OPERATION    = new OpType(PDL_MODEL + ".ClobOperation",    2);
    OpType BLOB_OPERATION    = new OpType(PDL_MODEL + ".BlobOperation",    3);

    String OID_TYPE         = "OID";
    String DATA_OBJECT_TYPE = "DataObject";

    String LINE_SEP = System.getProperty("line.separator");

    // Txn attributes
    String TAG_DATA_TYPE = PDL_MODEL + ".Tag";
    String TAG         = "tag";
    String TAGS        = "tags";
    String TAGGED_OID  = "taggedOID";
    String TAGS_TAG = TAGS + "." + TAG;
    String TAGS_TAGGED_OID = TAGS + "." + TAGGED_OID;

    // stuff used by VersioningEventProcessor and Versions
    String TXN_DATA_TYPE = PDL_MODEL + ".Txn";
}
