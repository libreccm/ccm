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
import com.arsdigita.persistence.DataObserver;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.persistence.metadata.ObjectType;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;


/**
 * This is the base class that all other persistent classes would
 * extend. It provides methods that delegate to a contained
 * DataObject.
 *
 * @version 1.0
 *
 * @see com.arsdigita.persistence.DataObject
 **/
public abstract class DomainObject {

    public static final String versionId = "$Id: DomainObject.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(DomainObject.class);

    private final DataObject m_dataObject;
    private boolean m_initialized = false;

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName The name of the <code>ObjectType</code> of the
     * new instance.
     *
     * @see com.arsdigita.persistence.Session#create(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public DomainObject(String typeName) {
        Session s = SessionManager.getSession();
        if (s == null) {
            throw new RuntimeException("Could not retrieve a session from " +
                                       "the session manager while instantiating " +
                                       "a class with ObjectType = " + typeName);
        }

        m_dataObject = s.create(typeName);
        initialize();
        postInitialization();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type The <code>ObjectType</code> of the new instance.
     *
     * @see com.arsdigita.persistence.Session#create(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public DomainObject(ObjectType type) {
        Session s = SessionManager.getSession();
        if (s == null) {
            throw new RuntimeException("Could not retrieve a session from " +
                                       "the session manager while instantiating " +
                                       "a class with ObjectType = " +
                                       type.getName());
        }

        m_dataObject = s.create(type);
        initialize();
        postInitialization();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an OID specified by
     * <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @exception DataObjectNotFoundException Thrown if we cannot
     * retrieve a data object for the specified OID
     *
     * @see com.arsdigita.persistence.Session#retrieve(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public DomainObject(OID oid) throws DataObjectNotFoundException {
        Session s = SessionManager.getSession();
        if (s == null) {
            throw new RuntimeException("Could not retrieve a session from " +
                                       "the session manager while instantiating " +
                                       "a class with OID = " + oid.toString());
        }

