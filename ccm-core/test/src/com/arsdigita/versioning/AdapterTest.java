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
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

public class AdapterTest extends BaseTestCase {
    private static final Logger s_log = Logger.getLogger(AdapterTest.class);

    public AdapterTest(String name) {
        super(name);
    }

    public void testDate() {
        Date expected = new Date();
        Date actual = (Date) Adapter.deserialize(Adapter.serialize(expected),
                                                 Types.DATE);
        assertEquals("serialized/deserialized date", expected, actual);
    }

    public void testTimestamp() {
        Timestamp expected = new Timestamp(System.currentTimeMillis());
        expected.setNanos(1973);
        Timestamp actual = (Timestamp)
            Adapter.deserialize(Adapter.serialize(expected),
                                Types.TIMESTAMP);
        assertEquals("serialized/deserialized timestamp", expected, actual);
    }

    public void testBigDecimalOID() {
        final String type = "versioning.serialization.DecimalKey";

        OID expected = new OID(type, new BigDecimal(12345));
        OID actual = (OID) Adapter.deserialize(Adapter.serialize(expected),
                                               Types.OID);
        assertEquals("serialized/deserialized oid", expected, actual);
    }

    public void testBigIntegerOID() {
        final String type = "versioning.serialization.IntegerKey";

        OID expected = new OID(type, new BigInteger("12345"));
        OID actual = (OID) Adapter.deserialize(Adapter.serialize(expected),
                                               Types.OID);
        assertEquals("serialized/deserialized oid", expected, actual);
    }


    public void testMultiKeyOID() {
        final String type = "versioning.serialization.CompoundKey";

        OID expected = new OID(type);
        expected.set("id2", new BigDecimal(54321));
        expected.set("sid", "foobar");
        expected.set("id1", new BigInteger("12345"));

        s_log.debug("expected=" + Adapter.serialize(expected));

        OID actual = (OID) Adapter.deserialize(Adapter.serialize(expected),
                                               Types.OID);
        assertEquals("serialized/deserialized oid", expected, actual);
        
    }
}
