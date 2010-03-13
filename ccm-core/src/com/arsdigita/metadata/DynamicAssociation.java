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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Association;
import com.arsdigita.persistence.metadata.Utilities;
import com.arsdigita.util.Assert;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.ForeignKey;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;
import com.redhat.persistence.pdl.PDLWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * This class provides support to create dynamic two-way associations between
 * any object types.  Link attributes will eventually be supported.
 *
 * @author Patrick McNeill
 * @version $Revision: #20 $ $Date: 2004/08/16 $
 */

public class DynamicAssociation extends DynamicElement {
    // the model this association will belong to
    private Model m_model;

    // the two primary roles for this association
    private Property m_prop1;
    private Property m_prop2;

    private Root m_root =
        SessionManager.getSession().getMetadataRoot().getRoot();

    // the DataObject containing information about this association
    // null indicates a new association
    private DataObject m_dataObject = null;

    private static final String objectTypeString =
        "com.arsdigita.persistence.DynamicAssociation";

    private static final int COLLECTION =
	com.arsdigita.persistence.metadata.Property.COLLECTION;
    private static final int NULLABLE =
	com.arsdigita.persistence.metadata.Property.NULLABLE;

    /**
     * Load a pre-existing DynamicAssociation for editing.  Note this is
     * rather useless right now since we don't support link attributes.
     *
     * @param modelName the fully-qualified model name for this association
     * @param objectType1 the fully-qualified name of the datatype of the
     *                    first property
     * @param property1 the name of the first property
     * @param objectType2 the fully-qualified name of the datatype of the
     *                    second property
     * @param property2 the name of the second property
     * @throws PersistenceException if either ObjectType is null or if they
     *                              do not contain the correct property
     */
    public DynamicAssociation(String modelName,
                              String objectType1,
                              String property1,
                              String objectType2,
                              String property2) {
        Model model = Model.getInstance(modelName);
        ObjectType type1 = m_root.getObjectType(objectType1);
        ObjectType type2 = m_root.getObjectType(objectType2);

        if (type1 == null) {
            throw new PersistenceException(objectType1 + " does not exist");
        }

        if (type2 == null) {
            throw new PersistenceException(objectType2 + " does not exist");
        }

        Role prop1 = (Role) type2.getProperty(property1);
        Role prop2 = (Role) type1.getProperty(property2);

        if ((prop1 == null) || (prop2 == null)) {
            throw new PersistenceException
		("Either " + property1 + " or " + property2 + " is null");
        }

        if (!prop1.isReversable() || !prop2.isReversable()) {
            throw new PersistenceException
		("Either " + property1 + " or " + property2 + " does not " +
		 "belong to an association");
        }

        if (!prop1.getReverse().equals(prop2)) {
            throw new PersistenceException
		("Properties belong to different associations");
        }

        m_prop1 = prop1;
        m_prop2 = prop2;
        m_model = model;

        DataCollection collection = SessionManager.getSession()
            .retrieve(objectTypeString);
        collection.addEqualsFilter("modelName", model.getQualifiedName());
        collection.addEqualsFilter("property1", prop1.getName());
        collection.addEqualsFilter
            ("objectType1", prop1.getType().getQualifiedName());
        collection.addEqualsFilter("property2", prop2.getName());
        collection.addEqualsFilter
            ("objectType2", prop2.getType().getQualifiedName());

        try {
            if (!collection.next()) {
                throw new PersistenceException
		    ("The association you have requsted is static, and thus " +
		     "cannot be used as a dynamic association");
            }

            m_dataObject = collection.getDataObject();
        } finally {
            collection.close();
        }
    }

    /**
     * Creates a new DynamicAssociation.  The two named properties will
     * be created with the given datatypes and multiplicities.
     *
     * @param modelName the fully-qualified model name for this association
     * @param objectType1 the fully-qualified name of the datatype of the
     *                    first property
     * @param property1 the name of the first property
     * @param objectType2 the fully-qualified name of the datatype of the
     *                    second property
     * @param property2 the name of the second property
     * @throws PersistenceException if either ObjectType is null or if they
     *                              do not contain the correct property
     */
    public DynamicAssociation(String modelName,
                              String objectType1,
                              String property1,
                              int multiplicity1,
                              String objectType2,
                              String property2,
                              int multiplicity2) {
        Model model = Model.getInstance(modelName);

        ObjectType type1 = m_root.getObjectType(objectType1);
        ObjectType type2 = m_root.getObjectType(objectType2);

        if (type1 == null) {
            throw new PersistenceException(objectType1 + " does not exist");
        }

        if (type2 == null) {
            throw new PersistenceException(objectType2 + " does not exist");
        }

        Role prop1 = (Role) type1.getProperty(property1);
        Role prop2 = (Role) type2.getProperty(property2);

        if ((prop1 != null) || (prop2 != null)) {
            throw new PersistenceException
		("Either " + property1 + " or " + property2 + " is not null");
        }

        prop1 = new Role(property1, type1, false, multiplicity1 == COLLECTION,
			 multiplicity1 == NULLABLE);
        prop2 = new Role(property2, type2, false, multiplicity2 == COLLECTION,
			 multiplicity2 == NULLABLE);

	prop1.setReverse(prop2);

        m_prop1 = prop1;
        m_prop2 = prop2;
        m_model = model;
    }

