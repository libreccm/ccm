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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * A wrapper for the "VT4" object type.
 *
 * @since 2003-05-16
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/
class VT4 {

    private final static String BIG_DECIMAL = "bigDecimal";
    private final static String BIG_INTEGER = "bigInteger";
    private final static String BLOB        = "blob";
    private final static String BOOLEAN     = "boolean";
    private final static String BYTE        = "byte";
    private final static String CHARACTER   = "character";
    private final static String CLOB        = "clob";
    private final static String DATE        = "date";
    private final static String DOUBLE      = "double";
    private final static String FLOAT       = "float";
    private final static String INTEGER     = "integer";
    private final static String LONG        = "long";
    private final static String SHORT       = "short";
    private final static String STRING      = "string";

    private final DataObject m_dobj;

    public VT4() {
        m_dobj = SessionManager.getSession().create(Const.VT4);
        m_dobj.set("id", functions.nextSequenceValue());
    }

    public VT4(DataObject dobj) {
        m_dobj = dobj;
    }

    public DataObject getDataObject() {
        return m_dobj;
    }

    public void setBigDecimal(BigDecimal value) {
        m_dobj.set(BIG_DECIMAL, value);
    }

    public BigDecimal getBigDecimal() {
        return (BigDecimal) m_dobj.get(BIG_DECIMAL);
    }

    public void setBigInteger(BigInteger value) {
        m_dobj.set(BIG_INTEGER, value);
    }

    public BigInteger getBigInteger() {
        return (BigInteger) m_dobj.get(BIG_INTEGER);
    }

    public void setBlob(byte[] value) {
        m_dobj.set(BLOB, value);
    }

    public byte[] getBlob() {
        return (byte[]) m_dobj.get(BLOB);
    }

    public void setBoolean(Boolean value) {
        m_dobj.set(BOOLEAN, value);
    }

    public Boolean getBoolean() {
        return (Boolean) m_dobj.get(BOOLEAN);
    }

    public void setByte(Byte value) {
        m_dobj.set(BYTE, value);
    }

    public Byte getByte() {
        return (Byte) m_dobj.get(BYTE);
    }

    public void setCharacter(Character value) {
        m_dobj.set(CHARACTER, value);
    }

    public Character getCharacter() {
        return (Character) m_dobj.get(CHARACTER);
    }

    public void setClob(String value) {
        m_dobj.set(CLOB, value);
    }

    public String getClob() {
        return (String) m_dobj.get(CLOB);
    }

    public void setDate(Date value) {
        m_dobj.set(DATE, value);
    }

    public Date getDate() {
        return (Date) m_dobj.get(DATE);
    }

    public void setDouble(Double value) {
        m_dobj.set(DOUBLE, value);
    }

    public Double getDouble() {
        return (Double) m_dobj.get(DOUBLE);
    }

    public void setFloat(Float value) {
        m_dobj.set(FLOAT, value);
    }

    public Float getFloat() {
        return (Float) m_dobj.get(FLOAT);
    }

    public void setInteger(Integer value) {
        m_dobj.set(INTEGER, value);
    }

    public Integer getInteger() {
        return (Integer) m_dobj.get(INTEGER);
    }

    public void setLong(Long value) {
        m_dobj.set(LONG, value);
    }

    public Long getLong() {
        return (Long) m_dobj.get(LONG);
    }

    public void setShort(Short value) {
        m_dobj.set(SHORT, value);
    }

    public Short getShort() {
        return (Short) m_dobj.get(SHORT);
    }

    public void setString(String value) {
        m_dobj.set(STRING, value);
    }

    public String getString() {
        return (String) m_dobj.get(STRING);
    }
}
