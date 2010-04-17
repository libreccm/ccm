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

import com.arsdigita.persistence.metadata.SimpleType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import java.math.BigDecimal;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * An OID represents a unique object ID. An OID is composed of an
 * object type and 1 or more values.
 *
 * <p> The OID class encapsulates the details of the primary key of an
 *  object.  You use instances of OID for retrieving an object from
 *  the database and you would set the OID to a known value. You can
 *  also get an OID object from a DataObject.
 *
 * <p> Note that when the object type is a subtype of ACS Object, we
 * know that the <code>object_id</code> uniquely identifies the
 * object. The OID class is meant to handle both this special case and
 * the more general case where there does not exist a single, unique
 * integer.
 *
 *
 * <p>
 *
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: OID.java 1591 2007-06-15 12:52:55Z steve_p $
 */
public class OID {

    private ObjectType m_type;
    private Map m_values = new HashMap();

    /** some shorthand names for common content-types  */
    private final static String[][] TYPE_SHORTHAND = {
    	{"Article", "com.arsdigita.cms.contenttypes.Article"},
    	{"MultiPartArticle", "com.arsdigita.cms.contenttypes.MultiPartArticle"},
    	{"PressRelease", "com.arsdigita.cms.contenttypes.PressRelease"},
    	{"NewsItem", "com.arsdigita.cms.contenttypes.NewsItem"},
    	{"Bookmark", "com.arsdigita.cms.contenttypes.Bookmark"},
    	{"ESDService", "com.arsdigita.cms.contenttypes.ESDService"},
    	{"SiteProxy", "com.arsdigita.cms.contenttypes.SiteProxy"},
    	{"RelatedLink", "com.arsdigita.cms.contentassets.RelatedLink"},
    	{"FileAttachment", "com.arsdigita.cms.contentassets.FileAttachment"},
    	{"Category", "com.arsdigita.categorization.Category"},
    };
    
    /** the separator character to be used in the new OID formatting scheme */
    private final static char OID_SEPARATOR_CHAR = '-';

    /** A Format used for formatting/parsing OIDs */
    private static MessageFormat m_format_old = new MessageFormat("[{0}:{1}]");
    private static MessageFormat m_format_new = new MessageFormat("{0}" + OID_SEPARATOR_CHAR + "{1}");

    /** used to log errors  */
    private static final Logger m_log = Logger.getLogger(OID.class);


    /**
     *  Creates an OID for the Object type.  An example of an object
     *  type would be "com.arsdigita.kernel.ACSObject."  Using this
     *  constructor alone does not create a full OID.  Rather, the
     *  code must also call the {@link #set(String propertyName,
     *  Object value)} method to set the value that corresponds with
     *  the OID (this value must be part of the object key for the
     *  passed in object type). For instance, if the code passes in
     *  the ACSObject ObjectType then the code should also call
     *  <code>oid.set("id", valueOfID);</code>.  The list of potential
     *  parameters that can be set can be retrieved by calling
     *  <code>type.getObjectMap().getObjectKey()</code>.
     *  <p>
     *  This constructor is typically used when the ObjectType has
     *  multiple keys (
     *  <code>type.getObjectMap().getObjectKey().getCount() > 1</code>)
     *  but it can be used when there is only a single key however it
     *  would probably be easier to use one of the other convenience
     *  constructors when there is only a single key.
     *
     *  @param type The ObjectType
     *
     *  @pre type != null 
     */
    public OID(ObjectType type) {
        m_type = type;
    }

    /**
     * Creates an OID with a single attribute for the key. To create
     * a multi-valued OID, use a single arg OID constructor, and add
     * individual properties with the set method.  This constructor
     * should be used when the object type being instantiated has a
     * single primary key.  For instance, if the object type is
     * <code>com.arsdigita.kernel.ACSObject</code> then the value
     * should be the <code>object_id</code>.  So, if developers wanted
     * to create the OID for ID zero, they would call
     * <code>new OID(acsObjectType, new BigDecimal(0))</code>.  A
     * <code>BigDecimal</code> is passed in because the "id" attribute
     * for the ACSObject type is declared as <code>BigDecimal</code> in
     * the PDL file.
     *
     * @param type The ObjectType of the ID
     * @param value The value of the ID
     * @exception PersistenceException will be thrown if the given
     *            object type does not have exactly a single key (if
     *            <code>type.getObjectMap().getObjectKey().getCount()
     *            != 1</code>).
     *
     * @pre type != null
     * @pre type.getObjectMap().getObjectKey().getCount() == 1
     */
    public OID(ObjectType type, Object value) {
        this(type);
        Iterator it = type.getKeyProperties();
        if (!it.hasNext()) {
            throw new PersistenceException("Empty object key: " + type);
        }

        Property prop = (Property) it.next();

        if (it.hasNext()) {
            throw new PersistenceException(
                                           "This object type has a compound key."
                                           );
        }

        String attr = prop.getName();
        set(attr, value);
    }

