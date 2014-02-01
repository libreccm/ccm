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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Utilities;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.Value;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * DynamicObjectTypeTest tests to make sure that the DynamicObjectType
 * class works as advertised.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 */

public class DynamicObjectTypeTest extends PersistenceTestCase {

    
    private static Logger s_log =
        Logger.getLogger(DynamicObjectTypeTest.class.getName());

    Session m_session;

    private MetadataRoot m_root;
    private ArrayList m_tables = new ArrayList();
    private ArrayList m_objectTypes = new ArrayList();

    String superTypeString = "com.arsdigita.kernel.ACSObject";
    String objectTypeModel = "com.arsdigita.kernel";
    ObjectType supertype;
    DynamicObjectType dot;
    int counter = 0;
    String objectTypeName = "newObject00";

    public DynamicObjectTypeTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        super.persistenceSetUp();

        load("com/arsdigita/metadata/pdl/DataOperation.pdl");
        m_session = SessionManager.getSession();
        m_root = m_session.getMetadataRoot();

        supertype = m_root.getObjectType(superTypeString);
        while (m_root.getObjectType(objectTypeModel + "." +
                                    objectTypeName) != null) {
            counter++;
            String nextCount = (new Integer(counter)).toString();
            if (counter < 10) {
                nextCount = "0" + nextCount;
            }
            objectTypeName = objectTypeName.substring
                (0, objectTypeName.length() - 2) + nextCount;
        }

