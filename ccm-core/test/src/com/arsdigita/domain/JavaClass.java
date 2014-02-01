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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 * Represents a Java Class and its dependency relationships.
 * @see http://www.clarkware.com/software/JDepend.html
 *
 * @author Jon Orris
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class JavaClass extends DomainObject  {

    private static Logger log =
        Logger.getLogger(JavaClass.class.getName());

    /**
     * BASE_DATA_OBJECT_TYPE represents the full objectType name for the
     * class
     **/
    private static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.domain.Class";


    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    public JavaClass(DataObject object) {
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
    public JavaClass(String typeName) {
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
    public JavaClass(ObjectType type) {
        super(type);
    }


    /**
     * Constructor. Retrieves a JavaClass instance, retrieving an existing
     * note from the database with OID oid. Throws an exception if an
     * object with OID oid does not exist or the object is not of type JavaClass
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
    public JavaClass(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public String getName() {
        return (String) get("name");

    }

    public void setName(String name) {
        set("name", name);
    }

    public boolean isAbstract() {
        return ((Boolean) get("isAbstract")).booleanValue();
    }

    public JavaPackage getPackage() {
        return (JavaPackage) DomainObjectFactory.newInstance((DataObject) get("package"));
    }



}
