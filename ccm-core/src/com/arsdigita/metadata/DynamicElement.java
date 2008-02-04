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
package com.arsdigita.metadata;

import com.arsdigita.persistence.SessionManager;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ForeignKey;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;

/**
 * DynamicElement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 **/

abstract class DynamicElement {

    public final static String versionId = "$Id: DynamicElement.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * This takes an ObjectType name and model, and generates a unique
     * table name that can be used to store the object type.
     *
     * @param type The object type.
     * @return a unique table name that is used to store instance data
     *         for the object type
     */
    static String generateTableName(ObjectType type) {
	return type.getName();
    }

    /**
     * This takes a table name and a Property to generate a unique
     * column name.
     *
     * @param table The table containing the column.
     * @param name The proposed name of the column.
     * @return a unique column name that will be used to store this property
     */
    static String generateColumnName(Table table, String name) {
	return name;
    }


    /**
     * Determines a unique name for a mapping table for a particular
     * role reference and object type.
     *
     * @param prop The property being stored in the table.
     * @return a unique table name
     */
    static String generateTableName(Property prop) {
	return generateTableName(prop.getContainer()) + "_" + prop.getName();
    }

    static ForeignKey fk(Table table, String prefix, UniqueKey uk) {
	Column[] cols = new Column[uk.getColumns().length];
	for (int i = 0; i < cols.length; i++) {
	    Column col = uk.getColumns()[i];
	    String name;
	    if (prefix == null) {
		name = col.getName();
	    } else {
		name = prefix + "_" + col.getName();
	    }
	    String columnName = generateColumnName(table, name);
	    cols[i] = new Column(columnName, col.getType(), col.getSize(),
				 col.getScale(), true);
	    table.addColumn(cols[i]);
	}
	return new ForeignKey(table, null, cols, uk);
    }

    static ObjectType type
	(com.arsdigita.persistence.metadata.CompoundType type) {
	if (type == null) { return null; }
	return SessionManager.getMetadataRoot().getRoot().getObjectType
            (type.getQualifiedName());
    }

    static com.arsdigita.persistence.metadata.ObjectType type
	(ObjectType type) {
	if (type == null) { return null; }
	return SessionManager.getMetadataRoot().getObjectType
	    (type.getQualifiedName());
    }

    static Property property
	(com.arsdigita.persistence.metadata.Property prop) {
	if (prop == null) { return null; }
	return type(prop.getContainer()).getProperty(prop.getName());
    }

    static com.arsdigita.persistence.metadata.Property property
	(Property prop) {
	if (prop == null) { return null; }
	return type(prop.getContainer()).getProperty(prop.getName());
    }

}
