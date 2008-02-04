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
package com.arsdigita.formbuilder;

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import com.arsdigita.persistence.Session;
import java.util.Collection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import java.util.ArrayList;


/**
 * This class is used to maintain the list of data queries available
 * to the {@link com.arsdigita.formbuilder.DataDrivenSelect} class.
 *
 * All the queries are expected to return two attributes,
 * <code>id</code> is some unique id for the row, and
 * <code>label</code> is the human friendly label to display.
 *
 * eg
 * <pre>
 * query DataQueryPackages {
 *    BigDecimal id;
 *    String label;
 *
 *     do {
 *        select package_id, pretty_name
 *        from apm_packages
 *        order by pretty_name asc
 *    } map {
 *        id = package_id;
 *        label = pretty_name;
 *   }
 * }
 * </pre>
 */
public class PersistentDataQuery extends ACSObject {
    // First of all a whole load of constants for the PDL

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.formbuilder.PersistentDataQuery";

    public static final String TYPE_ID = "typeId";
    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";


    // Then, the six standard constructors

    /**
     * Constructor. Creates a new persistent data query
     */
    public PersistentDataQuery() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. Used by subclasses to create
     * a data query with a different base data
     * object type
     *
     * @param typeName the base data object type
     */
    public PersistentDataQuery(String typeName) {
        super(typeName);
    }

    /**
     * Constructor. Used by subclasses to create
     * a data query with a different base data
     * object type.
     *
     * @param type the data object type
     */
    public PersistentDataQuery(com.arsdigita.persistence.metadata.ObjectType type) {
        super(type);
    }

    /**
     * Constructor. Instantiates a new persistent
     * data query from a previously retrieved
     * data object
     *
     * @param obj the data object
     */
    public PersistentDataQuery(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. Instantiates a new persistent
     * data query by retrieving a data object
     * with the specified id.
     *
     * @param id the id of the data object
     */
    public PersistentDataQuery(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Used by subclasses to retrieve
     * a persistent data query with a specified
     * oid.
     *
     * @param oid the oid of the data object
     */
    public PersistentDataQuery(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     * Retrieves a collection of all the PersistentDataQuery
     * objects in the data base
     */
    public static Collection getQueries(BebopObjectType type) {
        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        types.addEqualsFilter(TYPE_ID, type.getID());

        ArrayList l = new ArrayList();
        while (types.next()) {
            DataObject obj = types.getDataObject();
            PersistentDataQuery t = new PersistentDataQuery(obj);
            l.add(t);
        }
        return l;
    }

    /**
     * Retrieves the persistent data query with the specified
     * fully qualified pdl query name
     */
    public static PersistentDataQuery findByName(BebopObjectType type,
                                                 String name)
        throws DataObjectNotFoundException {

        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        types.addEqualsFilter(TYPE_ID, type.getID());
        types.addEqualsFilter(NAME, name);

        if (types.next()) {
            DataObject obj = types.getDataObject();
            PersistentDataQuery t = new PersistentDataQuery(obj);
            types.close();
            return t;
        }
        throw new DataObjectNotFoundException("cannot find data query called " + name);
    }

    /**
     * Creates a new persitent data query, initialising the
     * required attributes
     *
     * @param description the pretty name for the query
     * @param name the fully qualified pdl query name
     */
    public static PersistentDataQuery create(BebopObjectType type,
                                             String description,
                                             String name) {
        PersistentDataQuery query = new PersistentDataQuery();

        query.set(TYPE_ID, type.getID());
        query.set(NAME, name);
        query.set(DESCRIPTION, description);

        return query;
    }

    // Now the attribute accessors

    /**
     * Sets the package to which this meta object is scoped
     *
     * @param pack the id of the package
     */
    public void setTypeId(BigDecimal pack) {
        set(TYPE_ID, pack);
    }

    /**
     * Retrieves the id of the packge to whcih this
     * meta object is scoped
     */
    public BigDecimal getTypeId() {
        return (BigDecimal)get(TYPE_ID);
    }

    /**
     * Sets the package to which this meta object is
     * scoped.
     *
     * @param pack the package
     */
    public void setType(BebopObjectType type) {
        set(TYPE_ID, type.getID());
    }

    /**
     * Retrieves the package to which this meta object is
     * scoped.
     */
    public BebopObjectType getType()
        throws DataObjectNotFoundException {

        return new BebopObjectType((BigDecimal)get(TYPE_ID));
    }

    /**
     * Retrieves the pretty name of the query
     */
    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    /**
     * Sets teh pretty name of the query
     *
     * @param description the new pretty name
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Retrieves the fully qualified pdl query
     * name
     */
    public String getName() {
        return (String)get(NAME);
    }

    /**
     * Sets the pdl query name
     *
     * @param name the fully qualified pdl query name
     */
    public void setName(String name) {
        set(NAME, name);
    }


    // Finally put your custom code here
}
