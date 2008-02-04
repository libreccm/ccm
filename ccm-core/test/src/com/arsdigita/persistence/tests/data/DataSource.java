/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Value;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * The DataSource class will provide an arbitrary number of pseudorandom test
 * values compatible with any given data object property. The values are
 * deterministically dependent on the key with which the DataSource is
 * constructed and the name, type, and containing object type of the given
 * property. Whenever a value is requested for a given property, the
 * DataSource key, fully qualified name of the object type containing the
 * property, the property name, and the fully qualified name of the property
 * type are combined and hashed using an MD5 hashing algorithm. The resulting
 * message digest is then used to produce a pseudorandom but deterministic
 * value of the correct type for the specified property.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 **/

public class DataSource {

    public final static String versionId = "$Id: DataSource.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger LOG = Logger.getLogger(DataSource.class);

    private String m_key;

    public DataSource(String key) {
        m_key = key;
    }

    public String getKey() {
        return m_key;
    }

    private BigInteger makeBigInteger(byte[] hash, int start, int scale) {
        BigInteger result = BigInteger.ZERO;

        // convert each bit of the hash into a digit of the result starting
        // with start
        for (int i = start; i < start + scale/8; i++) {
            byte digit = hash[i % hash.length];
            result = result.shiftLeft(8);
            result = result.add(BigInteger.valueOf(digit));
        }

        return result;
    }

    private String makeNumber(byte[] hash, int scale, int precision) {
        StringBuffer result = new StringBuffer();
        result.append(makeBigInteger(hash, 0, scale));

        if (precision > 0) {
            result.append('.');
            result.append(makeBigInteger(hash, scale/8, precision).abs());
        }

        return result.toString();
    }

    private String makeString(byte[] hash, int maxLength) {
        int length = 1 + (int) ((maxLength - 1) *
                                ((float) Math.abs(hash[0]) / 127.0));
        StringBuffer result = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            byte b = hash[(i + 1) % hash.length];
            b = (byte) (b & 0x7F);

            if (b < 0x20) {
                b = (byte) (0x20 + b);
            }

            result.append((char) b);
        }

        return result.toString();
    }

    private String makeBoolean(byte[] hash) {
        return hash[0] > 0 ? "true" : "false";
    }

    public OID getOID(ObjectTree tree) {
        ObjectType type = tree.getObjectType();
        OID oid = new OID(type);
        for (Iterator it = type.getKeyProperties(); it.hasNext(); ) {
            Property key = (Property) it.next();
            oid.set(key.getName(), getTestData(tree, key.getName()));
        }

        return oid;
    }

    private static final Column getColumn(Property p) {
	Root root = SessionManager.getSession().getMetadataRoot().getRoot();
	ObjectMap om = root.getObjectMap
	    (root.getObjectType(p.getContainer().getQualifiedName()));
	Mapping m = om.getMapping(Path.get(p.getName()));
	if (m instanceof Value) {
	    return ((Value) m).getColumn();
	} else {
	    return null;
	}
    }

    public Object getTestData(ObjectTree tree, String path) {
        Property prop = (Property) tree.getProperty(path);

	Column col = getColumn(prop);

        if (prop.isAttribute()) {
            Assert.assertNotNull(col);
        }

        String toHash = m_key + ":" +
            tree.getRoot().getObjectType().getQualifiedName() + ":" +
            tree.getAbsolutePath(path) + ":" +
            prop.getType().getQualifiedName();

        byte[] hash;

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            hash = digest.digest(toHash.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new UncheckedWrapperException(e);
        }

        DataType type = prop.getType();
        if (type instanceof ObjectType) {
            return getOID(tree.getSubtree(path));
        } else if (type.equals(MetadataRoot.BIGINTEGER)) {
            return new BigInteger(makeNumber(hash, 32, 0));
        } else if (type.equals(MetadataRoot.BIGDECIMAL)) {
            return new BigDecimal(makeNumber(hash, 32, 0));
        } else if (type.equals(MetadataRoot.BOOLEAN)) {
            return Boolean.valueOf(makeBoolean(hash));
        } else if (type.equals(MetadataRoot.BYTE)) {
            return Byte.valueOf(makeNumber(hash, 8, 0));
        } else if (type.equals(MetadataRoot.CHARACTER)) {
            return new Character(makeString(hash, 1).charAt(0));
        } else if (type.equals(MetadataRoot.DATE)) {
            return new Date(1000*Long.parseLong(makeNumber(hash, 32, 0)));
        } else if (type.equals(MetadataRoot.DOUBLE)) {
            return Double.valueOf(makeNumber(hash, 64, 0));
        } else if (type.equals(MetadataRoot.FLOAT)) {
            return Float.valueOf(makeNumber(hash, 32, 0));
        } else if (type.equals(MetadataRoot.INTEGER)) {
            return Integer.valueOf(makeNumber(hash, 32, 0));
        } else if (type.equals(MetadataRoot.LONG)) {
            return Long.valueOf(makeNumber(hash, 64, 0));
        } else if (type.equals(MetadataRoot.SHORT)) {
            return Short.valueOf(makeNumber(hash, 16, 0));
        } else if (type.equals(MetadataRoot.STRING)) {
            int size = col.getSize();
            if (size < 0) {
                LOG.warn("Size for property " + prop.getName() +
                         " unspecified. Defaulting to 10");
                size = 10;
            }

            return makeString(hash, size);
        } else if (type.equals(MetadataRoot.BLOB)) {
            return hash;
        } else if (type.equals(MetadataRoot.CLOB)) {
            return makeString(hash, 1024);
        } else {
            throw new IllegalArgumentException("unknown type");
        }
    }

}
