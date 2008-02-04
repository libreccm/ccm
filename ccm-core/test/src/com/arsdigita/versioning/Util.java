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

import com.arsdigita.developersupport.SQLDebugger;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Static helper methods.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-20
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class Util {
    private final static Logger s_log = Logger.getLogger(Util.class);

    private Util() {}

    public static DataObject newDataObject(String objectType) {
        Assert.exists(objectType, String.class);
        OID oid = new OID(objectType, Sequence.next());
        return SessionManager.getSession().create(oid);
    }

    public static void dumpChangesTable(String msg) {
        String changesQuery =
            "select id, obj_id, txn_id\n" +
            "from vcx_obj_changes\n" +
            "order by id";
        String[] changesQueryColumns = {"id", "obj_id", "txn_id"};
        SQLDebugger.dumpQuery(msg, changesQueryColumns, changesQuery);
    }

    public static void dumpGenericOperations(String msg) {
        String[] opQueryColumns =
            {"operation_id", "attribute", "change_id",
             "event", "value", "classname"};
        String opQuery =
            "select\n" +
            "  op.id as operation_id, attribute, change_id,\n" +
            "  ev.name as event, gop.value, jc.name as classname\n" +
            "from\n" +
            "  vcx_operations op,\n" +
            "  vcx_generic_operations gop,\n" +
            "  vcx_event_types ev,\n" +
            "  vcx_java_classes jc\n" +
            "where\n" +
            "  op.id = gop.id\n" +
            "  and ev.id = op.event_type_id\n" +
            "  and op.class_id = jc.id\n" +
            "order by op.id, change_id";
        SQLDebugger.dumpQuery(msg, opQueryColumns, opQuery);
    }

    public static void dumpClobOperations(String msg) {
        String[] opQueryColumns =
            {"operation_id", "attribute", "change_id",
             "event", "value", "classname"};
        String opQuery =
            "select\n" +
            "  op.id as operation_id, attribute, change_id,\n" +
            "  ev.name as event, cob.value, jc.name as classname\n" +
            "from\n" +
            "  vcx_operations op,\n" +
            "  vcx_clob_operations cob,\n" +
            "  vcx_event_types ev,\n" +
            "  vcx_java_classes jc\n" +
            "where\n" +
            "  op.id = cob.id\n" +
            "  and ev.id = op.event_type_id\n" +
            "  and op.class_id = jc.id\n" +
            "order by op.id, change_id";
        SQLDebugger.dumpQuery(msg, opQueryColumns, opQuery);
    }

    public static void dumpTxns(String msg) {
        String[] columns =
            {"id", "modifying_ip", "timestamp", "modifying_user"};
        String query =
            "select id, modifying_ip, timestamp, modifying_user\n" +
            "from vcx_txns\n" +
            "order by id";
        SQLDebugger.dumpQuery(msg, columns, query);
    }

    public static void dumpTags(String msg) {
        String[] columns = {"id", "tag", "tagged_oid", "txn_id"};
        String query =
            "select id, tag, tagged_oid, txn_id\n" +
            "from vcx_tags\n" +
            "order by id";
        SQLDebugger.dumpQuery(msg, columns, query);
    }

    public static void dumpVersioningLog(String msg) {
        dumpTxns(msg);
        dumpTags(msg);
        dumpChangesTable(msg);
        dumpGenericOperations(msg);
        dumpClobOperations(msg);
    }

    public static void dumpVT1(String msg) {
        String[] columns = {"id", "name", "content", "int_attr"};
        SQLDebugger.dumpTable(msg, columns, "te_vt1");
    }

    public static void dumpVT2(String msg) {
        String[] columns = {"id", "name", "unver_attr"};
        SQLDebugger.dumpTable(msg, columns, "te_vt2");
    }

    public static void dumpVT3(String msg) {
        String[] columns = {"id", "name", "rt1_id"};
        SQLDebugger.dumpTable(msg, columns, "te_vt3");
    }

    public static void dumpVT4(String msg) {
        String[] columns =
            {"id", "j_big_decimal", "j_big_integer", "j_boolean",
            "j_byte", "j_character", "j_float"};
        SQLDebugger.dumpTable(msg, columns, "te_vt4");
    }

    public static void dumpRT1(String msg) {
        String[] columns = {"id", "name", "int_attr"};
        SQLDebugger.dumpTable(msg, columns, "te_rt1");
    }

    public static void dumpC1(String msg) {
        String[] columns = {"id", "name", "composite_id"};
        SQLDebugger.dumpTable(msg, columns, "te_c1");
    }

    public static void dumpC2(String msg) {
        String[] columns = {"id", "name", "composite_id"};
        SQLDebugger.dumpTable(msg, columns, "te_c2");
    }

    public static void dumpUVCT1(String msg) {
        String[] columns = {"id", "name", "composite_id"};
        SQLDebugger.dumpTable(msg, columns, "te_uvct1");
    }

    public static void dumpUVCT2(String msg) {
        String[] columns = {"id", "name", "composite_id"};
        SQLDebugger.dumpTable(msg, columns, "te_uvct2");
    }
}
