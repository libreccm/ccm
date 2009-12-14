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
package com.arsdigita.db;

import com.arsdigita.persistence.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 *
 * Central location for obtaining database connection.
 *
 * @author David Dao
 * @version $Id: ConnectionManager.java 738 2005-09-01 12:36:52Z sskracic $
 * @since 4.5
 *
 */

public class ConnectionManager {

    private static final Logger LOG =
        Logger.getLogger(ConnectionManager.class);

    /**
     * Gets a jdbc connection.
     *
     * @deprecated Use {@link Session#getConnection()} instead.
     **/
    public static java.sql.Connection getConnection()
        throws java.sql.SQLException {
        return SessionManager.getSession().getConnection();
    }


    /**
     * Returns a connection to the connection pool. Anytime code calls
     * getConnection(), it needs to call this method when it is done
     * with the connection
     *
     * @param conn the connection to return
     * @throws java.sql.SQLException
     * @deprecated Connections acquired through
     * Session.getConnection() will automatically be returned to the
     * pool at the end of the transaction.
     **/
    public static void returnConnection(Connection conn)
        throws java.sql.SQLException {
        // do nothing
    }

    /**
     * Returns the connection presently in use by this thread.
     * @deprecated Use {@link Session#getConnection()} instead.
     **/
    public static java.sql.Connection getCurrentThreadConnection() {
        return SessionManager.getSession().getConnection();
    }

}