    /**
     *  This looks up the specified ObjectType within the SessionManager
     *  and returns the ObjectType object that is specified by the string.
     *
     *  @param typeName This is the name of the ObjectType to look up.
     *  @pre typeName != null
     */
    private static final ObjectType lookup(String typeName) {
        ObjectType type =
            MetadataRoot.getMetadataRoot().getObjectType(typeName);
        if (type == null) {
            throw new PersistenceException("No such type " + typeName);
        }
        return type;
    }


    /**
     *  Creates an OID for the named ObjectType. The typename of the
     *  ObjectType must be defined in the MetadataRoot.  An example
     *  of how this would be used is whithin the default constructor
     *  of a DomainObject.  For instance, ACSObject may have a default
     *  constructor that looks like
     *  <pre>
     *  <code>
     *  public ACSObject() {
     *     super("com.arsdigita.kernel.ACSObject");
     *  }
     *  </code>
     *  </pre>
     *  and the call to super contains "<code>new OID(typeName)</code>"
     *
     *  @param typeName The name of the ObjectType.
     *
     *  @pre typeName != null
     *  @pre SessionManager.getMetadataRoot().getObjectType(typeName) != null
     */
    public OID(String typeName) {
        this(lookup(typeName));
    }


    /**
     * Creates an OID with a single attribute for the key. To create a
     * multi-valued OID, use a single arg OID constructor, and add
     * individual properties with the set method.  This constructor
     * should be used when the object type being instantiated has a
     * single primary key.  For instance, if the object type is
     * <code>com.arsdigita.kernel.ACSObject</code> then the value
     * should be the <code>object_id</code>.  So, if developers wanted
     * to create the OID for ID zero, they would call <code>new
     * OID("com.arsdigita.kernel.ACSObject", new
     * BigDecimal(0))</code>.  A <code>BigDecimal</code> is passed in
     * because the "id" attribute for the ACSObject type is declared
     * as <code>BigDecimal</code> in the PDL file.  This is analogous
     * to {@link #OID(ObjectType type, Object value)} except that a string
     * is used to lookup the correct object type.
     * <p>
     *  The typename of the ObjectType must be defined in the MetadataRoot.
     *
     * @param type The name of the ObjectType for the ID
     * @param value The value of the ID
     * @exception PersistenceException will be thrown if the given
     *            object type does not have exactly a single key (if
     *            <code>type.getObjectMap().getObjectKey().getCount()
     *            != 1</code>).
     *
     *  @pre SessionManager.getMetadataRoot().getObjectType(typeName) != null
     */
    public OID(String typeName, Object value) {
        this(lookup(typeName), value);
    }


    /**
     * Creates an OID with a single attribute for the key. To create a
     * multi-valued OID, use a single arg OID constructor, and add
     * individual properties with the set method.  This constructor
     * should be used when the object type being instantiated has a
     * single primary key.  For instance, if the object type is
     * <code>com.arsdigita.kernel.ACSObject</code> then the value
     * should be the <code>object_id</code>.  So, if developers wanted
     * to create the OID for ID zero, they would call <code>new
     * OID("com.arsdigita.kernel.ACSObject", 0)</code>.  This is analogous
     * to {@link #OID(ObjectType type, Object value)} except that a string
     * is used to lookup the correct object type and the passed in
     * <code>int</code> is converted to a <code>BigDecimal</code> object.
     * <p>
     *  The typename of the ObjectType must be defined in the MetadataRoot.
     *
     * @param type The name of the ObjectType for the ID
     * @param value The integer value of the ID
     *  @exception PersistenceException will be thrown if the given
     *             object type does not have exactly a single key (if
     *             <code>type.getObjectMap().getObjectKey().getCount()
     *             != 1</code>).
     *
     *  @pre SessionManager.getMetadataRoot().getObjectType(typeName) != null */
    public OID(String typeName, int value) {
        this(typeName, new BigDecimal(value));
    }


    /**
     * Creates an OID with a single attribute for the key. To create a
     * multi-valued OID, use a single arg OID constructor, and add
     * individual properties with the set method.  This constructor
     * should be used when the object type being instantiated has a
     * single primary key.  For instance, if the object type is
     * <code>com.arsdigita.kernel.ACSObject</code> then the value
     * should be the <code>object_id</code>.  So, if developers wanted
     * to create the OID for ID zero, they would call <code>new
     * OID(acsObjectType, 0)</code>.  This is analogous to {@link
     * #OID(ObjectType type, Object value)} except that the passed in
     * <code>int</code> is converted to a <code>BigDecimal</code>
     * object.
     *
     *  @param type The ObjectType of the ID
     *  @param value The value of the property
     *  @exception PersistenceException will be thrown if the given
     *             object type does not have exactly a single key (if
     *             <code>type.getObjectMap().getObjectKey().getCount()
     *             != 1</code>).
     *
     *  @pre type != null
     *  @pre type.getObjectMap().getObjectKey().getCount() == 1
     */
    public OID(ObjectType type, int value) {
        this(type, new BigDecimal(value));
    }

