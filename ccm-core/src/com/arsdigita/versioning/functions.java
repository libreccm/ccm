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

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.Event;
import com.redhat.persistence.ObjectEvent;
import com.arsdigita.developersupport.Debug;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

// new versioning

/**
 * Sundry small static methods.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-03-13
 * @version $Revision: #13 $ $DateTime: 2004/08/16 18:10:38 $
 **/
final class functions implements Constants {
    private final static Logger s_log = Logger.getLogger(functions.class);

    private final static String M_SSN = "m_ssn";
    private final static String PRINTED_NULL = "null";
    private final static String EMPTY_MAP = "<empty map>";
    // The length of the decimal representation of the maximum long value.
    private final static int LONG_WIDTH =
        String.valueOf(Long.MAX_VALUE).length();

    private final static ThreadLocal s_seqID = new ThreadLocal() {
            public Object initialValue() {
                return new MutableInteger();
            }
        };

    private functions() {}

    /**
     * Retrieves a value from a db-backed sequence and wraps the useless checked
     * SQLException.
     **/
    public static BigInteger nextSequenceValue() {
        MutableInteger val = (MutableInteger) s_seqID.get();
        val.increment();
        s_seqID.set(val);
        return val.bigIntegerValue();
    }

    /**
     * <p>Retrieves a value from a db-backed sequence and wraps the useless
     * checked SQLException. The value is meant to be used <em>exclusively</em>
     * for creating Txn data objects.</p>
     **/
    public static BigInteger nextTxnID() {
        try {
            return Sequences.getNextValue("vcx_txns_id_seq").toBigInteger();
        } catch (SQLException ex) {
            throw new UncheckedWrapperException
                ("couldn't get the next key value out of a sequence", ex);
        }
    }

    /**
     * @return the object type of the container of the property for which this
     * property event has been generated
     **/
    public static ObjectType getObjectType(Event ev) {
        return ((DataObject) ev.getObject()).getObjectType();
    }

    /**
     * @retrun the object type of the data object for which this object event
     * has been generated.
     **/
    public static ObjectType getObjectType(ObjectEvent ev) {
        return ((DataObject) ev.getObject()).getObjectType();
    }

    public static String prettyString(DataObject dobj) {
        if ( dobj == null ) return PRINTED_NULL;

        StringBuffer sb = new StringBuffer();
        sb.append(dobj).append(LINE_SEP);

        List props = new ArrayList();
        for (Iterator ii=dobj.getObjectType().getProperties(); ii.hasNext(); ) {
            Property prop = (Property) ii.next();
            props.add(prop.getName());
        }

        Collections.sort(props);
        for (Iterator ii=props.iterator(); ii.hasNext(); ) {
            String prop = (String) ii.next();
            sb.append(prop).append("=");
            sb.append(dobj.get(prop)).append(LINE_SEP);
        }
        return sb.toString();
    }

    /**
     * Converts the <code>map</code> to a pretty string representation of
     * key-value pairs, sorted by keys.
     *
     **/
    public static String prettyString(Map map) {
        if ( map == null ) return PRINTED_NULL;
        if ( map.size() == 0 ) return EMPTY_MAP;

        List keys = new ArrayList(map.keySet());
        Collections.sort(keys);
        return prettyString(map, keys.iterator());
    }

    public static String prettyStringUnsorted(Map map) {
        if ( map == null ) return PRINTED_NULL;
        if ( map.size() == 0 ) return EMPTY_MAP;

        return prettyString(map, map.keySet().iterator());
    }

    private static String prettyString(Map map, Iterator keys) {
        StringBuffer sb = new StringBuffer();
        while ( keys.hasNext() ) {
            Object key = keys.next();
            sb.append(key).append("=").append(map.get(key));
            sb.append(LINE_SEP);
        }
        return sb.toString();
    }

    public static String prettyString(DomainObject obj) {
        if ( obj == null ) return PRINTED_NULL;

        DataObject dobj = (DataObject)
            Debug.getPrivateField(DomainObject.class, obj, "m_dataObject");
        return prettyString(dobj);
    }

    // This class is not synchronized
    private static class MutableInteger {
        private final static long BATCH_SIZE = 100;
        private long m_int;
        private long m_max;

        public MutableInteger() {
            fetchFromSequence();
        }

        private void fetchFromSequence() {
            try {
                long next = Sequences.getNextValue("vcx_id_seq").longValue();
                if (Long.MAX_VALUE / BATCH_SIZE < next ) {
                    throw new VersioningException
                        ("ran out of suitable sequence values");
                }
                m_int = next * BATCH_SIZE;
                if ( Long.MAX_VALUE - BATCH_SIZE < m_int ) {
                    throw new VersioningException("ran out");
                }
                m_max = m_int + BATCH_SIZE;
            } catch (SQLException ex) {
                throw new UncheckedWrapperException
                    ("couldn't get the next key value out of a sequence", ex);
            }
        }

        public void increment() {
            m_int++;
            if ( m_int == m_max ) {
                fetchFromSequence();
            }
        }

        public BigInteger bigIntegerValue() {
            return BigInteger.valueOf(m_int);
        }

        public String toString() {
            return String.valueOf(m_int);
        }
    }
}
