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
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.SimpleType;
import com.arsdigita.util.Assert;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// new versioning

/**
 * Type-safe enum listing all the types whose serialization/deserialization is
 * supported.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-03-18
 * @version $Revision: #12 $ $DateTime: 2004/08/16 18:10:38 $
 */
final class Types {
    private final static String DATA_TYPE = Constants.PDL_MODEL + ".JavaClass";

    private final static Map s_typesByName = new HashMap();
    private final static Map s_typesByID = new HashMap();

    // the following Types constants need to be kept in sync with
    // sql/default/versioning/insert-vcx_java_classes.sql

    public final static Types VOID        = newType(0, "java.lang.Void");
    public final static Types BIG_DECIMAL = newType(1, "java.math.BigDecimal");
    public final static Types BIG_INTEGER = newType(2, "java.math.BigInteger");
    public final static Types BLOB        = newType(3, "byte[]");
    public final static Types BOOLEAN     = newType(4, "java.lang.Boolean");
    public final static Types BYTE        = newType(5, "java.lang.Byte");
    public final static Types CHARACTER   = newType(6, "java.lang.Character");
    public final static Types DATE        = newType(7, "java.util.Date");
    public final static Types DOUBLE      = newType(8, "java.lang.Double");
    public final static Types FLOAT       = newType(9, "java.lang.Float");
    public final static Types INTEGER     = newType(10, "java.lang.Integer");
    public final static Types LONG        = newType(11, "java.lang.Long");
    public final static Types OID         =
        newType(12, "com.arsdigita.persistence.OID");
    public final static Types SHORT       = newType(13, "java.lang.Short");
    public final static Types STRING      = newType(14, "java.lang.String");
    public final static Types TIMESTAMP   = newType(15, "java.sql.Timestamp");

    // XXX Quickfix:
    // Method initialize() should only be run once.
    // Originally it had been controlled by the package this.initializer,
    // which uses the old initializer system and has been commented out in
    // enterprise.ini for a long time.
    // As a quick replacement we introduce the variable here.
    // Fixme: It might be necessary to controle it by the core initializer, so
    // CCM can be restarted in a servlet container using management extension
    // (and without restarting the container itself).
    // (2010-01-14, as of version 6.6.0)
    private static boolean s_hasRun = false;

    private BigInteger m_id;
    private String m_name;
    private DataObject m_dobj;

    private Types(BigInteger id, String name) {
        m_name = name;
        m_id = id;
    }

    public BigInteger getID() {
        return m_id;
    }

    synchronized public DataObject getDataObject() {
        if ( m_dobj == null ) {
            m_dobj = SessionManager.getSession().retrieve(new OID(DATA_TYPE, m_id));
            m_dobj.disconnect();
        }
        return m_dobj;
    }

    private static Types newType(int id, String name) {
        if ( s_typesByName.containsKey(name) ) {
            throw new IllegalArgumentException
                ("s_typesByName already contains " + name);
        }
        BigInteger biID = new BigInteger(String.valueOf(id));
        if ( s_typesByID.containsKey(biID) ) {
            throw new IllegalArgumentException
                ("s_typesByID already contains " + id);
        }

        Types result = new Types(biID, name);
        s_typesByName.put(name, result);
        s_typesByID.put(biID, result);
        return result;
    }

    static void initialize() {
        // XXX FixMe
        // refers to the old style initializer of the package versioning which
        // is commented out of the enterprise.ini file (since an unkown time,
        // currently version 1.0.5
        // Must be dealt with internally here!
    //  if (Initializer.hasRun()) {
        if ( s_hasRun ) {
            throw new IllegalStateException("can't be called more than once");
        }
        for (Iterator ii=s_typesByID.values().iterator(); ii.hasNext(); ) {
            Types type = (Types) ii.next();
            type.getDataObject();
        }
        s_hasRun = true;
    }

    /**
     * Basically a shortcut for {@link #getType(Class) getType(obj.getClass())}.
     **/
    public static Types getObjectType(Object obj) {
        if ( obj == null ) {
            return VOID;
        }
        if ( obj instanceof byte[] ) return BLOB;

        return getType(obj.getClass());
    }

    /**
     * Maps the specified Java class to an instance of the <code>Types</code>
     * enum.
     *
     * <p>This should be considered a private method. It is package-scoped to
     * make it unit-testable. </p>
     *
     * @pre klass != null
     * @throws UnknownTypeException note this this is an unchecked exception.
     **/
    static Types getType(final Class klass) throws UnknownTypeException {
        Assert.exists(klass, Class.class);
        Class superClass = klass;
        while (true) {
            Types result = getTypeOrNull(superClass.getName());
            if ( result != null ) return result;

            superClass = superClass.getSuperclass();
            if ( superClass == null ) {
                throw new UnknownTypeException(klass + " is not a known type");
            }
        }
    }

    /**
     * @pre type != null
     **/
    public static Types getType(SimpleType type) {
        return getType(type.getJavaClass());
    }

    private static Types getTypeOrNull(String className) {
        return (Types) s_typesByName.get(className);
    }

    static Types getType(BigInteger id) {
        Types result = (Types) s_typesByID.get(id);
        if ( result == null ) {
            throw new UnknownTypeException(id + " is not a known type.");
        }
        return result;
    }

    public static Types getType(DataObject dobj) {
        return getType((BigInteger) dobj.get("id"));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(m_name).append(":").append(m_id);
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return m_id.intValue();
    }
}
