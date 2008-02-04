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
package com.redhat.persistence.profiler.rdbms;

import com.redhat.persistence.engine.rdbms.RDBMSProfiler;
import com.redhat.persistence.engine.rdbms.RDBMSStatement;
import com.redhat.persistence.engine.rdbms.StatementLifecycle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CompoundProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class CompoundProfiler implements RDBMSProfiler {

    public final static String versionId = "$Id: CompoundProfiler.java 817 2005-09-15 14:25:13Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private List m_children = new ArrayList();

    public CompoundProfiler() {}

    public void add(RDBMSProfiler child) {
        m_children.add(child);
    }

    public StatementLifecycle getLifecycle(Connection conn, RDBMSStatement stmt) {
        CompoundLifecycle result = null;
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            RDBMSProfiler child = (RDBMSProfiler) it.next();
            StatementLifecycle sl = child.getLifecycle(conn, stmt);
            if (sl == null) { continue; }
            if (result == null) { result = new CompoundLifecycle(); }
            result.add(sl);
        }
        return result;
    }

    private static class CompoundLifecycle implements StatementLifecycle {

        private List m_children = new ArrayList();

        public void add(StatementLifecycle child) {
            m_children.add(child);
        }

        public void beginPrepare() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginPrepare();
            }
        }

        public void endPrepare() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endPrepare();
            }
        }

        public void endPrepare(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endPrepare(e);
            }
        }

        public void beginSet(int pos, int type, Object obj) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginSet(pos, type, obj);
            }
        }

        public void endSet() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endSet();
            }
        }

        public void endSet(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endSet(e);
            }
        }

        public void beginExecute() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginExecute();
            }
        }

        public void endExecute(int updateCount) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endExecute(updateCount);
            }
        }

        public void endExecute(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endExecute(e);
            }
        }

        public void beginNext() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginNext();
            }
        }

        public void endNext(boolean more) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endNext(more);
            }
        }

        public void endNext(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endNext(e);
            }
        }

        public void beginGet(String column) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginGet(column);
            }
        }

        public void endGet(Object result) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endGet(result);
            }
        }

        public void endGet(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endGet(e);
            }
        }

        public void beginClose() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginClose();
            }
        }

        public void endClose() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endClose();
            }
        }

        public void endClose(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endClose(e);
            }
        }

    }

}
