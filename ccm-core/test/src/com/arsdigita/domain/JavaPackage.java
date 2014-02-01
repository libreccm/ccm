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
package com.arsdigita.domain;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 * Represents a Java Package and its dependency relationships.
 * @see http://www.clarkware.com/software/JDepend.html
 *
 * @author Jon Orris
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class JavaPackage extends DomainObject  {
    private static Logger log =
        Logger.getLogger(JavaPackage.class.getName());

    /**
     * BASE_DATA_OBJECT_TYPE represents the full objectType name for the
     * class
     **/
    private static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.domain.Package";


    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    public JavaPackage() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public JavaPackage(DataObject object) {
        super(object);
    }
    /**
     * Constructor. The contained DataObject is
     * initialized with a new DataObject with an
     * ObjectType specified by the string
     * typeName.
     *
     * @param typeName The name of the ObjectType of the
     * contained DataObject.
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public JavaPackage(String typeName) {
        super(typeName);
    }

    /**
     * Constructor. The contained DataObject is
     * initialized with a new DataObject with an
     * ObjectType specified by type.
     *
     * @param type The ObjectType of the contained
     * DataObject.
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public JavaPackage(ObjectType type) {
        super(type);
    }


    /**
     * Constructor. Retrieves a JavaPackage instance, retrieving an existing
     * note from the database with OID oid. Throws an exception if an
     * object with OID oid does not exist or the object is not of type JavaPackage
     *
     * @param oid The OID for the retrieved
     * DataObject.
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     *
     * @exception DataObjectNotFoundException Thrown if we cannot
     * retrieve a data object for the specified OID
     *
     **/
    public JavaPackage(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public String getName() {
        return (String) get("name");

    }

    public void setName(String name) {
        set("name", name);

    }

    public DomainCollection getClasses()  {
        DataCollection c = (DataCollection) get("classes");
        return new DomainCollection(c)  {

                public DomainObject getDomainObject()  {
                    return new JavaClass(m_dataCollection.getDataObject());
                }
            };

    }

    public DomainCollection getAfferentPackages() {
        DataAssociation a = (DataAssociation) get("depends_on_set");
        return new DomainCollection(a)  {

                public DomainObject getDomainObject()  {
                    return new JavaPackage(m_dataCollection.getDataObject());
                }
            };

    }

    public DomainCollection getEfferentPackages() {
        DataAssociation a = (DataAssociation) get("used_by_set");
        return new DomainCollection(a)  {

                public DomainObject getDomainObject()  {
                    return new JavaPackage(m_dataCollection.getDataObject());
                }
            };

    }


    public int getAfferentCoupling() {
        DataQuery query = getSession().retrieveQuery("com.arsdigita.domain.AfferentCoupling");
        query.setParameter("id", get("id") );
        query.next();
        Number count = (Number) query.get("count");
        return count.intValue();
    }

    public int getEfferentCoupling() {
        DataQuery query = getSession().retrieveQuery("com.arsdigita.domain.EfferentCoupling");
        query.setParameter("id", get("id") );
        query.next();
        Number count = (Number) query.get("count");
        query.close();
        return count.intValue();
    }

    public float getAbstractness() {
        DataQuery query = getSession().retrieveQuery("com.arsdigita.domain.Abstractness");
        query.setParameter("id", get("id") );
        query.next();
        Number abstractness = (Number) query.get("abstractness");
        query.close();
        return abstractness.floatValue();

    }

    public float getInstability() {
        final float efferentCoupling = getEfferentCoupling();
        return efferentCoupling / (getAfferentCoupling() + efferentCoupling);
    }
}
