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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * A generic Sequence implementation appropriate for use with databases
 * that don't support Oracle style sequences.
 *
 * @author Kevin Scaldeferri
 */


public class GenericSequenceImpl extends SequenceImpl {

    public static final String versionId = "$Id: GenericSequenceImpl.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /* This is a pseudo-Singleton implementation.  We create a
     * single instance for each sequence.
     */

    // keeps a reference to each known sequence indexed by
    // sequence name
    private static HashMap s_knownSequences = new HashMap();

    private static final String currentValueSelect =
        "select value from sequences where sequence_name = ?";

    private static final String currentValueUpdate =
        "update sequences set value = ? where sequence_name = ?";
    // each instance has a name

    private String m_sequenceName;

    // private constructor

    private GenericSequenceImpl(String sequenceName) {
        m_sequenceName = sequenceName;
    }


    /**
     * This creation method should be used to obtain instances
     * of GenericSequenceImpl.
     */


    public static GenericSequenceImpl createSequence(String sequenceName) {
        GenericSequenceImpl gs;

        synchronized (s_knownSequences) {
            if (! s_knownSequences.containsKey(sequenceName)) {
                gs = new GenericSequenceImpl(sequenceName);
                s_knownSequences.put(sequenceName, gs);
            } else {
                gs = (GenericSequenceImpl) s_knownSequences.get(sequenceName);
            }
        }

        return gs;
    }

    /**
     * Gets the next value in the sequence
     */

    /*
     * Assumes that we have a table "sequences" in the database that
     * we use to store the currently value of a sequence
     */

    public BigDecimal getNextValue() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        try {
            BigDecimal result = this.getNextValue(conn);
            return result;
        } finally {
            ConnectionManager.returnConnection(conn);
        }
    }

    public BigDecimal getCurrentValue() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        try {
            BigDecimal result = this.getCurrentValue(conn);
            return result;
        } finally {
            ConnectionManager.returnConnection(conn);
        }
    }

    public synchronized BigDecimal getNextValue(java.sql.Connection conn)
        throws SQLException {

        // TODO: should lock the table (in some database agnostic way)
        // need to figure out if "select ... for update" is widely supported

        BigDecimal value;

        PreparedStatement stmt = conn.prepareStatement(currentValueSelect);
        try {
            stmt.setString(1, m_sequenceName);

            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    value = rs.getBigDecimal("value");
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

        // increment
        value = value.add(new BigDecimal(1));

        // update the db

        PreparedStatement stmt2 = conn.prepareStatement(currentValueUpdate);
        try {
            stmt2.setBigDecimal(1, value);
            stmt2.setString(2, m_sequenceName);

            stmt2.executeUpdate();
            // TODO: unlock the table
        } finally {
            stmt2.close();
        }

        return value;
    }

    public synchronized BigDecimal getCurrentValue(java.sql.Connection conn)
        throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(currentValueSelect);
        try {
            stmt.setString(1, m_sequenceName);

            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getBigDecimal("value");
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }
}
