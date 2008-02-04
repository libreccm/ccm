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

import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.util.Iterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * ObjectTypeValidator
 *
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class ObjectTypeValidator  {

    public final static String versionId = "$Id: ObjectTypeValidator.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(ObjectTypeValidator.class.getName());
    DataObjectManipulator m_manipulator;

    static  {
       s_log.setLevel(Level.DEBUG);
    }

    public ObjectTypeValidator() {
        m_manipulator = new DataObjectManipulator();
    }

    public void performCRUDTest(String objectTypeName) throws Exception {
        try {
            s_log.info("CRUDTest on " + objectTypeName);
            DataObject object = SessionManager.getSession().create(objectTypeName);
            reportPropertyTypes(object);

            initializeObject( object, null );
            OID id = object.getOID();
            s_log.info("Initialized object with id: " + id);
            object.save();
            object = SessionManager.getSession().retrieve(id);
            Assert.assertNotNull(object, "Object of type: " + objectTypeName + "and id: " + id + " was not found!");
            checkDefaultValues(object);

            checkUpdates(object, null);
            deleteObject(id);

        } catch (Exception e) {
            s_log.info("END CRUDTest on " + objectTypeName + " With error!" );
            s_log.info(e.getMessage());
            s_log.info("");
            s_log.info("");
            throw e;
        }

        s_log.info("END CRUDTest on " + objectTypeName);
        s_log.info("");
        s_log.info("");



    }

    private void initializeObject(DataObject data, DataObject associatedObject)  throws Exception {
        setDefaultProperties(data, associatedObject);
        s_log.debug("Created " + data.getObjectType().getQualifiedName() + " with OID: " + data.getOID());

        makeChildObjects(data);
        makeAssociations(data, associatedObject);
    }
    private void deleteObject(OID id)  throws Exception {

        // Manipulator for removing associations before delete
        PropertyManipulator.AssociationManipulator assocRemover = new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject data) throws Exception {
                    s_log.info("Found association: " + p.getName());
                    if( p.isCollection() ) {
                        DataAssociation assoc = (DataAssociation) data.get(p.getName());
                        DataAssociationCursor cursor = assoc.cursor();
                        while(cursor.next()) {
                            s_log.info("Removing from association: " + cursor.getDataObject().getObjectType().getName());
                            cursor.remove();
                            s_log.info("Removed!");
                        }

                    }

                }
            };

        DataObject data = SessionManager.getSession(). retrieve(id);
        s_log.info("");
        String objectName = data.getObjectType().getName();
        s_log.info("Deleting object: " + objectName + " with OID: " + data.getOID());

        PropertyManipulator.manipulateProperties(data, assocRemover);

        s_log.info("daving data!");
        data.save();
        s_log.info("about to delete!");
        data.delete();
        Assert.truth(data.isDeleted());
        data = SessionManager.getSession(). retrieve(id);
        Assert.truth( null == data );
        s_log.info("END Removing object: " + objectName);
        s_log.info("");


    }


    private void setDefaultProperties(DataObject data,
                                      DataObject associatedObject)
        throws Exception {

        final ObjectType type = data.getObjectType();
        s_log.info("");
        s_log.info("Making new object for: " + type.getQualifiedName());
        KeyGenerator.setKeyValues(data);

        ObjectType associated = (associatedObject == null)
            ? null: associatedObject.getObjectType();

        for (Iterator it = type.getKeyProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (prop.isAttribute()) { continue; }

            DataType propType = prop.getType();
            if (propType.equals(associated)) {
                data.set(prop.getName(), associatedObject);
            } else {
                makeChild(prop, data);
            }
        }

        PropertyManipulator.NonKeyManipulator manip =
            new PropertyManipulator.NonKeyManipulator(type) {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    m_manipulator.setDefaultProperty(p, dataInner);
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);
        s_log.info("END new object.");
        s_log.info("");
        s_log.info("");

    }

    private void setKeyProperties(DataObject data,
                                  final DataObject associatedObject)
        throws Exception {

        s_log.info("setting key properties");
        KeyGenerator.setKeyValues(data);

        final ObjectType type = data.getObjectType();
        PropertyManipulator.AttributeManipulator manip =
            new PropertyManipulator.AttributeManipulator() {
                public boolean obeys(Property p) {
                    return super.obeys(p) && type.isKeyProperty(p)
                        && p.getType().isCompound();
                }

                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    if( associatedObject != null
                        && p.getType().equals(associatedObject.getObjectType())) {

                        dataInner.set(p.getName(), associatedObject);
                    } else {
                        DataObject object =
                            SessionManager.getSession().create(p.getType().getQualifiedName());
                        reportPropertyTypes(object);
                        initializeObject(object, dataInner);
                    }
                }
            };
        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeAssociations(DataObject data,
                                  final DataObject associatedObject)
        throws Exception {

        PropertyManipulator.AssociationManipulator manip =
            new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {

                    DataType assocType = p.getType();
                    if( associatedObject == null
                        || !assocType.equals(associatedObject
                                             .getObjectType()) ) {
                        String msg = "Making association for:" +
                            dataInner.getObjectType().getName();
                        if( null == associatedObject ) {
                            msg += " Is not already associated with any object.";
                        } else {
                            msg += " Is not associated with: "
                                + associatedObject.getObjectType().getName();
                            msg += " The association type is: " + assocType.getName();
                        }
                        s_log.info(msg);
                        makeAssociation(p, dataInner);
                    }
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeAssociation(Property p, DataObject data)
        throws Exception {

        String fullTypeName = p.getType().getQualifiedName();
        s_log.info("Making associated object: " + fullTypeName +
                   " for ObjectType: " + data.getObjectType().getQualifiedName());

        DataObject associatedObject = SessionManager.getSession().create(fullTypeName);
        reportPropertyTypes(associatedObject);
        initializeObject(associatedObject, data);
        associatedObject.save();
        reportPropertyTypes(associatedObject);
        s_log.info("Getting association:  " + p.getName());
        if (p.isCollection()) {
            DataAssociation assoc = (DataAssociation) data.get(p.getName());
            assoc.add(associatedObject);
        } else {
            data.set(p.getName(), associatedObject);
        }
    }


    private void makeChildObjects(DataObject data)
        throws Exception {

        PropertyManipulator.ComponentManipulator manip =
            new PropertyManipulator.ComponentManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    makeChild(p, dataInner);
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeChild(Property p, final DataObject parent)
        throws Exception {

        final String fullTypeName = p.getType().getQualifiedName();
        s_log.info("Making child object: " + fullTypeName +
                   " for ObjectType: " + parent.getObjectType().getQualifiedName());

        DataObject child = SessionManager.getSession().create(fullTypeName);
        reportPropertyTypes(child);

        initializeObject(child, parent);
        PropertyManipulator.AssociationManipulator manip =
            new PropertyManipulator.AssociationManipulator() {
                public boolean obeys(Property pInner) {
                    final boolean isParentRef = super.obeys(pInner)
                        && !pInner.isCollection()
                        && pInner.getType().equals(parent.getObjectType());
                    return isParentRef;
                }
                public void manipulate(Property pInner, DataObject data)
                    throws Exception {
                    s_log.info("Setting parent role reference for: "
                               + fullTypeName + " Property: "
                               + pInner.getName());
                    data.set(pInner.getName(), parent);
                }

            };
        PropertyManipulator.manipulateProperties(child, manip);
        if (p.isCollection()) {
            DataAssociation children = (DataAssociation) parent.get(p.getName());
            children.add(child);

        }
        else {
            parent.set(p.getName(), child);
        }

    }


    private void checkUpdates(DataObject data, final DataObject parent)
        throws Exception {

        final ObjectType type = data.getObjectType();

        try {
            PropertyManipulator.NonKeyManipulator manip =
                new PropertyManipulator.NonKeyManipulator(type) {

                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {
                        m_manipulator.updateAllPropertyCombinations(
                                                                    p, dataInner);
                    }
                };

            PropertyManipulator.manipulateProperties(data, manip);

            PropertyManipulator.PredicateManipulator childManip =
                new PropertyManipulator.ComponentManipulator() {
                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {

                        if(p.isCollection()) {
                            DataAssociation children =
                                (DataAssociation) dataInner.get(p.getName());
                            DataAssociationCursor cursor = children.cursor();
                            while(cursor.next()) {
                                DataObject child = cursor.getDataObject();
                                s_log.debug("checkUpdates on child: "
                                            + child.getObjectType()
                                            .getQualifiedName());
                                checkUpdates(child, dataInner);
                            }
                        } else {
                            DataObject child =
                                (DataObject) dataInner.get(p.getName());
                            s_log.debug("checkUpdates on child: "
                                        + child.getObjectType()
                                        .getQualifiedName());
                            checkUpdates(child, dataInner);
                        }
                    }

                };

            PropertyManipulator.manipulateProperties(data, childManip);

            PropertyManipulator.AssociationManipulator assocManip =
                new PropertyManipulator.AssociationManipulator() {
                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {

                        if(p.isCollection()) {
                            DataAssociation associations =
                                (DataAssociation) dataInner.get(p.getName());
                            DataAssociationCursor cursor = associations.cursor();
                            while(cursor.next()) {
                                DataObject assoc = cursor.getDataObject();
                                if( !assoc.equals(parent) ) {
                                    s_log.debug("checkUpdates on assoc: "
                                                + assoc.getObjectType()
                                                .getQualifiedName());
                                    checkUpdates(assoc, dataInner);
                                }
                            }
                        } else {
                            DataObject assoc =
                                (DataObject) dataInner.get(p.getName());
                            if( null != assoc &&  !assoc.equals(parent) ) {
                                s_log.debug("checkUpdates on assoc: "
                                            + assoc.getObjectType()
                                            .getQualifiedName());
                                checkUpdates(assoc, dataInner);
                            }
                        }
                    }
                };

            PropertyManipulator.manipulateProperties(data, assocManip);
        } catch (UndefinedEventException e) {
            s_log.info("Update event undefined for type: "
                       + type.getQualifiedName()
                       + " Update tests cannot be performed.");
            s_log.info("UndefinedEventException: " + e.getMessage());
        }


    }

    private void checkDefaultValues(DataObject data) throws Exception {
        s_log.debug("Checking default values for : "
                    + data.getObjectType().getName());

        class DefaultValidator extends PropertyManipulator.NonKeyManipulator {
            public DefaultValidator(ObjectType type) {
                super(type);
            }

            public void manipulate(Property p, DataObject dataInner)
                throws Exception {
                s_log.debug("checking property: " + p.getName());
                Object currentVal = dataInner.get(p.getName());
                DataObjectManipulator.SimpleTypeManipulator manip =
                    m_manipulator.getManipulator(p);
                Object defaultVal = manip.getDefaultValue();
                manip.checkEquals( p.getName(), defaultVal, currentVal );
            }
        };

        DefaultValidator validator = new DefaultValidator(data.getObjectType());
        PropertyManipulator.manipulateProperties(data, validator);

        PropertyManipulator.AssociationManipulator assocValidator =
            new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception  {

                    s_log.debug("Checking for association: " + p.getName());
                    if (p.isCollection()) {
                        DataAssociation assoc =
                            (DataAssociation) dataInner.get(p.getName());
                        DataAssociationCursor cursor = assoc.cursor();
                        while(cursor.next()) {
                            DataObject role = cursor.getDataObject();
                            checkRole(role);
                        }

                    } else {
                        DataObject role =
                            (DataObject) dataInner.get(p.getName());
                        checkRole(role);
                    }

                }
                void checkRole(DataObject role) throws Exception {
                    reportPropertyTypes(role);
                    DefaultValidator roleValidator =
                        new DefaultValidator(role.getObjectType());
                    PropertyManipulator.manipulateProperties(
                                                             role, roleValidator);
                }
            };

        PropertyManipulator.manipulateProperties(data, assocValidator);
        s_log.debug("END Checking default values");
        s_log.debug("");

    }

    private void reportPropertyTypes(DataObject data) throws Exception {
        ObjectType type = data.getObjectType();
        Iterator keys = type.getProperties();
        s_log.info("Properties for type: " + type.getName());
        while(keys.hasNext()) {
            Property p = (Property) keys.next();
            if( p.isAttribute() ) {
                String msg = "Property " + p.getName() + " is attribute. Class is: " + p.getJavaClass();
                if( type.isKeyProperty(p) )  {
                    msg += " is key property.";
                }
                msg += " value is: " + data.get(p.getName());
                s_log.info(msg);

            }
            else {
                s_log.info("Property " + p.getName() + "  is component: " +
                           p.isComponent() + " is collection: " + p.isCollection() + " is role: " + p.isRole());
                s_log.info("ObjectType is is: " + p.getType().getName());

            }

        }

        s_log.info("END Properties for type: " + type.getName());
    }

    private void setDefaultProperty(Property p, DataObject data) throws Exception {
        // s_log.info("Property " + p.getName() + " class is: " + p.getJavaClass());
        m_manipulator.setDefaultProperty(p, data);

    }

    private void updateAllPropertyCombinations(Property p, DataObject data) throws Exception {
        m_manipulator.updateAllPropertyCombinations(p, data);
    }

    private static boolean isNonKeyAttribute(Property p, ObjectType type) {
        return p.isAttribute() && !type.isKeyProperty(p);
    }
}
