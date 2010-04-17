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
package com.redhat.persistence.engine.rdbms;

import java.sql.SQLException;

/**
 * StatementLifecycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: StatementLifecycle.java 737 2005-09-01 12:27:29Z sskracic $
 **/

public interface StatementLifecycle {

    void beginPrepare();
    void endPrepare();
    void endPrepare(SQLException e);

    void beginSet(int pos, int type, Object obj);
    void endSet();
    void endSet(SQLException e);

    void beginExecute();
    void endExecute(int updateCount);
    void endExecute(SQLException e);

    void beginNext();
    void endNext(boolean more);
    void endNext(SQLException e);

    void beginGet(String column);
    void endGet(Object result);
    void endGet(SQLException e);

    void beginClose();
    void endClose();
    void endClose(SQLException e);

}
