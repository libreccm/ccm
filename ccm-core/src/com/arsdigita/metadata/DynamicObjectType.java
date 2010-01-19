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
package com.arsdigita.metadata;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.Sequences;
import com.arsdigita.initializer.Startup;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.metadata.SimpleType;
import com.arsdigita.persistence.metadata.Utilities;
import com.arsdigita.util.StringUtils;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ForeignKey;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;
import com.redhat.persistence.metadata.Value;
import com.redhat.persistence.pdl.PDLWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * DynamicObjectType is a class that can be used to dynamically
 * create and modify {@link
 * com.arsdigita.persistence.metadata.ObjectType}.  It can be used to
 * create the subtype, add and remove Attributes and RoleReferences as
 * well as perform many other tasks related to the new object type.
 * When the application is done creating the object type, it should
 * call {@link #save()} to persist the information about the newly created
 * object type.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #24 $ $Date: 2004/08/16 $
 */

public class DynamicObjectType extends DynamicElement {

    public static final String versionId = "$Id: DynamicObjectType.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final int COLLECTION =
	com.arsdigita.persistence.metadata.Property.COLLECTION;
    private static final int NULLABLE =
	com.arsdigita.persistence.metadata.Property.NULLABLE;
    private static final int REQUIRED =
	com.arsdigita.persistence.metadata.Property.REQUIRED;


    // the contained ObjectType
    private ObjectType m_objectType;
    private ObjectMap m_objectMap;

    private static final String objectTypeString =
        "com.arsdigita.persistence.DynamicObjectType";

    private Root m_root =
        SessionManager.getSession().getMetadataRoot().getRoot();

    // This is used for updating any information about the given DataObject.
    private DataObject m_dataObject;

    // this is a convenience member variable
    private Table m_table = null;

    // this shows if the item is new or not which is used to
    // determine if the ddlToAdd should be wrapped with a
    // "create table" or an "alter table"
    private boolean m_isNew = true;

    private Collection m_mappingTables = new ArrayList();
    private Collection m_columns = new ArrayList();
    private boolean m_generateTable = false;

    private Map m_defaultValueMap = new HashMap();

    private static final Logger s_log =  Logger.getLogger(DynamicObjectType.class);

    // Empty array for internal use. Should be part of a generic utility class.
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /*  TODO
        Items for docs:
        1. The name should be short
    */
    /*  TODO
        Items needed that we do not yet have:
        3. We need to force the user to specify a Refernce Key
        4. We need to restrict the possible parent types to types
        with only a single key
        5. When adding a RoleReference to each object type,
        we need to make sure to not create the mapping table twice
    */


    /**
     *  This procedures allows developers to dynamically create
     *  object types that subtype existing object types.
     *  The model.name string must be unique
     *
     *  @param supertype This is the existing ObjectType that should
     *                   be extended to create this ObjectType.
     *                   If the supertype is a DynamicObjectType,
     *                   it must be saved before saving this new
     *                   DynamicObjectType.  A supertype can not be null
     *  @param name This is the name of the new object type.  This
     *              should only be the name and should not contain
     *              and "."  The fully qualified name (which is
     *              required by the other constructor) is the
     *              model of the supertype followed by a "."
     *              followed by the passed in name.  This can
     *              be retrieved by calling
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}.
     *              The name must not be null.
     *
     *  @pre name != null
     *  @pre supertype != null
     */
    public DynamicObjectType
	(String name,
	 com.arsdigita.persistence.metadata.ObjectType supertype) {
        this(name, supertype, supertype.getModel());
    }


    /**
     *  This procedures allows developers to dynamically create object
     *  types that may or may not subtype an object.  The
     *  model.name string must be unique and either the model or the
     *  supertype must not be null.  If the model is null, the model
     *  from the supertype is used.
     *
     *  @param supertype This is the existing ObjectType that should
     *                   be extended to create this ObjectType.
     *                   If the supertype is a DynamicObjectType,
     *                   it must be saved before saving this new
     *                   DynamicObjectType.  A supertype can be null.
     *  @param name This is the name of the new object type.  This
     *              should only be the name and should not contain
     *              and "."  The fully qualified name (which is
     *              required by the other constructor) is the
     *              model of the supertype followed by a "."
     *              followed by the passed in name.  This can
     *              be retrieved by calling
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}.
     *              The name must not be null and must only contain
     *              alpha-numeric characters.
     *  @param model This is the name of the model that will be used
     *               for the object type.  If this is specified, it is used
     *               as the object type.
     *
     *  @pre name != null
     *  @pre supertype != null || model != null
     */
    public DynamicObjectType
	(String name, com.arsdigita.persistence.metadata.ObjectType supertype,
	 com.arsdigita.persistence.metadata.Model model) {
        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        if (!StringUtils.isAlphaNumeric(name)) {
            throw new PersistenceException
                ("The name of the DynamicObjectType must be alphanumeric. " +
                 "You tried to create it using [" + name + "]");

        }

        if (model == null) {
            model = supertype.getModel();
        }

        m_objectType = new ObjectType(Model.getInstance(model.getName()),
				      name, type(supertype));
	m_objectMap = new ObjectMap(m_objectType);
	m_root.addObjectType(m_objectType);
	m_root.addObjectMap(m_objectMap);

        // set up the table name and the reference key
        m_table = new Table(generateTableName(m_objectType));
        m_root.addTable(m_table);
	m_generateTable = true;

	m_objectMap.setTable(m_table);

        // now we try to create a reference key
        String columnName = m_objectType.getName() + "_id";
	ObjectMap sm = m_objectMap.getSuperMap();
	if (sm == null) {
	    Column col =
		new Column(columnName, java.sql.Types.INTEGER, 32);
	    m_table.addColumn(col);
	    UniqueKey key = new UniqueKey(null, col);
	    m_table.setPrimaryKey(key);

            // in this case, there is not parent type so we have to
            // create the primary key
            Role role = new Role
		("id", m_root.getObjectType("global.BigDecimal"), false,
		 false, false);
            m_objectType.addProperty(role);
            m_objectMap.getKeyProperties().add(role);
	    m_objectMap.addMapping(new Value(Path.get(role.getName()),
					     col));
	} else {
	    UniqueKey uk = sm.getTable().getPrimaryKey();
	    ForeignKey fk = fk(m_table, null, uk);
	    UniqueKey key = new UniqueKey(m_table, null, fk.getColumns());
	    m_table.setPrimaryKey(key);
        }
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param typeName The name of the object type to instantiate.
     *                  This needs to be the fully qualified name
     *                  such as "com.arsdigita.cms.metadata.Hotel"
     *                  This is the same name that is returned by
     *                  calling
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}
     *
     *  @exception PersistenceException thrown if the requested
     *             type cannot be found or has not yet been saved
     */
    public DynamicObjectType(String typeName) {
        this(SessionManager.getMetadataRoot().getObjectType(typeName),
             typeName);
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param objectType The object type that should be mutated
     *
     *  @exception PersistenceException is thrown if passed in ObjectType
     *             is not mutable
     */
    public DynamicObjectType
	(com.arsdigita.persistence.metadata.ObjectType objectType) {
        this(objectType, objectType.getQualifiedName());
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param objectType The object type that should be mutated
     *
     *  @exception PersistenceException is thrown if passed in ObjectType
     *             is not mutable
     */
    private DynamicObjectType
	(com.arsdigita.persistence.metadata.ObjectType objectType,
	 String typeName) {
        if (objectType == null) {
            throw new PersistenceException("The Object Type you have " +
                                           "requested (" + typeName +
                                           ") does not exist");
        }

        m_objectType = type(objectType);
	m_objectMap = m_root.getObjectMap(m_objectType);

        // get the DataObject from the DataBase to make sure that it is
        // a modifiable type.
        DataCollection collection = SessionManager.getSession()
            .retrieve(objectTypeString);
        collection.addEqualsFilter("dynamicType", typeName);

        try {
            if (!collection.next()) {
                throw new PersistenceException
		    ("The Object Type you have requested (" + typeName +
		     ") cannot be used as a Dynamic Object because it " +
		     "has been defined as read-only");
            }

            m_dataObject = collection.getDataObject();
        } finally {
            collection.close();
        }

	m_table = m_objectMap.getTable();
        m_isNew = false;
    }

    /**
     * Returns <code>true</code> if this object type has the specified
     * property.
     **/
    public boolean hasProperty(String name) {
        return m_objectType.hasProperty(name);
    }

    /**
     *  This adds an Attribute of multiplicity 0..1 which is the
     *  equivalent to adding a column to a table without a "not null"
     *  constraint
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a
     *              Clob is used.  Otherwise, a varchar is used.
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    public com.arsdigita.persistence.metadata.Property addOptionalAttribute
	(String name, SimpleType propertyType) {
        return addOptionalAttribute(name, propertyType, -1);
    }


    /**
     *  This adds an Attribute of multiplicity 0..1 which is the
     *  equivalent to adding a column to a table without a "not null"
     *  constraint
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a
     *              Clob is used.  Otherwise, a varchar is used.
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    public com.arsdigita.persistence.metadata.Property addOptionalAttribute
	(String name, SimpleType propertyType, int size) {
        return addAttribute(name, propertyType, NULLABLE, size, null);
    }


    /**
     *  This adds an Attribute of multiplicity 1..1 which is the
     *  equivalent to adding a column to a table with a "not null"
     *  constraint
     *
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param defaultValue This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type or the default
     *             value is null
     */
    public com.arsdigita.persistence.metadata.Property addRequiredAttribute
	(String name, SimpleType propertyType, Object defaultValue) {
        return addRequiredAttribute(name, propertyType, -1, defaultValue);
    }


    /**
     *  This adds an Attribute of multiplicity 1..1 which is the
     *  equivalent to adding a column to a table with a "not null"
     *  constraint
     *
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a
     *              Clob is used.  Otherwise, a varchar is used.
     *  @param default This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type or the provided
     *             default is null.
     */
    public com.arsdigita.persistence.metadata.Property addRequiredAttribute
	(String name, SimpleType propertyType, int size, Object defaultValue) {
        if (defaultValue == null) {
            throw new PersistenceException
                ("In order to create a required attribute, the default " +
		 "value must not be null");
        }

        return addAttribute(name, propertyType, REQUIRED, size, defaultValue);
    }


    /**
     *  This actually adds the attribute to this object type by
     *  creating the actual Attribute object for use by the supertype
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute.  It must be unique
     *              out of the set of current attributes and rolereferences
     *              within this object type
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param defaultValue This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    private com.arsdigita.persistence.metadata.Property addAttribute
	(String name, SimpleType propertyType, int multiplicity, int size,
	 Object defaultValue) {
        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        if (!StringUtils.isAlphaNumeric(name)) {
            throw new PersistenceException
                ("The name of the DynamicObjectType must be alphanumeric. " +
                 "You tried to create it using [" + name + "]");

        }

        if (hasProperty(name)) {
            throw new PersistenceException
		("The property [" + name + "] already " +
		 "exists in this object type.");
        }

        Role role =
	    new Role(name,
		     m_root.getObjectType(propertyType.getQualifiedName()),
		     false, multiplicity == COLLECTION,
		     multiplicity == NULLABLE);
        m_objectType.addProperty(role);

        String columnName = generateColumnName(m_table, name);

        if (size <= 0) {
            size = -1;
        }

        Adapter ad = m_root.getAdapter(propertyType.getJavaClass());
        int jdbcType = ad.defaultJDBCType();

        if (propertyType.equals(m_root.getObjectType("global.String")) &&
	    size > 4000) {
            jdbcType = Types.CLOB;
            size = -1;
        }

	Column col = new Column(columnName, jdbcType, size);
        col.setNullable(true);
	m_table.addColumn(col);
	m_columns.add(col);
	m_objectMap.addMapping(new Value(Path.get(name), col));

        if (defaultValue != null) {
            m_defaultValueMap.put(name, defaultValue);
        }

        return property(role);
    }

    private java.sql.Connection m_conn = null;

    private java.sql.Statement createStatement() throws PersistenceException {
        try {
            m_conn = ConnectionManager.getConnection();
            return m_conn.createStatement();
        } catch(SQLException e) {
            throw PersistenceException.newInstance
		("Unable to create statement: " + e.getMessage(), e);
        }
    }


    /**
     *  This removes an attribute from object type definition.
     *  This can only remove attributes from the dynamic definition
     *  and not from the parent defintion.  That is, the attribute
     *  that should be removed should be found in <code>getAttributes()</code>
     *  <p>
     *  This does not remove any information from the database.
     *  Rather, it simply dereferences the column in the object type.
     *  Therefore, if an attribute is removed, the data can still be
     *  recovered
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name the name of the attribute to remove
     *  @exception ModelException if the name passed
     *             in is not an Attribute that can be removed
     */
    public void removeAttribute(String name) {
//	throw new UnsupportedOperationException();
        // XXX: set a flag here to say that save() needs to regenerate all
	// subtypes as well
        Property property = m_objectType.getDeclaredProperty(name);
        if (property == null) {
            // we are going to throw an error so let's figure out which one
            if (m_objectType.getProperty(name) != null) {
                throw new PersistenceException
                    ("The property [" + name + "] is a property of the " +
		     "super type and not of this type.  Please delete it " +
		     "from the super type.");
            } else {
                throw new PersistenceException
                    ("The property [" + name + "] you have asked to remove " +
		     "is not a property of this DynamicObjectType");
            }
        } else {
            m_objectType.removeProperty(name);
	    }
    }

    /**
     * Adds an optional one-way association (a role reference) to this Dynamic
     * ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @return the Property that was added to the ObjectType
     */
    public com.arsdigita.persistence.metadata.Property addOptionalAssociation
	(String name, com.arsdigita.persistence.metadata.ObjectType type) {
        return addAssociation(name, type, NULLABLE, null);
    }

    /**
     * Adds an required one-way association (a role reference) to this Dynamic
     * ObjectType.  A default value must be specified.  The referenced type
     * <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @param defaultValue the default value of this Property
     * @return the Property that was added to the ObjectType
     */
    public com.arsdigita.persistence.metadata.Property addRequiredAssociation
	(String name, com.arsdigita.persistence.metadata.ObjectType type,
	 Object defaultValue) {
        return addAssociation(name, type, REQUIRED, defaultValue);
    }

    /**
     * Adds a multiplicitous one-way association (a role reference) to this
     * Dynamic ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @return the Property that was added to the ObjectType
     */
    public com.arsdigita.persistence.metadata.Property addCollectionAssociation
	(String name, com.arsdigita.persistence.metadata.ObjectType type) {
        return addAssociation(name, type, COLLECTION, null);
    }

    private void addColumns(Column[] cols) {
	for (int i = 0; i < cols.length; i++) {
	    m_columns.add(cols[i]);
	}
    }

    /**
     * Adds a one-way association (a role reference) to this Dynamic
     * ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @param mult the multiplicity of the type to add
     * @param defaultValue the default value of the new Property
     * @return the Property that was added to the ObjectType
     */
    public com.arsdigita.persistence.metadata.Property addAssociation
	(String name, com.arsdigita.persistence.metadata.ObjectType objType,
	 int mult, Object defaultValue) {
	ObjectType type = type(objType);

        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                throw new PersistenceException
                    ("The name of the association must be alphanumeric. " +
                     "You tried to create it using [" + name + "]");
            }
        }

        if (hasProperty(name)) {
            throw new PersistenceException
		("The property [" + name + "] already " +
		 "exists in this object type.");
        }

        Role role = new Role(name, type, false, mult == COLLECTION,
			     mult == NULLABLE);
        m_objectType.addProperty(role);

        if (!role.isCollection()) {
            // add a foreign key column here

            // we need this both for the column type, and the FK constraint
	    UniqueKey key =
		m_root.getObjectMap(type).getTable().getPrimaryKey();
	    ForeignKey fk = fk(m_table, name, key);
	    addColumns(fk.getColumns());
            m_objectMap.addMapping(new JoinTo(Path.get(name), fk));
        } else {
            // we need to create a mapping table here
            Table table = new Table(generateTableName(role));
	    m_root.addTable(table);

            UniqueKey from = m_objectMap.getTable().getPrimaryKey();
            UniqueKey to =
		m_root.getObjectMap(type).getTable().getPrimaryKey();

	    JoinThrough jt = new JoinThrough
		(Path.get(name), fk(table, "from", from), fk(table, "to", to));
	    addColumns(jt.getFrom().getColumns());
	    addColumns(jt.getTo().getColumns());
	    m_objectMap.addMapping(jt);
            m_mappingTables.add(table);
        }

        if (defaultValue != null) {
            m_defaultValueMap.put(name, defaultValue);
        }

        return property(role);
    }


    /**
     *  This persists the changes made so that the information is stored
     *  and will not be lost on server restart.  No Events will work for
     *  new Attributes or RoleReferences before this is called because
     *  this generates the Events that need to be executed.
     *
     *  Specifically, this
     *  <ol>
     *  <li>creates the DDL that must be executed to bring the
     *      database in sync with the events and prepare the
     *      object events to be generated</li>
     *  <li>creates or updates the needed events in memory</li>
     *  <li>generate the new PDL file to represent this object type</li>
     *  <li>execute the DDL</li>
     *  <li>if the DDL executes successfully, update the database to
     *      reflect the new syntax.  Otherwise, throw a PersistenceException
     *      </li>
     *  </ul>
     */
    public com.arsdigita.persistence.metadata.ObjectType save() {
        // 1. Create the DDL that needs to be executed and prepare for
        //    the object events
        final Collection ddlToAdd = makeTableDDL();

        // 2. Generate the PDL.
        final String pdl = makeObjectTypePDL();
        s_log.debug("PDL: " + pdl);

        // 3. execute the DDL
        java.sql.Statement statement = createStatement();

        try {
            if (ddlToAdd != null) {
                String ddl = null;
                try {
                    Iterator iterator = ddlToAdd.iterator();
                    while (iterator.hasNext()) {
                        ddl = (String)iterator.next();
                        statement.executeUpdate(ddl);
                    }
                } catch (SQLException e) {
                    throw PersistenceException.newInstance
			(e.getMessage() + Utilities.LINE_BREAK +
			 "SQL for ADD: " + ddl + Utilities.LINE_BREAK +
			 "PDL: " + pdl, e);
                }
            }

            String mappingSQL = null;
            try {
                String[] mappingTables = makeMappingTablesDDL();
                for (int i = 0; i < mappingTables.length; i++) {
                    mappingSQL = mappingTables[i];
                    s_log.debug("update: " + mappingSQL);
                    statement.executeUpdate(mappingSQL);
                }

            } catch(SQLException e) {
                throw PersistenceException.newInstance
		    (e.getMessage() + Utilities.LINE_BREAK +
		     "SQL for Mapping Table: " + mappingSQL +
		     Utilities.LINE_BREAK + "PDL: " + pdl, e);
            }

            try {
                m_conn.commit();
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e.getMessage(), e);
            }
        } finally {
            try {
                if (m_conn != null) {
                    m_conn.rollback();
                    ConnectionManager.returnConnection(m_conn);                    
                }
                statement.close();
            } catch(SQLException ignored) {
            }
        }


        // 4. if DDL executing is successful, update the database to
        //    reflect the new syntax
        try {
            if (m_dataObject == null) {
                m_dataObject = SessionManager.getSession().create
		    (objectTypeString);
                m_dataObject.set("id", Sequences.getNextValue());
                m_dataObject.set("objectType", objectTypeString);
                m_dataObject.set("dynamicType",
				 m_objectType.getQualifiedName());
                m_dataObject.set("displayName", objectTypeString);
            }

            m_dataObject.set("pdlFile", pdl);
            m_dataObject.save();
        } catch (SQLException e) {
            throw PersistenceException.newInstance("Error saving PDL file", e);
        }

        m_isNew = false;
        m_mappingTables = new ArrayList();
        m_defaultValueMap = new HashMap();

        return type(m_objectType);
    }


    /**
     *  This method returns the ObjectType that is being manipulated
     *  by this DynamicObjectType
     */
    public com.arsdigita.persistence.metadata.ObjectType getObjectType() {
        return type(m_objectType);
    }

    /**
     * Creates the PDL for the object type. Is public for testing/debugging purposes.
     *
     * @return PDL for ObjectType
     */
    public String makeObjectTypePDL() {
	StringWriter pdl = new StringWriter();
	PDLWriter w = new PDLWriter(pdl);
	w.write(m_objectType);
        String result = "model " +
            m_objectType.getModel().getQualifiedName() + ";" +
            Utilities.LINE_BREAK + pdl.toString();
        return result;
    }

    /**
     * Returns the table creaton DDL. Is public for testing/debugging purposes.
     *
     * @return Table creation DDL
     */
    public final Collection makeTableDDL() {
	ArrayList result = new ArrayList();

	if (m_generateTable) {
	    result.add(m_table.getSQL(false));
	} else {
	    for (Iterator it = m_columns.iterator(); it.hasNext(); ) {
		Column col = (Column) it.next();
		result.add(col.getSQL());
	    }
	}

	return result;
    }

    /**
     * Returns an array of Strings containing DDL for new mapping tables.
     * Is public for testing/debugging purposes.
     *
     * @post return != null
     * @return Mapping table creation DDL
     */
    public final String[] makeMappingTablesDDL() {
        String[] mappingDDL;

        if (m_mappingTables.size() > 0) {
            mappingDDL = new String[m_mappingTables.size()];
            int idx = 0;
	    for (Iterator it = m_mappingTables.iterator(); it.hasNext(); ) {
                Table t = (Table) it.next();
                String sql = t.getSQL(false);
                mappingDDL[idx++] = sql;
            }
        } else {
	    mappingDDL = EMPTY_STRING_ARRAY;
	}

        return mappingDDL;
    }

    /**
     *  This prints out a String representation of this object type
     */
    public String toString() {
        String appendString = Utilities.LINE_BREAK + "The following will be " +
            "added to the table:" + makeTableDDL();

        return m_objectType.toString() + appendString;
    }


    /**
     *  This allows the user to either input or output a given
     *  data object type x
     *  <p>
     *  This is only meant to be called from the command line.
     *  the usage is
     *  <code>
     *  java com.arsdigita.persistence.metadataDyanmicObjectType
     *  &lt;[import | export]&gt; &lt;DynamicObjectType&gt; &lt;FileLocation&gt;
     *  &lt;StartupScript&gt; &lt;WebAppRoot&gt;
     *  </code>
     *  <p>
     *  <ul>
     *  <li>The first item, "import" or "export" tells the method whether
     *  you are loading a file into the database or you want to print
     *  a file in the database into the file system.</li>
     *
     *  <li>The DynamicObjectType is the fully qualified name of the
     *      object type.  An example is
     *      <code>com.arsdigita.cms.MyDynamicType</code>
     *  </li>
     *
     *  <li>The FileLocation is location in the file system where
     *      the file should be read from or written to.  For example
     *      /home/tomcat/webapps/enterprise/dynamictypes/cms/MyDynamicType.pdl
     *  </li>
     *
     *  <li>The StartupScript is the location of your enterprise.init script
     *      (or the file that is used to specify the initializers to run
     *       as well as how to access the database).  For example
     *       <code>
     *       /home/tomcat/webapps/acs/WEB-INF/resources/enterprise.init
     *       </code>
     *  </li>
     *
     *  <li>The WebAppRoot is used by the initializers to find the correct
     *      code to execute.  For example <code>/home/tomcat/webapps/enterprise
     *      </code>
     *  </li>
     *  </ul>
     *
     *  So, to export the dynamic type MyDynamicType from the database to
     *  the file system, you can type
     *  <code>
     *  java com.arsdigita.persistence.metadata.DynamicObjectType export
     *  com.arsdigita.cms.MyDynamicType /tmp/MyDynamicType.pdl
     *  /home/tomcat/webapps/enterprise/WEB-INF/resources/enterprise.init
     *  /home/tomcat/webapps/enterprise
     *  </code>
     *  <p>
     *  Make sure that you have the DynamicObjectType.class file
     *  in your classpath (which is something that is not in your classpath
     *  when you typically start your server)
     */
    static public void main(String args[]) {
        String IMPORT = "import";
        String EXPORT = "export";
        String usageString = "Usage: java DyanmicObjectType " +
            " <[" + IMPORT + " | " + EXPORT + "]> <DynamicObjectType> " +
            "<FileLocation> <StartupScript> <WebAppRoot>";

        if (args.length != 5) {
            System.err.println(usageString);
            System.exit(1);
        }

        String type = args[0];
        if (!(type.equalsIgnoreCase(IMPORT) || type.equalsIgnoreCase(EXPORT))) {
            System.err.println
                ("The first argument must specify whether you wish to " +
                 "'import' or 'export'" + Utilities.LINE_BREAK + usageString);
        }

        String objectType = args[1];
        String fileName = args[2];
        String startupScript = args[3];
        String webAppRoot = args[4];

        Startup startup = new Startup(webAppRoot, startupScript);
        startup.setLastInitializer("com.arsdigita.persistence.Initializer");
        startup.init();

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        // open the transaction
        if (! txn.inTxn()) {
            txn.beginTxn();
        }

        // get the data object we will be working with
        DataObject dataObject;
        DataCollection collection = SessionManager.getSession()
            .retrieve(objectTypeString);
        collection.addEqualsFilter("dynamicType", objectType);
        if (collection.next()) {
            dataObject = collection.getDataObject();
        } else {
            dataObject = SessionManager.getSession().create(objectTypeString);
            try {
                dataObject.set("id", Sequences.getNextValue());
            } catch (SQLException e) {
                System.err.println("Unable to create sequence:" +
                                   Utilities.LINE_BREAK + e.getMessage());
                txn.commitTxn();
                System.exit(1);
            }
            dataObject.set("objectType", objectTypeString);
            dataObject.set("dynamicType", objectType);
            dataObject.set("displayName", objectType);
        }

        if (type.equalsIgnoreCase(IMPORT)) {
            // read in the file
            try {
                BufferedReader reader = new BufferedReader
                    (new FileReader(fileName));
                StringBuffer pdlFile = new StringBuffer();

                try {
                    String nextLine = reader.readLine();
                    while (nextLine != null) {
                        pdlFile.append(nextLine + Utilities.LINE_BREAK);
                        nextLine = reader.readLine();
                    }
                } catch (IOException e) {
                    String suffix = "";
                    if (!"".equals(pdlFile.toString())) {
                        suffix = "We were able read the following" +
                            pdlFile.toString();
                    }
                    System.err.println("There was an error reading the file [" +
                                       fileName + "].  " + suffix);
                }

                dataObject.set("pdlFile", pdlFile.toString());
                dataObject.save();
            } catch (FileNotFoundException e) {
                System.err.println("The file you have provided to input [" +
                                   fileName + "] cannot be accessed.");
                if (!(new File(fileName)).canRead()) {
                    System.err.println("The system cannot read the file");
                }
                txn.commitTxn();
                System.exit(1);
            }

        } else {
            String pdlFile = (String)dataObject.get("pdlFile");
            if (pdlFile == null) {
                System.err.println("The object type you have requested [" +
                                   objectType + "] cannot be found.  Please " +
                                   "check the type and try again.");
                txn.commitTxn();
                System.exit(1);
            }

            // write the PDL to the file system
            try {
                (new PrintWriter(new FileOutputStream(fileName))).print(pdlFile);
            } catch (FileNotFoundException e) {
                System.err.println("The file you have provided to input [" +
                                   fileName + "] cannot be accessed.");
                if (!(new File(fileName)).canWrite()) {
                    System.err.println("The system cannot write to the file");
                }
            }
        }

        txn.commitTxn();
        startup.destroy();
    }
}
