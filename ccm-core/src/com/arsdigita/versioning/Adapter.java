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
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.SimpleType;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

// new versioning

/**
 * A utility class for serializing/deserializing PDL types to and from Strings.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-03-18
 * @version $Revision: #10 $ $DateTime: 2004/08/16 18:10:38 $
 */
final class Adapter {
    private static final Logger s_log = Logger.getLogger(Adapter.class);

    private static final String s_dateDelim   = ":";
    private static final String s_oidDelim    = ";";
    private static final String s_tstampDelim = ".";

    private static final Map s_converters = new HashMap();
    static {
        initializeAdapters();
    }
    private Adapter() {}


    /**
     * @pre Session.getObjectType(obj) != null
     **/
    public static String serialize(Object obj) {
        if ( obj == null ) return null;

        return getConverter(Types.getObjectType(obj)).serialize(obj);
    }

    public static Object deserialize(String value, Types type) {
        if ( value == null ) return null;

        return getConverter(type).deserialize(value);
    }

    private static Converter getConverter(Types type) {
        Assert.exists(type, Types.class);
        return (Converter) s_converters.get(type);
    }

    // Note that there is no adapter for the BLOB type. It is a special case
    // that does not utilize the adapter machinery.
    private static void initializeAdapters() {
        s_converters.put(Types.BIG_DECIMAL, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new BigDecimal(str);
                }
            });
        s_converters.put(Types.BIG_INTEGER, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new BigInteger(str);
                }
            });
        s_converters.put(Types.BOOLEAN, new SimpleConverter() {
                public Object deserialize(String str) {
                    return Boolean.valueOf(str);
                }
            });
        s_converters.put(Types.BYTE, new SimpleConverter() {
                public Object deserialize(String str) {
                    return Byte.valueOf(str);
                }
            });
        s_converters.put(Types.CHARACTER, new SimpleConverter() {
                public Object deserialize(String str) {
                    Assert.isTrue(str.length() == 1, "str.length() == 1");
                    return new Character(str.charAt(0));
                }
            });
        s_converters.put(Types.DATE, new Converter() {
                public String serialize(Object obj) {
                    Date date = (Date) obj;
                    StringBuffer result = new StringBuffer(100);
                    result.append(date.getTime());
                    result.append(s_dateDelim);
                    result.append(date.toString());
                    return result.toString();
                }

                public Object deserialize(String str) {
                    int idx = str.indexOf(s_dateDelim);
                    Assert.isTrue(idx>=0, "idx>0");
                    return new Date(Long.parseLong(str.substring(0, idx)));
                }
            });
        s_converters.put(Types.DOUBLE, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new Double(str);
                }
            });
        s_converters.put(Types.FLOAT, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new Float(str);
                }
            });
        s_converters.put(Types.INTEGER, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new Integer(str);
                }
            });
        s_converters.put(Types.LONG, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new Long(str);
                }
            });
        s_converters.put(Types.OID, new Converter() {
                public String serialize(Object obj) {
                    return serializeOID((OID) obj);
                }

                public Object deserialize(String str) {
                    return deserializeOID(str);
                }
            });
        s_converters.put(Types.SHORT, new SimpleConverter() {
                public Object deserialize(String str) {
                    return new Short(str);
                }
            });
        s_converters.put(Types.STRING, new SimpleConverter() {
                public Object deserialize(String str) {
                    return str;
                }
            });

        s_converters.put(Types.TIMESTAMP, new Converter() {
                public String serialize(Object obj) {
                    Timestamp tstamp = (Timestamp) obj;
                    StringBuffer result = new StringBuffer(100);
                    result.append(tstamp.getTime());
                    result.append(s_tstampDelim);
                    result.append(tstamp.getNanos());
                    result.append(s_dateDelim);
                    result.append(tstamp.toString());
                    return result.toString();
                }

                public Object deserialize(String str) {
                    int mIdx = str.indexOf(s_tstampDelim);
                    Assert.isTrue(mIdx>=0, "mIdx>0");
                    long millis = Long.parseLong(str.substring(0, mIdx));
                    int nIdx = str.indexOf(s_dateDelim, mIdx);
                    Assert.isTrue(nIdx>=0, "nIdx>0");
                    int nanos = Integer.parseInt(str.substring(mIdx+1, nIdx));
                    Timestamp result = new Timestamp(millis);
                    result.setNanos(nanos);
                    return result;
                }
            });
    }

    /**
     * @pre prop != null
     **/
    private static Types getType(Property prop) {
        Assert.exists(prop, Property.class);
        DataType dataType = prop.getType();
        if ( dataType.isSimple() ) {
            return Types.getType((SimpleType) dataType);
        } else {
            throw new Error("not implemented for compound types yet");
        }
    }

    private static String serializeOID(final OID oid) {
        List keyValuePairs = new ArrayList();
        Iterator props=oid.getObjectType().getKeyProperties();
        while ( props.hasNext() ) {
            Property prop = (Property) props.next();
            String pName = prop.getName();
            Object pValue = oid.get(pName);
            Assert.exists(pValue, Object.class);
            Types type = getType(prop);
            final char sep = ':';
            StringBuffer packed = new StringBuffer();
            packed.append(pName).append(sep).append(type.getID()).append(sep);
            packed.append(Adapter.serialize(pValue));
            keyValuePairs.add(packed.toString());
        }

        Assert.isTrue(keyValuePairs.size()>0,
                     "oid has at least one property");
        if ( keyValuePairs.size() > 1 ) Collections.sort(keyValuePairs);

        final StringBuffer result = new StringBuffer(64);
        result.append(oid.getObjectType().getQualifiedName());

        for (Iterator ii=keyValuePairs.iterator(); ii.hasNext(); ) {
            String value = (String) ii.next();
            result.append(s_oidDelim).append(value);
        }
        return result.toString();
    }

    private static OID deserializeOID(String str) {
        final StringTokenizer st = new StringTokenizer(str, s_oidDelim);
        Assert.isTrue(st.hasMoreTokens(), str);
        OID oid = new OID(st.nextToken());

        while (st.hasMoreTokens() ) {
            final String token = st.nextToken();
            final StringTokenizer tuple = new StringTokenizer(token, ":");

            Assert.isTrue(tuple.hasMoreTokens(), token);
            String pName = tuple.nextToken();
            Assert.isTrue(tuple.hasMoreTokens(), token);
            String pType = tuple.nextToken();
            Assert.isTrue(tuple.hasMoreTokens(), token);
            String pValue = token.substring(pName.length() + pType.length() + 2);
            oid.set(pName, deserialize(pValue,
                                       Types.getType(new BigInteger(pType))));
        }
        return oid;
    }
}
