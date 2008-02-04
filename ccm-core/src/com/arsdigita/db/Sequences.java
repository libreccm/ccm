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

import com.arsdigita.db.oracle.OracleSequenceImpl;
import com.arsdigita.db.postgres.PostgresSequenceImpl;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * the Sequence class provides functionality akin to Oracle sequences,
 * i.e. unique integer values appropriate for use as primary keys
 *
 * the Sequence class does not actually provide an implementation.
 * the database dependent implementation must be implemented elsewhere
 *
 * The thread's current connection will be used for the sequence,
 * unless one does not exist in which case a new connection will be
 * retrieved and closed by the specific implementation class.
 *
 * @author Kevin Scaldeferri
 */

public class Sequences {

    public static final String versionId = "$Id: Sequences.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(Sequences.class);

    private static final String defaultSequenceName = "acs_object_id_seq";

    private static SequenceImpl getSequenceImpl(Connection conn,
                                                String sequenceName)
        throws SQLException {
        String url = conn.getMetaData().getURL();

        switch (DbHelper.getDatabaseFromURL(url)) {
        case DbHelper.DB_ORACLE:
            return OracleSequenceImpl.createSequence(sequenceName);
        case DbHelper.DB_POSTGRES:
            return PostgresSequenceImpl.createSequence(sequenceName);
        default:
            DbHelper.unsupportedDatabaseError("sequences");
            return null;
        }
    }

    public static BigDecimal getCurrentValue() throws SQLException {
        return getCurrentValue(defaultSequenceName);
    }

    public static BigDecimal getNextValue() throws SQLException {
        return getNextValue(defaultSequenceName);
    }

    public static BigDecimal getCurrentValue(String sequenceName)
        throws SQLException {

        Connection conn = SessionManager.getSession().getConnection();
        return getNextValue(sequenceName, conn);
    }

    public static BigDecimal getNextValue(String sequenceName)
        throws SQLException {
        Connection conn = SessionManager.getSession().getConnection();
        return getNextValue(sequenceName, conn);
    }

    public static BigDecimal getCurrentValue(Connection conn)
        throws SQLException {
        return getCurrentValue(defaultSequenceName, conn);
    }

    public static BigDecimal getNextValue(Connection conn)
        throws SQLException {
        return getNextValue(defaultSequenceName,conn);
    }

    public static BigDecimal getCurrentValue(String sequenceName,
                                             Connection conn)
        throws SQLException {
        return getSequenceImpl(conn, sequenceName).getCurrentValue(conn);
    }

    public static BigDecimal getNextValue(String sequenceName, Connection conn)
        throws SQLException {
        return getSequenceImpl(conn, sequenceName).getNextValue(conn);
    }
}
