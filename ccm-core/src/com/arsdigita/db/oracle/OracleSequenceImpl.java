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
package com.arsdigita.db.oracle;

import com.arsdigita.db.SequenceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


/**
 * Implementation of the Sequence class for the Oracle RDBMS.
 *
 * @author Kevin Scaldeferri
 * @version $Id: OracleSequenceImpl.java 738 2005-09-01 12:36:52Z sskracic $
 */

public class OracleSequenceImpl extends SequenceImpl {

    private String m_sequenceName;

    // private constructor

    private OracleSequenceImpl(String sequenceName) {
        m_sequenceName = sequenceName;
    }

    public static OracleSequenceImpl createSequence(String sequenceName) {
        return new OracleSequenceImpl(sequenceName);
    }

    public BigDecimal getCurrentValue(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement
            ("select " + m_sequenceName + ".currval from dual");
        try {
            ResultSet rs = stmt.executeQuery();

            try {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist.");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    public BigDecimal getNextValue(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement
            ("select " + m_sequenceName + ".nextval from dual");
        try {
            ResultSet rs = stmt.executeQuery();

            try {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal(1);
                    return value;
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist.");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }
}