    public com.arsdigita.persistence.metadata.Property getProperty1() {
        return property(m_prop1);
    }

    public com.arsdigita.persistence.metadata.Property getProperty2() {
        return property(m_prop2);
    }

    /**
     * Saves this DynamicAssociation.  If it's a new Association, the table
     * will be created.  Editing really makes no sense right now, so it
     * won't do a whole lot but resave the PDL.  The Properties are also
     * added to the object types, facilitating retrieval of the association.
     *
     * @return the newly created Association
     */
    public Association save() {
        ObjectType type1 = (ObjectType)m_prop1.getType();
        ObjectType type2 = (ObjectType)m_prop2.getType();

        // this is an add, so we need to do stuff
        if (m_dataObject == null) {
	    ObjectMap om1 = m_root.getObjectMap(type1);
	    ObjectMap om2 = m_root.getObjectMap(type2);

            UniqueKey from = om2.getTable().getPrimaryKey();
            UniqueKey to = om1.getTable().getPrimaryKey();

            if ((from == null) || (to == null)) {
                throw new PersistenceException
		    ("One of the object type tables does not have a key.");
            }

            Property p2 = type1.getProperty(m_prop2.getName());
            Property p1 = type2.getProperty(m_prop1.getName());
            Assert.isTrue(p2==null || p2==m_prop2);
            Assert.isTrue(p1==null || p1==m_prop1);
            if (p2 == null) {
                type1.addProperty(m_prop2);
            }
            if (p1 == null) {
                type2.addProperty(m_prop1);
            }

            Table mappingTable = new Table(generateTableName(m_prop1));
            m_root.addTable(mappingTable);

	    ForeignKey fromKey = fk(mappingTable, "from", from);
	    ForeignKey toKey = fk(mappingTable, "to", to);

	    om2.addMapping(new JoinThrough(Path.get(m_prop1.getName()),
					   fromKey, toKey));
	    om1.addMapping(new JoinThrough(Path.get(m_prop2.getName()),
					   toKey, fromKey));

            String ddl = mappingTable.getSQL(false);

            java.sql.Statement statement = null;
            java.sql.Connection conn = null;
            try {
                conn = ConnectionManager.getConnection();
                statement = conn.createStatement();
                statement.executeUpdate(ddl);
            } catch (SQLException e) {
                throw PersistenceException.newInstance
		    (e.getMessage() + Utilities.LINE_BREAK +
		     "SQL for ADD: " + ddl, e);
            } finally {
                try {
                    if (conn != null) { ConnectionManager.returnConnection(conn); }
                    if (statement != null) { statement.close(); }
                } catch(SQLException ignored) {
                }
            }
        }

        StringWriter sw = new StringWriter();
        PDLWriter pw = new PDLWriter(sw);
        pw.writeAssociation(m_prop1);
        String pdl = "model " + m_model.getQualifiedName() + ";" +
            Utilities.LINE_BREAK + sw.toString();

        try {
            if (m_dataObject == null) {
                m_dataObject = SessionManager.getSession()
                    .create(objectTypeString);

                m_dataObject.set("id", Sequences.getNextValue());
                m_dataObject.set("objectType", objectTypeString);
                m_dataObject.set("displayName", objectTypeString);
                m_dataObject.set("modelName", m_model.getQualifiedName());
                m_dataObject.set("property1", m_prop1.getName());
                m_dataObject.set("objectType1", type1.getQualifiedName());
                m_dataObject.set("property2", m_prop2.getName());
                m_dataObject.set("objectType2", type2.getQualifiedName());
            }

            m_dataObject.set("pdlFile", pdl);
            m_dataObject.save();
        } catch (SQLException e) {
            throw PersistenceException.newInstance("Error saving PDL file", e);
        }

        return getProperty1().getAssociation();
    }
}
