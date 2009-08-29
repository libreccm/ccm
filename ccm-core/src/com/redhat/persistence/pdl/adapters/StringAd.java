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
package com.redhat.persistence.pdl.adapters;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.arsdigita.db.DbHelper;


/**
 * StringAd: StringAdapter class
 *
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: StringAd.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class StringAd extends SimpleAdapter {

    public StringAd() {
            super("global.String", Types.VARCHAR);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
        throws SQLException {
        ps.setString(index, (String) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        if (md.getColumnType(rs.findColumn(column)) == Types.CLOB &&
            DbHelper.getDatabase(rs) != DbHelper.DB_POSTGRES) {
            Clob clob = rs.getClob(column);
            if (clob == null) {
                return null;
            } else {
                return clob.getSubString(1L, (int)clob.length());
            }
        } else {
            return rs.getString(column);
        }
    }

    public boolean isMutation(Object value, int jdbcType) {
        return (value != null && jdbcType == Types.CLOB);
    }

    public void mutate(ResultSet rs, String column, Object value, int jdbcType)
        throws SQLException {
        if (DbHelper.getDatabase(rs) == DbHelper.DB_POSTGRES) {
            // do nothing
            return;
        }

        Clob clob = rs.getClob(column);
        try {
            Writer out = (Writer)clob.getClass().getMethod("getCharacterOutputStream", new Class[0]).invoke(clob);
            out.write(((String) value).toCharArray());
            out.flush();
            out.close();
        } catch (IOException e) {
            // This used to be a persistence exception, but using
            // persistence exception here breaks ant verify-pdl
            // because the classpath isn't set up to include
            // com.arsdigita.util.*
            throw new Error("Unable to write LOB: " + e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to write CLOB", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Unable to write CLOB", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to write CLOB", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to write CLOB", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to write CLOB", e);
        }
    }

}
