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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;

/**
 * KeyGenerator
 *
 * Attemps to generate default keys for DataObjects. Used in the 'MetaTest' framework.
 * Needs further doc.
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 */

public class KeyGenerator {

    public static final String versionId = "$Id: KeyGenerator.java 749 2005-09-02 12:11:57Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static Map s_keys = new HashMap();
    private KeyGenerator()
    {
    }
    /**
     *  Generates a key and sets all of the key properties for the DataObject
     *
     */
    public static void setKeyValues(DataObject object) throws Exception {
        ObjectType type = object.getObjectType();
        while(type.getSupertype() != null) {
            type = type.getSupertype();
        }
        Key key = (Key) s_keys.get(type);

        if( null == key )  {
            key = new Key(type);
            s_keys.put(type, key);
        }
        else {
            key.incrementValues();
        }

        key.setObjectKey(object);
    }

    private static class Key {
        Map m_keyValues = new HashMap();
        final ObjectType m_type;

        Key(ObjectType type) throws Exception {

            Iterator iter = type.getKeyProperties();
            while(iter.hasNext()) {
                Property p = (Property) iter.next();
                if( p.getType().isSimple() ) {
                    Object initialValue = s_defaults.get(p.getJavaClass());
                    if( null == initialValue ) {
                        StringBuffer buf = new StringBuffer();
                        buf.append("Error generating key for ObjectType: " + type.getName());
                        buf.append(" Cannot make key for property: " + p.getName());
                        buf.append(" which has class: " + p.getJavaClass());
                        throw new Exception(buf.toString());
                    } else {
                        m_keyValues.put( p.getName(), initialValue );
                    }

                }
            } // while

            m_type = type;

        } // Key()

        void incrementValues() {
            Iterator iter = m_keyValues.keySet().iterator();
            while(iter.hasNext()) {
                Object key = iter.next();
                Object oldValue = m_keyValues.get(key);
                SimpleTypeValue st = (SimpleTypeValue) s_simpleValues.get(oldValue.getClass());
                Object newValue = st.increment(oldValue);
                m_keyValues.put(key, newValue);
            }
        }

        void setObjectKey(DataObject object) {
            Assert.assertTrue( m_type.equals(object.getObjectType()) || object.getObjectType().isSubtypeOf(m_type) );

            Iterator iter = m_keyValues.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();
                object.set(name, value);
            }
        }
    } // key


    static Map s_defaults = new HashMap();
    static {
        s_defaults.put( java.math.BigInteger.class, BigInteger.ONE );
        s_defaults.put( java.math.BigDecimal.class, new BigDecimal("1") );
        s_defaults.put( java.lang.Boolean.class, Boolean.TRUE );
        s_defaults.put( java.lang.Byte.class, new Byte((byte) 1) );
        s_defaults.put( java.lang.Character.class, new Character('A') );
        s_defaults.put( java.util.Date.class, new Date() );
        s_defaults.put( java.lang.Integer.class, new Integer(1) );
        s_defaults.put( java.lang.Long.class, new Long(1) );
        s_defaults.put( java.lang.Short.class, new Short((short) 1 ) );
        s_defaults.put( java.lang.String.class, "A" );
    }
    static Map s_simpleValues = new HashMap();

    static abstract class SimpleTypeValue {
        SimpleTypeValue( Class javaClass )  {
            m_defaultValue = s_defaults.get(javaClass);
            s_simpleValues.put( javaClass, this );
        }

        abstract Object increment(Object value);

        final Object m_defaultValue;
    }

    static {
        new SimpleTypeValue(java.math.BigDecimal.class) {
            Object increment(Object value) {
                BigDecimal bigValue = (BigDecimal) value;
                BigDecimal newValue = bigValue.add((BigDecimal) m_defaultValue);
                return newValue;
            }
        };

        new SimpleTypeValue(java.math.BigInteger.class) {
            Object increment(Object value) {
                BigInteger bigValue = (BigInteger) value;
                BigInteger newValue = bigValue.add((BigInteger) m_defaultValue);
                return newValue;
            }
        };

        new SimpleTypeValue(java.lang.Boolean.class) {
            Object increment(Object value) {
                Boolean boolVal = (Boolean) value;
                if (boolVal.booleanValue()) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        };

        new SimpleTypeValue(java.lang.Byte.class) {
            Object increment(Object value) {
                Byte numericValue = (Byte) value;
                Byte newValue = new Byte((byte)(numericValue.byteValue() + ((Byte) m_defaultValue).byteValue()));
                return newValue;
            }
        };

        new SimpleTypeValue(java.lang.Integer.class) {
            Object increment(Object value) {
                Integer numericValue = (Integer) value;
                Integer newValue = new Integer(numericValue.intValue() + ((Integer) m_defaultValue).intValue());
                return newValue;
            }
        };

        new SimpleTypeValue(java.lang.Long.class) {
            Object increment(Object value) {
                Long numericValue = (Long) value;
                Long newValue = new Long(numericValue.longValue() + ((Long) m_defaultValue).longValue());
                return newValue;
            }
        };

        new SimpleTypeValue(java.lang.Short.class) {
            Object increment(Object value) {
                Short numericValue = (Short) value;
                Short newValue = new Short((short)(numericValue.shortValue() + ((Short) m_defaultValue).shortValue()));
                return newValue;
            }
        };

        new SimpleTypeValue(java.util.Date.class) {
            Object increment(Object value) {
                Date dateValue = (Date) value;
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateValue);
                cal.roll(Calendar.MINUTE, 1);

                return cal.getTime();
            }
        };

        new SimpleTypeValue(java.lang.Character.class) {
            Object increment(Object value) {
                Character charValue = (Character) value;
                return new Character( (char)(charValue.charValue() + 1));
            }
        };
        new SimpleTypeValue(java.lang.String.class) {
            Object increment(Object value) {
                String str = (String) value;
                StringBuffer buf = new StringBuffer(str);
                final char charVal = buf.charAt(buf.length() - 1);
                if( charVal == 'z' ) {
                    buf.append('A');
                }
                else {
                    buf.setCharAt(buf.length() - 1, (char)( charVal + 1));
                }
                return buf.toString();
            }
        };


    }

}