    /**
     *  Adds a property to the OID. Is used as part of the key for the Object ID.
     *
     *  @param propertyName Name of the property
     *  @param value The property
     *
     */
    public void set(String propertyName, Object value) {
        Property prop = m_type.getProperty(propertyName);

        // We do some type-checking here, to ensure that OIDs are being
        // created with legit types of values.
        if (prop == null) {
            throw new PersistenceException
                ("no such property: " + propertyName
                 + " for type " + m_type.getName());
        }

        // null has no type
        // if prop isn't an attribute, not sure what to do with it.
        if (prop.isAttribute() && value != null) {
            // we can be sure this is a simpletype because
            // isAttribute was true.
            SimpleType expectedType = (SimpleType)prop.getType();
            if (!expectedType.getJavaClass()
                .isAssignableFrom(value.getClass())) {
                throw new PersistenceException
                    ("expected " + expectedType.getJavaClass()
                     + "actual type " + value.getClass());
            }
        } else if (value != null) {
            if (value instanceof DataObject) {
                ObjectType ot = (ObjectType) prop.getType();
                DataObject dobj = (DataObject) value;
                ObjectType.verifySubtype(ot, dobj.getObjectType());
            } else {
                throw new PersistenceException
                    ("expected DataObject for property " + propertyName
                     + " but got " + value.getClass());
            }
        }

        if (hasProperty(propertyName)) {
            throw new PersistenceException
                (propertyName + " is already set to " + get(propertyName));
        }

        m_values.put(propertyName, value);
    }

    /**
     *  Obtains a property associated with the OID.
     *
     *  @param propertyName Name of the property
     *
     *  @return The property, or null if there is no property with this name.
     */
    public Object get(String propertyName) {
        return m_values.get(propertyName);
    }

    /**
     *  @param name The name of the property
     *
     *  @return true if there is a property mapped to name, false if not.
     */
    public boolean hasProperty(String name) {
        return m_values.containsKey(name);
    }

    /**
     *  @return A Map of all properties for the OID.
     */
    Map getProperties() {
        return m_values;
    }

