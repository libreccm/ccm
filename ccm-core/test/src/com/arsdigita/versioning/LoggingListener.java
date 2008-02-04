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
import org.apache.log4j.Logger;

/**
 * This is for debugging.
 * 
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-28
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/
public final class LoggingListener extends RollbackAdapter {
    private final static Logger s_log = Logger.getLogger(LoggingListener.class);

    private boolean m_isOn = true;
    private boolean m_useLog4j = false;
    private String m_msg;

    public LoggingListener(String msg) {
        m_msg = msg;
    }


    public void onStart(OID oid) {
        log(m_msg + " onStart: ", oid);
    }

    public void onEnqueue(OID oid) {
        log("onEnqueue: ", oid);
    }

    public void onSet(OID oid, String property, Object value) {
        log("onSet: ", oid, property, value);
    }

    public void onAdd(OID oid, String property, Object value) {
        log("onAdd: ", oid, property, value);
    }

    public void onRemove(OID oid, String property, Object value) {
        log("onRemove: ", oid, property, value);
    }

    public void onDequeue(OID oid) {
        log("onDequeue: ", oid);
    }

    public void onFinish(OID oid) {
        log(m_msg + " onFinish: ", oid);
    }

    private void log(String msg) {
        if ( !m_isOn) return;

        if ( m_useLog4j ) {
            s_log.debug(msg);
        } else {
            System.err.println(msg);
        }
    }

    private void log(String msg, OID oid) {
        StringBuffer sb = new StringBuffer(200);
        sb.append(msg).append("oid=").append(Adapter.serialize(oid));
        log(sb.toString());
    }

    private void log(String msg, OID oid, String property, Object value) {
        StringBuffer sb = new StringBuffer(200);
        sb.append(msg).append("oid=").append(Adapter.serialize(oid));
        sb.append(", property=").append(property);
        log(sb.toString());
    }
}