        dot = new DynamicObjectType(objectTypeName, supertype);
    }

    protected void persistenceTearDown() {
//        load("com/arsdigita/metadata/pdl/DataOperation.pdl");
        Iterator iter = m_tables.iterator();
        java.sql.Statement statement = null;
        try {
            statement = SessionManager.getSession()
                .getConnection()
                .createStatement();
            while (iter.hasNext()) {
                String table = (String)iter.next();
                try {
                    statement.executeUpdate("drop table " + table);
                } catch (Exception e) {
                    s_log.info("Error executing statement " +
                               "'drop table " + table + "': " + e);
                }
            }
        } catch (Exception e) {
            s_log.info("Error creating statement: " + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                //ignore
            }
        }

        getSession().getTransactionContext().abortTxn();
        getSession().getTransactionContext().beginTxn();

        iter = m_objectTypes.iterator();
        while (iter.hasNext()) {
            DataOperation operation = SessionManager.getSession()
                .retrieveDataOperation
                ("metadatatest.DataOperationToDeleteTestDynamicObjectTypes");
            String name = ((ObjectType)iter.next()).getQualifiedName();
            operation.setParameter("dynamicType", name.toLowerCase());
            operation.execute();
        }
        // this is here so that the "delete" operation above takes
        getSession().getTransactionContext().commitTxn();
        getSession().getTransactionContext().beginTxn();
        super.persistenceTearDown();
    }

    private ObjectMap getObjectMap(CompoundType type) {
	Root root = m_root.getRoot();
	return root.getObjectMap(root.getObjectType(type.getQualifiedName()));
    }

    private Column getColumn(Property p) {
	Root root = m_root.getRoot();
	ObjectMap om = root.getObjectMap
	    (root.getObjectType(p.getContainer().getQualifiedName()));
	Mapping m = om.getMapping(Path.get(p.getName()));
	if (m instanceof Value) {
	    return ((Value) m).getColumn();
	} else {
	    return null;
	}
    }

    private void addTable(Table table) {
	if (table == null) { return; }
	if (m_tables.contains(table.getName())) {
	    m_tables.add(table.getName());
	}
    }

    // ORA-00054: Resource busy and aquire with NOWAIT specified.

    public void FAILStestConstructors() throws Exception {
        dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
        dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
        dot.addRequiredAttribute("requiredAttribute", MetadataRoot.BOOLEAN,
                                 new Boolean(true));
        dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
                                 "my new text string");
        ObjectType objectType = dot.save();

        addTable(getObjectMap(objectType).getTable());
        m_objectTypes.add(objectType);

        // make sure that the supertype is valid
        String actualSuperType = objectType.getSupertype().getQualifiedName();

        assertTrue("The supertype should have been '" + superTypeString + "'" +
                   " but actually was '" + actualSuperType + "'",
                   superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(),
                           new String[] {"optionalAttribute",
                                         "optionalAttribute2",
                                         "requiredAttribute",
                                         "requiredAttribute2"});

        dot = new DynamicObjectType("com.arsdigita.kernel." + objectTypeName);
        dot.addRequiredAttribute("requiredAttribute3", MetadataRoot.BOOLEAN,
                                 new Boolean(false));
        dot.addRequiredAttribute("requiredAttribute4", MetadataRoot.STRING, 400,
                                 "my really cool default");
        objectType = dot.save();

        actualSuperType = objectType.getSupertype().getQualifiedName();

        assertTrue("The supertype should have been '" + superTypeString + "'" +
                   " but actually was '" + actualSuperType + "'",
                   superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(),
                           new String[] {"optionalAttribute",
                                         "optionalAttribute2",
                                         "requiredAttribute",
                                         "requiredAttribute2",
                                         "requiredAttribute3",
                                         "requiredAttribute4"});

        dot = new DynamicObjectType(objectType);
        dot.addRequiredAttribute("requiredAttribute5", MetadataRoot.DOUBLE,
                                 new Double(4));
        dot.addRequiredAttribute("requiredAttribute6", MetadataRoot.DATE, 400,
                                 new Date());
        objectType = dot.save();

        actualSuperType = objectType.getSupertype().getQualifiedName();

        assertTrue("The supertype should have been '" + superTypeString + "'" +
                   " but actually was '" + actualSuperType + "'",
                   superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(),
                           new String[] {"optionalAttribute",
                                         "optionalAttribute2",
                                         "requiredAttribute",
                                         "requiredAttribute2",
                                         "requiredAttribute3",
                                         "requiredAttribute4",
                                         "requiredAttribute5",
                                         "requiredAttribute6"});
        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation
            ("metadatatest.DataOperationToDeleteTestDynamicObjectTypes");
        operation.setParameter("dynamicType", "com.arsdigita.kernel." +
                               objectTypeName);


        // now test
        //DynamicObjectType(String name, ObjectType supertype, String model)
        dot = new DynamicObjectType("testType", supertype, null);
        ObjectType dotObject = dot.save();
        Model dotModel = dotObject.getModel();
        m_objectTypes.add(dotObject);
        addTable(getObjectMap(dotObject).getTable());

        dot = new DynamicObjectType("testType2", null, dotObject.getModel());
        Property prop = dot.addRequiredAttribute("requiredAttribute6",
                                                 MetadataRoot.DATE, 400,
                                                 new Date());
        dotObject = dot.save();

        m_objectTypes.add(dotObject);
	addTable(getObjectMap(dotObject).getMapping
		 (Path.get(prop.getName())).getTable());

        assertTrue("The model name should be [com.arsdigita.kernel] not [" +
                   dotObject.getModel().getName() + "]",
                   "com.arsdigita.kernel".equals(dotObject.getModel().getName()));
        assertTrue(dotObject.getModel().equals(dotModel));

        // make sure an invalid name throws an exception
        try {
            dot = new DynamicObjectType("my.test.object.type", supertype);
            fail("using an invalid type should have thrown an exception");
        } catch (PersistenceException e) {
            // it should be here so we fall through
        }

        // make sure it is not possible to create an object type that
        // is supposed to be a static object type
        try {
            dot = new DynamicObjectType("com.arsdigita.kernel.ACSObject");
            fail("Static object types should not be allowed to become " +
                 "dynamic object types");
        } catch (PersistenceException e) {
            // it should be here so we fall through
        }
    }


    /**
     *  This tests the addOptionalAttribute method within DynamicObjectType
     */
    public void testAddRequiredAttribute() {
        Property prop = dot.addRequiredAttribute("myOptionalProp", m_root.DATE,
                                                 new Date());

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myOptionalProp"});

        Property prop2 = dot.addRequiredAttribute("myOptionalProp2",
                                                  m_root.FLOAT, 400,
                                                  new Float(4));
        assertTrue("the size for the new string attribute should be 400, not " +
                   getColumn(prop2).getSize(), getColumn(prop2).getSize() == 400);

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myOptionalProp", "myOptionalProp2"});


        // make sure that the column names are different but the table names
        // are the same
        assertTrue("the tables names are different when they should both be " +
                   "the same", getColumn(prop).getTableName().equals
                   (getColumn(prop2).getTableName()));

        assertTrue("the column names are the same when they should be different ",
                   !getColumn(prop).getColumnName().equals
                   (getColumn(prop2).getColumnName()));

        // duplicate names should throw an exception
        try {
            prop = dot.addRequiredAttribute("myOptionalProp", m_root.INTEGER,
                                            new Integer(3));
            fail("Adding attributes with the same name should throw an " +
                 "exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        assertTrue("An optional attribute should be required",
                   prop.getMultiplicity() == Property.REQUIRED);
    }


    /**
     *  This tests the addOptionalAttribute method within DynamicObjectType
     */
    public void testAddOptionalAttribute() {
        Property prop = dot.addOptionalAttribute("myOptionalProp", m_root.INTEGER);

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myOptionalProp"});

        Property prop2 = dot.addOptionalAttribute("myOptionalProp2",
                                                  m_root.STRING, 400);
        assertTrue("the size for the new string attribute should be 400, not " +
                   getColumn(prop2).getSize(), getColumn(prop2).getSize() == 400);

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myOptionalProp", "myOptionalProp2"});

        // make sure that the column names are different but the table names
        // are the same
        assertTrue("the tables names are different when they should both be " +
                   "the same", getColumn(prop).getTableName().equals
                   (getColumn(prop2).getTableName()));

        assertTrue("the column names are the same when they should be different ",
                   !getColumn(prop).getColumnName().equals
                   (getColumn(prop2).getColumnName()));

        // duplicate names should throw an exception
        try {
            prop = dot.addOptionalAttribute("myOptionalProp", m_root.INTEGER);
            fail("Adding attributes with the same name should throw an " +
                 "exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        assertTrue("An optional attribute should be nullable",
                   prop.getMultiplicity() == Property.NULLABLE);
    }

    // ORA-00054: Resource busy and aquire with NOWAIT specified.
    public void FAILStestAddRoleReference() {
        ObjectType acsobj =
            m_root.getObjectType("com.arsdigita.kernel.ACSObject");

        DataObject defaultObj =
            getSession().create("com.arsdigita.kernel.ACSObject");
        defaultObj.set("id", new BigDecimal(-50));
        defaultObj.set("objectType", "com.arsdigita.kernel.ACSObject");
        defaultObj.set("displayName", "Default Object");
        defaultObj.save();

        ObjectType objectType = dot.save();

        dot.addCollectionAssociation("myCollectionRR", acsobj);
        dot.save();
        dot.addOptionalAssociation("myOptionalRR", acsobj);
        dot.addRequiredAssociation("myRequiredRR", acsobj, new BigDecimal(-50));

        objectType = dot.save();

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myCollectionRR",
                                         "myOptionalRR",
                                         "myRequiredRR"});


	addTable(((JoinThrough) getObjectMap(objectType).getMapping
		  (Path.get("myCollectionRR"))).getFrom().getTable());
	addTable(getObjectMap(objectType).getTable());
        m_objectTypes.add(objectType);

        DataObject associated =
            getSession().create("com.arsdigita.kernel.ACSObject");
        associated.set("id", new BigDecimal(-51));
        associated.set("objectType", "com.arsdigita.kernel.ACSObject");
        associated.set("displayName", "Default Object");
        associated.save();

        DataObject testObj = getSession().create(objectType);
        testObj.set("id", new BigDecimal(-52));
        testObj.set("objectType", objectType.getQualifiedName());
        testObj.set("displayName", "Default Object");
        testObj.set("myOptionalRR", associated);

        // TODO: remove the line below once 192076 is complete
        testObj.set("myRequiredRR", defaultObj);

        DataAssociation assoc = (DataAssociation)testObj.get("myCollectionRR");
        assoc.add(defaultObj);
        assoc.add(associated);

        testObj.save();

        DataObject associated2 = (DataObject)testObj.get("myOptionalRR");
        DataObject associated3 = (DataObject)testObj.get("myRequiredRR");

        assertTrue("Optional attribute differs", associated.equals(associated2));
        assertTrue("Required attribute differs", defaultObj.equals(associated3));

        assoc = (DataAssociation)testObj.get("myCollectionRR");
        DataAssociationCursor cursor = assoc.cursor();

        while (cursor.next()) {
            cursor.remove();
        }

        testObj.delete();
        defaultObj.delete();
        associated.delete();
    }

    // ORA-00054: Resource busy and aquire with NOWAIT specified.

    public void FAILStestResaving() {
        ObjectType objectType = dot.save();

	addTable(getObjectMap(objectType).getTable());
        m_objectTypes.add(objectType);

        dot.addOptionalAttribute("testAttribute", MetadataRoot.STRING);
        ObjectType type = dot.save();

        dot = new DynamicObjectType(type);

        dot.addRequiredAttribute("testRequired", MetadataRoot.STRING, "foo");
        type = dot.save();

        DataObject testObj = getSession().create(type);
        testObj.set("id", new BigDecimal(-60));
        testObj.set("objectType", type.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttribute", "Test Attribute");
        testObj.set("testRequired", "Test Required");
        testObj.save();
        OID testOID = testObj.getOID();

        testObj = getSession().retrieve(testOID);

        String value = (String)testObj.get("testAttribute");
        assertTrue("Optional value not saved", value.equals("Test Attribute"));

        value = (String)testObj.get("testRequired");
        assertTrue("Required value not saved", value.equals("Test Required"));

        testObj.delete();
    }

    // test that all child objects have their events regenerated when a
    // parent class is altered
    // ORA-00054: Resource busy and aquire with NOWAIT specified.

    public void FAILStestAlteredParents() {
        ObjectType type = dot.save();

        DynamicObjectType sub = new DynamicObjectType("childType", type);
        ObjectType type2 = sub.save();

        DynamicObjectType subsub = new DynamicObjectType("subsubType", type2);
        ObjectType type3 = subsub.save();

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        dot.save();

        // The below code will throw an error if the event regeneration fails
        DataObject testObj = getSession().create(type2);
        testObj.set("id", new BigDecimal(-53));
        testObj.set("objectType", type2.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttr1", "Test Attribute");
        testObj.save();

        testObj = getSession().retrieve(new OID(type2, new BigDecimal(-53)));

        String value = (String)testObj.get("testAttr1");
        assertTrue("Child Value not saved correctly",
                   value.equals("Test Attribute"));

        testObj.delete();

        testObj = getSession().create(type3);
        testObj.set("id", new BigDecimal(-54));
        testObj.set("objectType", type3.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttr1", "Test Attribute");
        testObj.save();

        testObj = getSession().retrieve(new OID(type3, new BigDecimal(-54)));

        value = (String)testObj.get("testAttr1");
        assertTrue("Grand child value not saved correctly",
                   value.equals("Test Attribute"));

        testObj.delete();

	addTable(getObjectMap(type3).getTable());
        m_objectTypes.add(type3);
	addTable(getObjectMap(type2).getTable());
        m_objectTypes.add(type2);
	addTable(getObjectMap(type).getTable());
        m_objectTypes.add(type);
    }

    // test the case where a role reference type is change
    // ORA-00054: Resource busy and aquire with NOWAIT specified.
    public void FAILStestAlteredAssociation() {
        ObjectType type = dot.save();

        DynamicObjectType subdot = new DynamicObjectType("subtype", type);
        ObjectType subtype = subdot.save();

        DynamicObjectType dot2 =
            new DynamicObjectType("associater", supertype);
        dot2.addOptionalAssociation("testAssoc1", type);
        dot2.addOptionalAssociation("testAssoc2", subtype);

        ObjectType type2 = dot2.save();

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        dot.save();

        DataObject associated = getSession().create(type);
        associated.set("id", new BigDecimal(-56));
        associated.set("objectType", type.getQualifiedName());
        associated.set("displayName", "Default Object");
        associated.set("testAttr1", "Test Attr");
        associated.save();

        DataObject subassociated = getSession().create(subtype);
        subassociated.set("id", new BigDecimal(-58));
        subassociated.set("objectType", subtype.getQualifiedName());
        subassociated.set("displayName", "Default Object");
        subassociated.set("testAttr1", "Test Attr");
        subassociated.save();

        DataObject testObj = getSession().create(type2);
        testObj.set("id", new BigDecimal(-57));
        testObj.set("objectType", type2.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAssoc1", associated);
        testObj.set("testAssoc2", subassociated);
        testObj.save();

        testObj = getSession().retrieve(new OID(type2, new BigDecimal(-57)));

        DataObject associated2 = (DataObject)testObj.get("testAssoc1");
        String value = (String)associated2.get("testAttr1");

        assertTrue("Associated value not retrieved correctly",
                   value.equals("Test Attr"));

        associated2 = (DataObject)testObj.get("testAssoc2");
        value = (String)associated2.get("testAttr1");

        assertTrue("Associated subtype value not retrieved correctly",
                   value.equals("Test Attr"));

        testObj.delete();
        associated.delete();
        subassociated.delete();

	addTable(getObjectMap(type2).getTable());
        m_objectTypes.add(type2);

	addTable(getObjectMap(subtype).getTable());
        m_objectTypes.add(subtype);

	addTable(getObjectMap(type).getTable());
        m_objectTypes.add(type);
    }

    public void testSave() {
        // TODO
        // 1. test for and handle the situation where the generated table
        //    name already exists
        // 2. make the checks with locking the table, etc for race
        //    conditions
        // 3. Test for the situation where one of the column names
        //    is a duplicate (even though it should not happen)

    }


    /**
     *  This takes an Iterator and an Array and makes sure
     *  that they contain the same items.  It fails if they do not
     */
    private void validateProperties(Iterator properties, String[] propNames) {
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();
        for (int i=0; i<propNames.length; i++) {
            list.add(propNames[i]);
        }

        int count = 0;
        while (properties.hasNext()) {
            count++;
            String name = ((Property)properties.next()).getName();
            list2.add(name);
            assertTrue("The ObjectType contained the property [" + name + "] " +
                       "but it should not have", list.contains(name));
        }

        if (count != list.size()) {
            fail("The ObjectType had " + count + " elements but the array " +
                 "only had " + list.size() + Utilities.LINE_BREAK +
                 "The ObjectType had " + list2.toString() +
                 Utilities.LINE_BREAK + "while it should have had " +
                 Utilities.LINE_BREAK + list.toString());
        }
    }

    /**
     *  Here we want to make sure that we get unique names that
     *  are less than 26 characters long
     */
    /*
    public void testGenerateTableName() {
        //String generateTableName(String modelName, String objectName);
        Collection list = new ArrayList();

        for (int i = 0; i < 200; i++) {
            String tableName = m_generator.generateTableName("com.arsdigita.foo",
                                                             "myObjectName");
            assertTrue("generateTable produced duplicated table names: " + tableName,
                   !list.contains(tableName.toLowerCase()));
            list.add(tableName.toLowerCase());
        }

        // now that we know that it does not put out duplicates, let's check
        // for length
        String tableName = m_generator.generateTableName
            ("my.long.project.namespace.foo", "myReallyReallyLongObjectName");
        assertTrue("the table name passed back is too long",
               tableName.length() <= 26);

        // using a table we know that is in the system, let's see if
        // we get back a unique name
        tableName = m_generator.generateTableName("acs", "objects");
        assertTrue("the table generated is not correct",
               !"acs_objects".equals(tableName));

    }
    */

    /**
     * Check that we can extend a non-integer keyed table
     */
    public void testNonIntegerPrimaryKey() {
        DynamicObjectType dot = new DynamicObjectType(
                                                      "subEmail",
                                                      m_root.getObjectType("com.arsdigita.kernel.EmailAddress"));

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        ObjectType type = dot.save();

        DataObject testObj = getSession().create(type);

        testObj.set("emailAddress", "bob@bob.bob");
        testObj.set("isBouncing", Boolean.FALSE);
        testObj.set("isVerified", Boolean.TRUE);
        testObj.set("testAttr1", "hello bob");

        testObj.save();

        testObj = getSession().retrieve(new OID(type, "bob@bob.bob"));

        assertTrue("Primary key not set",
               ((String)testObj.get("emailAddress")).equals("bob@bob.bob"));
        assertTrue("Test attribute not saved",
               ((String)testObj.get("testAttr1")).equals("hello bob"));

        testObj.delete();

	addTable(getObjectMap(type).getTable());
        m_objectTypes.add(type);
    }


    /**
     *  This makes sure that the system generates unique column names
     *  that can be added to an alter table or create table statement
     */
    /*
    public void testGenerateColumnName() {
        //TODO
        //String generateColumnName(ObjectType obectType, String proposedName);

        ArrayList columns = new ArrayList();

        DynamicObjectType dot = new DynamicObjectType("myTestObjectType3",
                                                      m_root.getObjectType
                                                      ("com.arsdigita.kernel.ACSObject"));

        ObjectType objectType = dot.getObjectType();
        Table table = objectType.getReferenceKey().getTable();

        // let's test to make sure that it gives back different columns
        for (int i = 0; i < 200; i++) {
            String columnName = m_generator.generateColumnName(objectType,
                                                               "myObjectName");
            assertTrue("generateColumn produced a duplicate column name: " +
                   columnName, !columns.contains(columnName.toLowerCase()));
            Property property = new Property("prop" + i,
                                             MetadataRoot.BIGDECIMAL,
                                             Property.REQUIRED);

            columns.add(columnName.toLowerCase());
            property.setColumn(new Column(table, columnName,
                                          java.sql.Types.INTEGER, 32));
            objectType.addProperty(property);
        }

        // now that we know that it does not put out duplicates, let's check
        // for length
        String columnName = m_generator.generateColumnName
            (objectType, "myReallyReallyLongObjectName");
        assertTrue("the column name passed back is too long",
               columnName.length() <= 26);
    }
    */

}