    public boolean isInitialized() {
        for (Iterator it = m_type.getKeyProperties(); it.hasNext(); ) {
            if (!m_values.containsKey(((Property) it.next()).getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     *  @return The number of properties
     */
    public int getNumberOfProperties() {
        return getProperties().size();
    }

    /**
     * Indicates if an OID contains no non-null information.
     *
     * @return true if no values have been set or if all
     *         values have been set to null.
     */
    public boolean arePropertiesNull() {
        Iterator i = getProperties().values().iterator();
        while (i.hasNext()) {
            if (i.next() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     *  @return The ObjectType.
     *  @deprecated
     */
    public ObjectType getDataObjectType() {
        return getObjectType();
    }


    /**
     *  @return The ObjectType.
     **/
    public ObjectType getObjectType() {
        return m_type;
    }

    void specialize(ObjectType subtype) {
        ObjectType.verifySubtype(m_type, subtype);
        m_type = subtype;
    }


    /**
     * Serializes the OID.
     */
    public String toString() {
        String fullType = m_type.getQualifiedName();
        Map props = getProperties();
        StringBuffer sb = new StringBuffer();
        for(Iterator it = props.keySet().iterator(); it.hasNext(); ) {
        	Object key = it.next();
        	Object value = props.get(key);
        	sb.append(key.toString() + OID_SEPARATOR_CHAR + value.toString());
        	if (it.hasNext()) {
        		sb.append(OID_SEPARATOR_CHAR);
        	}
        }
        Object[] args = {convertTypeShorthand(fullType), sb.toString()};
        return m_format_new.format(args);
    }


    /** Parse the oid string. If it contains a left-square-bracket,
     * use the old parsing scheme. Otherwise, use the new parser.
     */
    public static OID valueOf(String s) throws IllegalArgumentException {
    	if (s.indexOf("[") >= 0 || s.indexOf("%5B") >= 0) {
    		return valueOfOld(s);
    	} else {
    		return valueOfNew(s);
    	}
    }

    /** new way to parse the OID string */
    public static OID valueOfNew(String s) throws IllegalArgumentException {
        StringTokenizer st = new StringTokenizer(s, "" + OID_SEPARATOR_CHAR);
        if (st.countTokens() < 2) {
            throw new IllegalArgumentException
            ("Invalid OID '" + s + "'. It must have at least the object type and the value");        	
//        } else if (st.countTokens() % 2 != 1) {
//            throw new IllegalArgumentException
//            ("Invalid OID '" + s + "'. It must have type followed by name-value pairs");        	
        }

        String type = convertTypeLonghand(st.nextToken());

        try {
        	OID oid = new OID(type);

	        if (st.hasMoreTokens()) {
	        	String key = st.nextToken();
	            String value = st.nextToken();
	            while (st.hasMoreTokens()) {
	            	value = value + OID_SEPARATOR_CHAR + st.nextToken();            	
	            }
	            
	            // if it can be a BigDecimal, that is what we make it
	            boolean bigDecimal = true;
	            for (int i = 0; i < value.length(); i++) {
	                char c = value.charAt(i);
	                if (!('0' <= c && c <= '9') && !((i == 0) && (c == '-'))) {
	                    bigDecimal = false;
	                    break;
	                }
	            }
	
	            if (bigDecimal) {
	                oid.set(key, new BigDecimal(value));
	            } else {
	                oid.set(key, value);
	            }
	        }
            return oid;
	    } catch (PersistenceException e) {
	    	throw new IllegalArgumentException
	    	("Invalid OID '" + s + "'. The type specified [" + type +
	    	"] is not defined");
	    }	
    }

    // Couldn't get MessageFormat to work, so I cheated
    public static OID valueOfOld(String s) throws IllegalArgumentException {
        StringTokenizer st = new StringTokenizer(s, "[:{}],");
        if (st.countTokens() < 2) {
            st = new StringTokenizer(java.net.URLDecoder.decode(s), "[:{}],");

            if (st.countTokens() < 2) {
                throw new IllegalArgumentException
                    ("Invalid OID '" + s + "'. It must have at least the object " +
                     "type and the value");
            }
        }

        String type = st.nextToken();

        try {
            // we know that the first item is the type of the object
            // represented by this OID
            OID oid = new OID(type);

            // for the rest of them, we are going to "split" on the
            // "=" sign
            int nTokens = 0;
            while (st.hasMoreTokens()) {
                String nextToken = st.nextToken();
                int index = nextToken.indexOf("=");
                String key = (nextToken.substring(0, index));
                String value = nextToken.substring(index + 1);

                // we need the key to loose the single space before it that
                // is created by the HashMap.  We cannot use trim because
                // we don't want to loose trailing spaces
                if (nTokens > 0) {
                    key = key.substring(1);
                }

                // if it can be a BigDecimal, that is what we make it
                boolean bigDecimal = true;
                for (int i = 0; i < value.length(); i++) {
                    char c = value.charAt(i);
                    if (!('0' <= c && c <= '9') &&
                        !((i == 0) && (c == '-'))) {
                        bigDecimal = false;
                        break;
                    }
                }

                if (bigDecimal) {
                    oid.set(key, new BigDecimal(value));
                } else {
                    oid.set(key, value);
                }
                nTokens++;
            }
            return oid;
        } catch (PersistenceException e) {
            throw new IllegalArgumentException
                ("Invalid OID '" + s + "'. The type specified [" + type +
                 "] is not defined");
        }
    }

    /**
     * Indicates if two OIDs have the same base type and contain the
     * same values.  Note that if values are null this isn't an ideal
     * distinction; it's best to check "arePropertiesNull" before
     * relying on this equals method (see DomainObject's equals method
     * for an example).  */
    public boolean equals(Object obj) {
        if (obj instanceof OID) {
            OID oid = (OID)obj;
            // we rely on the toString ecause the HashMap.equals does not
            // give us what we need
            return m_type.getBasetype().equals(oid.m_type.getBasetype()) &&
                m_values.equals(oid.m_values);
        }
        return false;
    }

    /**
     * Simple hashcode method to calculate hashcode based on the information
     * used in the equals method.  Needed because we overrode equals;
     * two equivalent objects must hash to the same value.
     */
    public int hashCode() {
        // here we rely on the values collection's hashcode method
        // to base its hashcode on the hashcodes of the contained values.
        return (m_type.getBasetype().hashCode() + m_values.hashCode());
    }
    
    /** create shorthand names for some common types */
    private static String convertTypeShorthand(String name) {
    	for (int i=0; i<TYPE_SHORTHAND.length; ++i) {
    		if (name.equals(TYPE_SHORTHAND[i][1])) {
    			return TYPE_SHORTHAND[i][0];
    		}
    	}
    	return name;
    }

    /** create longhand name for some common types */
    private static String convertTypeLonghand(String name) {
    	for (int i=0; i<TYPE_SHORTHAND.length; ++i) {
    		if (name.equals(TYPE_SHORTHAND[i][0])) {
    			return TYPE_SHORTHAND[i][1];
    		}
    	}
    	return name;
    }
}