        m_dataObject = s.retrieve(oid);
        if (m_dataObject == null) {
            throw new DataObjectNotFoundException
                ("Could not retrieve a DataObject with " +
                 "OID = " + oid.toString());
        }
        initialize();
        postInitialization();
    }

    /**
     * Constructor. Creates a new DomainObject instance to encapsulate a given
     * data object.
     *
     * @param dataObject The data object to encapsulate in the new domain
     * object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     **/
    public DomainObject(DataObject dataObject) {
        m_dataObject = dataObject;
        initialize();
        postInitialization();
    }

    /**
     * Returns the base data object type for this domain object class.
     * Intended to be overrided by subclasses whenever the subclass will
     * only work if their primary data object is of a certain base type.
     *
     * @return The fully qualified name ("modelName.typeName") of the base
     * data object type for this domain object class,
     * or null if there is no restriction on the data object type for
     * the primary data object encapsulated by this class.
     **/
    protected String getBaseDataObjectType() {
        return null;
    }

    /**
     * Called from all of the <code>DomainObject</code> constructors
     * to initalize or validate the new domain object or its
     * encapsulated data object.  This was introduced in order to
     * support efficient validation of the encapsualted data object's
     * type.  If the validation is typically performed in class
     * constructors, then redundant validation is performed in
     * superclass constructors.  This validation now occurs here.
     **/
    protected void initialize() {
        m_initialized = true;

        if (m_dataObject == null) {
            throw new RuntimeException
                ("Cannot create a DomainObject with a null data object");
        }

        String baseTypeName = getBaseDataObjectType();
        if (baseTypeName == null) {
            return;
        }
        // ensure data object is instance of baseTypeName or a subtype thereof.
        ObjectType.verifySubtype(baseTypeName, m_dataObject.getObjectType());
    }

    private void postInitialization() {
        if (!m_initialized) {
            StringWriter sw = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(sw));
            s_log.fatal(
                        "Some subclass of DomainObject has overriden " +
                        "initialize and failed to call super.initialize() " +
                        "this is a potentially fatal error because " +
                        "DomainObject.initialize() runs code that is necessary " +
                        "for any Domain Object to function properly. Note that " +
                        "it could be any class in the type hierarchy that is " +
                        "responsible for this problem. In order to help you " +
                        "debug this, the stack trace will be logged directly " +
                        "following this message."
                        );
            s_log.fatal(sw.toString());
        }

        m_dataObject.addObserver(this.new SaveObserver());
    }

    /**
     * Returns the type of this persistent object.
     *
     * @return The type of this persistent object.
     **/
    public ObjectType getObjectType() {
        return m_dataObject.getObjectType();
    }

    /**
     * Return true if this persistent object is newly created, false
     * otherwise.
     *
     * @return True if this persistent object is newly created, false
     * otherwise.
     *
     * @see com.arsdigita.persistence.DataObject#isNew()
     **/
    public boolean isNew() {
        return m_dataObject.isNew();
    }

    /**
     * Return the OID of this domain object.
     *
     * @return the OID of this domain object.
     *
     * @see com.arsdigita.persistence.DataObject#getOID()
     **/
    public OID getOID() {
        return m_dataObject.getOID();
    }

    /**
     *  Returns true if this persistent object is in a valid state.
     *  An invalid DomainObject usually results from using a data object that
     *  was retrieved during a transaction that has been rolled back.
     *
     * @return true if the object has been modified; false otherwise
     * @see com.arsdigita.persistence.DataObject#isValid()
     **/
    public boolean isValid() {
        return m_dataObject.isValid();
    }

    /**
     * Returns true if this persistent object has been modified since
     * it was retrieved.
     *
     * @return True if this persistent object has been modified, false
     * otherwise.
     *
     * @see com.arsdigita.persistence.DataObject#isModified()
     **/
    public boolean isModified() {
        return m_dataObject.isModified();
    }

    /**
     * Returns true if this persistent object has been deleted from
     * the database.  This does a database hit to check.
     *
     * @return True if the object has been deleted
     **/
    public boolean isDeleted() {
        return m_dataObject.isDeleted();
    }


    /**
     * Returns true if the property specified by <i>name</i> has
     * been modified since this persistent object was retrieved.
     *
     * @param name Name of the property on which to check modification status.
     *
     * @return True if the property specified by <i>name</i> has been
     * modified since this persistent object was retrieved, false otherwise.
     *
     * @see com.arsdigita.persistence.DataObject#isPropertyModified(String)
     **/
    public boolean isPropertyModified(String name) {
        return m_dataObject.isPropertyModified(name);
    }

    /**
     * Persists any changes made to this object. Note that a data
     * object can be saved without a call to its corresponding domain
     * object's save() method. This means that save() is not
     * guaranteed to be called when saves are cascaded due to
     * associations. For instance, suppose a Folder contains numerous
     * File objects. Adding Files to that Folder and calling
     * Folder.save() will implicitly save all the files without
     * calling the File's DomainObject.save() method.
     *
     * Do not override the save() method under any circumstances. Use
     * beforeSave and afterSave instead. This method is not declared
     * final for backwards compatibility.
     *
     * @see com.arsdigita.persistence.DataObject#save()
     * @see #beforeSave()
     * @see #afterSave()
     **/
    public final void save() throws PersistenceException {
        m_dataObject.save();
    }

    /**
     * Deletes this object.
     *
     * @see com.arsdigita.persistence.DataObject#delete()
     **/
    public void delete() throws PersistenceException {
        m_dataObject.delete();
    }


    /**
     * Set a property of the DomainObjects DataObject.
     * This should only be used inside of a setXXX
     * method.
     *
     * @see com.arsdigita.persistence.DataObject#set(String, Object)
     **/
    protected void set(String attr, Object value) {
        if (value instanceof DomainObject) {
            value = ((DomainObject) value).m_dataObject;
        }

        m_dataObject.set(attr, value);
    }

    /**
     * Set an association DomainObjects DataObject.
     * This should only be used inside of a setXXX
     * method.
     * <p>
     * Specificall, this method should only be used to set
     * associations whose multiplicity is 0..1 or 1..1.
     * If the upper bound of the multiplicity is greater than 1
     * then the {@link #add(String, DataObject)} method should be used.
     *
     * @see com.arsdigita.persistence.DataObject#set(String, Object)
     **/
    protected void setAssociation(String attr, DomainObject dobj) {
        set(attr, dobj == null ? null : dobj.m_dataObject);
    }

    protected static void setAssociation(DataObject dobj, String attr,
                                         DomainObject target) {
        dobj.set(attr, target == null ? null : target.m_dataObject);
    }

    /**
     * Remove the DomainObject from a DataAssociation
     *
     * @see com.arsdigita.persistence.DataAssociation#remove(DataObject)
     **/
    public void removeFromAssociation(DataAssociation da) {
        da.remove(m_dataObject);
    }

    /**
     * Add the DomainObject to a DataAssociation
     *
     * @see com.arsdigita.persistence.DataAssociation#add(DataObject)
     **/
    public DataObject addToAssociation(DataAssociation da) {
        return da.add(m_dataObject);
    }

    /**
     * Adds to associations with multiplicty > 1.  If the multiplicity
     * equals 1 then {@link #setAssociation(String, DomainObject)}
     * should be used instead.
     */
    protected DataObject add(String propertyName, DataObject dataObject) {
        DataAssociation da = (DataAssociation)this.get(propertyName);
        return da.add(dataObject);
    }
    /**
     * Adds to associations with multiplicty > 1.
     */
    protected DataObject add(String propertyName, DomainObject dobj) {
        return add(propertyName, dobj.m_dataObject);
    }

    /**
     * Removes from associations with multiplicity > 1.
     */
    protected void remove(String propertyName, DataObject dataObject) {
        DataAssociation da = (DataAssociation)this.get(propertyName);
        da.remove(dataObject);
    }

    /**
     * Removes from associations with multiplicity > 1.
     */
    protected void remove(String propertyName, DomainObject dobj) {
        remove(propertyName, dobj.m_dataObject);
    }

    /**
     * Removes.
     */
    protected void remove(String propertyName, OID oid) {
        DataAssociation da = (DataAssociation)this.get(propertyName);
        da.remove(oid);
    }

    /**
     * Clears an association with multiplicity > 1.
     */
    protected void clear(String propertyName) {
        DataAssociation da = (DataAssociation)this.get(propertyName);
        da.clear();
    }

    /**
     * Get a property of the DomainObjects DataObject.
     * This should only be used inside of a getXXX method.
     *
     * @see com.arsdigita.persistence.DataObject#get(String)
     **/
    protected Object get(String attr) {
        return m_dataObject.get(attr);
    }

    public Session getSession() {
        return m_dataObject.getSession();
    }

    /**
     * Is this domain object equal to another domain object?
     * Default implementation only compares OID (provided
     * it isn't empty).
     *
     * @param category A category
     * @return true if the two objects have the same OID,
     *  unless no OID info exists in which case true if they
     *  are at the same memory location (the default .equals).
     */
    public boolean equals(Object object) {
        if (object instanceof DomainObject) {
            return getDataObject().equals(((DomainObject)object).getDataObject());
        }
        return false;
    }

    /**
     * We override the standard hashCode method because
     * we have overridden equals.
     *
     * This delegates to OID.hashCode, unless the OID is
     * empty, in which case we delegate to super.hashCode
     * and end up with something based on this object's
     * location in memory.
     */
    public int hashCode() {
        return getDataObject().hashCode();
    }

    /**
     * Get the data object encapsulated by this domain object.
     * For use by DomainService class only.
     */
    DataObject getDataObject() {
        return m_dataObject;
    }

    /**
     * Specializes the encapsulated data object by turning it into a
     * subtype of this object's current type.
     *
     * @param subtype The subtype to which to specialize.
     *
     * @pre subType.isASuperType(getObjectType()) || subtype.equals(getObjectType())
     *
     * @post subtype.equals(getObjectType())
     *
     * @see com.arsdigita.persistence.DataObject#specialize(ObjectType)
     **/
    protected void specializeDataObject(ObjectType subtype) {
        m_dataObject.specialize(subtype);
    }

    /**
     * Specializes the encapsulated data object by turning it into a subtype
     * of this object's current type. In addition to the local precondition,
     * also has pre and post conditions of specializeDataObject(ObjectType).
     *
     * @param subtypeName The fully qualified name (e.g.
     * "com.arsdigita.kernel.User") of the subtype to which to specialize
     *
     * @pre SessionManager.getMetadataRoot().getObjectType(subtypeName) != null
     *
     * @see com.arsdigita.persistence.DataObject#specialize(String)
     **/
    protected void specializeDataObject(String subtypeName) {
        m_dataObject.specialize(subtypeName);
    }

    /**
     * Return the <code>OID</code> plus the flags <tt>N,M,D,U</tt> depending on
     * whether the object is new, modified, deleted, or unknown. Unknown is for
     * objects that have been invalidated.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        OID oid = getOID();
        if ( oid != null ) {
            result.append(oid.toString());
        } else {
            result.append("[");
            result.append(getBaseDataObjectType());
            result.append(":---]");
        }
        if (!isValid()) {
            result.append('U');
            return result.toString();
        }

        if ( isNew() ) result.append('N');
        if ( isModified() ) result.append('M');
        if ( isDeleted() ) result.append('D');

        return result.toString();
    }

    /**
     * Disconnects the encapsulated data object from the current
     * transaction. This allows the object to be used in multiple
     * transactions.
     *
     * @see #isDisconnected()
     **/
    public void disconnect() {
        m_dataObject.disconnect();
    }

    /**
     * Returns true if the encapsulated data object has been disconnected from
     * the transaction context. If true, the object can still be read, but any
     * attempt to update any of the object's attributes will cause an
     * exception to be thrown.
     *
     * @return true if the object has been disconnected
     **/
    public boolean isDisconnected() {
        return m_dataObject.isDisconnected();
    }

    /*
     * Called from a DataObserver, the beforeSave() method is
     * guaranteed to be executed before a save() and is always called
     * (unlike save()).
     *
     * @see #save()
     */
    protected void beforeSave() {}

    /*
     * Called from a DataObserver, the afterSave() method is
     * guaranteed to be executed after a save() and is always called
     * (unlike save()).
     *
     * @see #save()
     */
    protected void afterSave() {}

    protected void beforeDelete() {}

    protected void afterDelete() {}

    private class SaveObserver extends DataObserver {

        private DomainObject getDomainObject() { return DomainObject.this; }

        public void set(DataObject object, String property, Object previous,
                        Object value) { }

        public void add(DataObject object, String property,
                        DataObject value) { }

        public void remove(DataObject object, String property,
                           DataObject value) { }

        public void clear(DataObject object, String property) { }

        public void beforeSave(DataObject object) {
            getDomainObject().beforeSave();
        }

        public void afterSave(DataObject object) {
            getDomainObject().afterSave();
        }

        public void beforeDelete(DataObject object) {
            getDomainObject().beforeDelete();
        }

        public void afterDelete(DataObject object) {
            getDomainObject().afterDelete();
        }

        public int hashCode() {
            return getDomainObject().hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof SaveObserver) {
                return getDomainObject().equals(
                    ((SaveObserver) other).getDomainObject());
            }

            return false;
        }

        public boolean overrides(DataObserver o) {
            ObjectType me = getSession().getMetadataRoot().
                getObjectType(getDomainObject().getBaseDataObjectType());
            String other = ((SaveObserver) o).getDomainObject().
                getBaseDataObjectType();

            if (me.isSubtypeOf(other)) {
                return true;
            }

            return false;
        }

        public String toString() {
            return "Save observer for: " + getDomainObject().getOID() + " (" +
                super.toString() + ")";
        }
    }
}
