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
package com.arsdigita.kernel;

import com.arsdigita.db.Sequences;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Represents a package type.
 *
 * @since ACS 5.0
 * @version $Revision: #15 $, $Date: 2004/08/16 $
 * @version $Id: PackageType.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated without direct replacement. Refactor to use
 *             {@link com.arsdigita.web.ApplicationType} instead.
 */

public class PackageType extends com.arsdigita.domain.DomainObject {

    /** The logging object for this class. */
    private static final Logger s_log =
        Logger.getLogger(PackageType.class.getName());

    // Used for caching dispatchers
    private static java.util.HashMap s_dispatchers = new java.util.HashMap();

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.PackageType";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "PackageType".
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationType} instead.
     */
    public PackageType() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName the name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>
     *
     * @see Party#Party(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationType} instead.
     */
    public PackageType(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Constructor in which th contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.ApplicationType} instead.
     */
    public PackageType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    protected void initialize() {
        super.initialize();
        if (isNew()) {
            if (get("id") == null) {
                setID(generateID());
            }
        }
    }

    protected void beforeSave() throws PersistenceException {
        super.beforeSave();
        if (isNew()) {
            if (getDispatcherClass() == null) {
                setDispatcherClass("com.arsdigita.dispatcher.JSPApplicationDispatcher");
            }
        }
    }

    /**
     * Gets the value of the ID property.
     *
     * @return the value of the ID property.
     */
    public BigDecimal getID() {
        return (BigDecimal) get("id");
    }

    /**
     * Sets the value of the ID property only if this is new.
     * Returns the value that the ID is set to.
     *
     * @param id the value to try to set the ID property to
     * @return the value that the ID property is set to.
     */
    private BigDecimal setID(BigDecimal id) {
        if (isNew()) {
            set("id", id);
            return id;
        } else {
            return getID();
        }
    }

    private BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "id.";
            s_log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    /**
     * Gets the key of the package type.
     *
     * @return the key of the package.  This will never be null.
     * @see #setKey(String)
     */
    public String getKey() {
        return (String)get("packageKey");
    }

    /**
     * Sets the key of the package type.  The key is an abbreviation of the
     * package name and is a unique String representation of it.
     * An example key for the "ACS Kernel" is "acs-kernel."
     * <P>
     * This method
     * can only be called when the package type is intially created.  After
     * it is saved, calling this method will cause an exception to be thrown.
     *
     * @param key the key of the package
     * @throws RuntimeException if setKey() is called after the package
     * type has been persisted by calling the save() method.
     * @see #getKey()
     */
    public void setKey(String key) {
        if (getKey() == null) {
            set("packageKey", key);
        } else if (!isNew()) {
            throw new RuntimeException
                ("setKey() cannot be called with a new key" +
                 "once the packageType is persisted.");
        }
    }

    public String getDisplayName() {
        return (String)get("prettyName");
    }

    /**
     * Sets the display name of the package type.
     * The value of this method is used by UIs to provide a human-readable
     * display for a package type.
     *
     * @param displayName the display name of the package type
     */
    public void setDisplayName(String displayName) {
        set("prettyName", displayName);
    }

    /**
     * Returns the plural version of the display name.
     *
     * @return the plural version of the display name.
     * @see #setDisplayPlural(String)
     */
    public String getDisplayPlural() {
        return (String)get("prettyPlural");
    }

    /**
     * Sets the displayPlural of the package type.
     * The value is used by UIs to provide a human-readable
     * display for a package type.
     *
     * @param displayPlural the plural form of the display name
     * @see #getDisplayPlural()
     */
    public void setDisplayPlural(String displayPlural) {
        set("prettyPlural", displayPlural);
    }

    /**
     * Gets the Unique Resource Identifier (URI) for the package.
     *
     * @return the Unique Resource Identifier for this package.
     * @see #setURI(String)
     */
    public String getURI() {
        return (String)get("packageURI");
    }

    /**
     * Sets a Unique Resource Identifier (URI) for the package.
     * The URI is used to provide a completely unique identification
     * for this package.  The URI typically identifies a company
     * name and the package key (for example, http://www.arsdigita.com/acs-java).
     *
     * @param URI a Unique Resource Identifier for the package
     * @see #getURI()
     */
    public void setURI(String URI) {
        set("packageURI", URI);
    }

    /**
     * Gets the name of the dispatcher class for this package
     * type.
     *
     * @return the name of this package type's dispatcher class.
     * @see #setDispatcherClass(String)
     * @see #getDispatcher()
     */
    public String getDispatcherClass() {
        return (String)get("dispatcherClass");
    }

    /**
     * Sets the dispatcher class for this package type.
     * When an instance of this package is mounted at a site node,
     * the dispatcher becomes active for the URL for that node.
     * The dispatcher must implement the
     * {@link com.arsdigita.dispatcher.Dispatcher} interface.
     *
     * @param dispatcherClass the string name of a dispatcher implementation
     * @see #getDispatcherClass()
     */
    public void setDispatcherClass(String dispatcherClass) {
        set("dispatcherClass", dispatcherClass);
    }

    /**
     * Gets an instance of the dispatcher registered for this class.
     * package.  The {@link #setDispatcherClass(String)} method is used
     * to set which dispatcher will be returned.
     *
     * @return a dispatcher for this ACS package.
     * @see #setDispatcherClass(String)
     * @see #getDispatcher()
     */
    public Dispatcher getDispatcher()
        throws ClassNotFoundException, InstantiationException,
               IllegalAccessException, InvocationTargetException
    {
        Object dis;
        synchronized(s_dispatchers) {
            dis = s_dispatchers.get(getOID());
            if ( dis == null ) {
                Class dispatcherClass = Class.forName(getDispatcherClass());
                try {
                    java.lang.reflect.Method method =
                        dispatcherClass.getMethod("newInstance",null);
                    dis = method.invoke(null, new Object[] {});
                } catch (NoSuchMethodException e) {
                    dis = dispatcherClass.newInstance();
                }
                s_dispatchers.put(getOID(), dis);
            }
        }
        // If the dispatcher is still null, we need to throw an exception.
        if (dis == null) {
            throw new InstantiationException
                ("Unable to instantiate dispatcher: " + getDispatcherClass() + ".");
        }
        return (Dispatcher) dis;
    }

    /**
     * This convenience method is used to create an instance of this
     * package type.  The package instance that is returned is persisted
     * so the {@link #save()} method only needs to be called if it is changed.
     *
     * @param displayName the display name for the package instance
     * @return a persisted package instance.
     */
    public PackageInstance createInstance(String displayName) {
        PackageInstance pkg = new PackageInstance();
        return initInstance( pkg, displayName );
    }


    /**
     * This convenience method is used to create an instance of this
     * package type.  The package instance that is returned is persisted
     * so the {@link #save()} method only needs to be called if it is changed.
     *
     * @param displayName the display name for the package instance
     * @return a persisted package instance.
     */
    public PackageInstance createInstance(Class instanceClass,
                                          String displayName) {
        PackageInstance pkg;
        try {
            Constructor constructor =
                instanceClass.getConstructor( new Class[]{} );
            pkg = (PackageInstance) constructor.newInstance( new Object[]{} );
        } catch( NoSuchMethodException ex ) {
            throw new UncheckedWrapperException( ex );
        } catch( InstantiationException ex ) {
            throw new UncheckedWrapperException( ex );
        } catch( IllegalAccessException ex ) {
            throw new UncheckedWrapperException( ex );
        } catch( InvocationTargetException ex ) {
            throw new UncheckedWrapperException( ex );
        }

        return initInstance( pkg, displayName );
    }

    private PackageInstance initInstance( PackageInstance pkg,
                                          String displayName ) {
        pkg.setName(displayName);
        pkg.setType(this);
        pkg.save();
        // Fire Event to Listeners
        PackageEventListener pels[] = getListeners();
        for (int i = 0; i < pels.length; i++) {
            PackageEventListener listener = pels[i];
            listener.onCreate(pkg);
        }
        return pkg;
    }


    /**
     * Adds a stylesheet for this package type.  When any component
     * of this package is rendered from XML, the stylesheet will be used.
     *
     * @param sheet a stylesheet used for rendering this package
     */
/*  public void addStylesheet(Stylesheet sheet) {
        sheet.addToAssociation((DataAssociation) get("defaultStyle"));
    }
*/
    /**
     * Gets all the stylesheets used for rendering this package.
     *
     * @param locale the locale being used for rendering the package
     * @param outputType an identification of output type, such as
     * "text/html"
     * @return an array of stylesheets for the specificed locale and
     * output type.
     * @see #addStylesheet(Stylesheet)
     *
     * @deprecated without direct replacement. It is designed to work with
     * {@link com.arsdigita.templating.LegacyStylesheetResolver} which is
     * replaced by {@link com.arsdigita.templating.PatternStylesheetResolver}.
     * So thes method is just not used anymore. (pboy)
     */
/*    public Stylesheet[] getStylesheets(Locale locale, String outputType) {
        return StyleAssociation
            .getStylesheets(get("defaultStyle"), locale, outputType);
    }
*/
    /**
     * Gets the first stylesheet (best match) associated with this package.
     * @param locale the locale being used for rendering the package
     * @param outputType an identification of output type, such as
     * "text/html"
     * @return the best match stylesheet for this package.
     * @see #addStylesheet(Stylesheet)
     *
     * @deprecated without direct replacement. It is designed to work with
     * {@link com.arsdigita.templating.LegacyStylesheetResolver} which is
     * replaced by {@link com.arsdigita.templating.PatternStylesheetResolver}.
     * So thes method is just not used anymore. (pboy)
     */
/*    public Stylesheet getStylesheet(Locale locale, String outputType) {
        return StyleAssociation
            .getStylesheet(get("defaultStyle"), locale, outputType);
    }
*/
    /**
     * Removes a stylesheet from the set of stylesheets used for rendering
     * this package.
     *
     * @param sheet the stylesheet to be removed
     */
/*  public void removeStylesheet(Stylesheet sheet) {
        sheet.removeFromAssociation((DataAssociation)get("defaultStyle"));
    }
*/
    /**
     * Adds a listener to the events for this package.
     *
     * @param listenerClass the name of a listener instance
     */
    public void addListener(String listenerClass) {
        // To add a listener, first see if it is already there.
        // If not, create a data object and add to the association.
        if (!hasListener(listenerClass)) {
            try {
                instantiateListener(listenerClass);
            } catch (InstantiationException e) {
                s_log.error("Unable to add listener because it cannot be "+
                            "instantiated.  Error: " + e.getMessage());
            }
            ((DataAssociation)get("listener")).add
                (createListener(listenerClass));
        }
    }

    /**
     * Removes a listener from the events for this package.
     *
     * @param listenerClass the name of the listener instance
     */
    public void removeListener(String listenerClass) {
        DataAssociationCursor cursor = getListenerCursor();
        cursor.addFilter(cursor.getFilterFactory().equals("listenerClass",
                                                          listenerClass));
        if (cursor.next()) {
            DataObject listener = cursor.getDataObject();
            ((DataAssociation)get("listener")).remove(listener);
        }

        cursor.close();
    }

    /**
     * Checks if this package has a listener of the specified class.
     *
     * @param listenerClass the name of a listener registered on this class
     * @return <code>true</code> if the specified listener is registered,
     * <code>false</code> otherwise.
     */
    public boolean hasListener(String listenerClass) {
        DataAssociationCursor cursor = getListenerCursor();
        while (cursor.next()) {
            if (((String)cursor.get("listenerClass")).equals(listenerClass)) {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    /**
     * A convenience method for creating a listener data object.
     *
     */
    private DataObject createListener(String listenerClass) {
        DataObject listener;
        // See if the listener exists.
        DataCollection dc = SessionManager.getSession().retrieve
            ("com.arsdigita.kernel.PackageListener");
        dc.addFilter(dc.getFilterFactory().equals("listenerClass",
                                                  listenerClass));
        if (dc.next()) {
            DataObject dataObject = dc.getDataObject();
            dc.close();
            return dataObject;
        } else {
            listener = SessionManager.getSession().create
                ("com.arsdigita.kernel.PackageListener");
            listener.set("id", generateID());
            listener.set("listenerClass", listenerClass);
            listener.save();
            return listener;
        }
    }

    /**
     * Finds all the package event listeners registered to this
     * class.
     *
     * @return the package event listeners.
     */
    protected PackageEventListener[] getListeners() {
        List list = new ArrayList();

        // cursor of listeners for this package.
        DataAssociationCursor cursor = getListenerCursor();
        while (cursor.next()) {
            try {
                list.add(instantiateListener
                         (((String)cursor.get("listenerClass"))));
            } catch (InstantiationException e) {
                // We couldn't instaniate one.
                s_log.error(e.getMessage());
            }
        }

        // create the array.
        PackageEventListener[] listeners =
            new PackageEventListener[list.size()];
        for (int i = 0; i < listeners.length; i++) {
            listeners[i] = ((PackageEventListener)list.get(i));
        }
        return listeners;
    }

    /**
     * @return A cursor of listeners for this package.
     */
    private DataAssociationCursor getListenerCursor() {
        return ((DataAssociation) get("listener")).cursor();
    }

    /**
     * Convenience method for instantiating a class from a String className.
     *
     * @param className a <code>String</code> value for a class name.
     */
    private PackageEventListener instantiateListener(String className)
        throws InstantiationException
    {
        try {
            PackageEventListener pel =
                (PackageEventListener)Class.forName(className).newInstance();
            return pel;
        } catch (IllegalAccessException e) {
            String errorMsg = "Unable to access " + className + ":" +
                e.getMessage();
            s_log.error(errorMsg);
            throw new InstantiationException(errorMsg);
        } catch (ClassNotFoundException e) {
            String errorMsg = "Unable to find " + className + ":" +
                e.getMessage();
            s_log.error(errorMsg);
            throw new InstantiationException(errorMsg);
        }
    }

    /**
     * Returns a collection of all the instances of
     * package type.
     *
     * @return all the instances of this package type.
     */
    public PackageInstanceCollection getInstances() {
        DataAssociation instanceAssociation =
            (DataAssociation)(get("packageInstance"));
        return new PackageInstanceCollection(instanceAssociation);
    }

    /**
     * Finds the package type of the specified key.
     *
     * @param key the package key for the package
     * @return the package type with the specified key.
     * @exception DataObjectNotFoundException if the key does not correspond
     * to an existing package type.
     * @see #setKey(String)
     * @see #getKey()
     */
    public static PackageType findByKey(String key)
        throws DataObjectNotFoundException
    {

        PackageType pt = getTypeByKey(key);

        if (pt == null) {
            throw new DataObjectNotFoundException
                ("No package type matching the key '" + key + "' was found.");
        }

        return pt;
    }

    public static boolean typeExists(String key) {
        return getTypeByKey(key) != null;
    }

    private static PackageType getTypeByKey(String key) {
        DataCollection dc = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        dc.addFilter(dc.getFilterFactory().equals("packageKey", key));
        PackageType pt = null;
        try {
            if (dc.next()) {
                pt = new PackageType(dc.getDataObject());
            }

        } finally {
            dc.close();
        }

        return pt;
    }
    /**
     * Creates a package type with the specified key, display name, display
     * plural, and URI.  This is a convenience method for having
     * to create an empty packageType followed by all of the set methods.
     *
     * The example below creates a package type for subsites.
     *
     * <pre>
     * PackageType subsite = PackageType.create
     *       ("acs-subsite", "ACS Subsite", "ACS Subsites",
     *        "http://arsdigita.com/acs-subsite/");
     * </pre>
     *
     * @param key the key for the package type
     * @param displayName the display name for the package type
     * @param displayPlural the plural form of the display name
     * @param URI the Unique Resource Identifier for this package
     * @return A persisted packageType.  The save() method is automatically
     * called.
     * @see #setKey(String)
     * @see #setDisplayName(String)
     * @see #setDisplayPlural(String)
     * @see #setURI(String)
     */
    public static PackageType create(String key,
                                     String displayName,
                                     String displayPlural,
                                     String URI) {
        PackageType pt = new PackageType();
        pt.setKey(key);
        pt.setDisplayName(displayName);
        pt.setDisplayPlural(displayPlural);
        pt.setURI(URI);
        pt.save();
        return pt;
    }

    /**
     * Returns a list of all available package keys.
     *
     * @return a list of all PackageKeys in the system.
     */
    public static List getAllPackageKeys() {
        List keyList = new ArrayList();
        DataCollection dc =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        while (dc.next()) {
            keyList.add((String)dc.get("packageKey"));
        }
        return keyList;
    }

    /**
     * Returns a collection of all defined package types.
     *
     * @return A PackageTypeCollection with all package types.
     */
    public static PackageTypeCollection retrieveAll() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        PackageTypeCollection packageTypeCollection = new PackageTypeCollection
            (dataCollection);

        return packageTypeCollection;
    }

    /**
     * Returns a list of all available package ID values.
     *
     * @deprecated
     * @see #getAllPackageKeys()
     */
    public static List getAllPackageIds() {
        List keyList = new ArrayList();
        DataCollection dc = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        while (dc.next()) {
            keyList.add((BigDecimal)dc.get("id"));
        }
        return keyList;
    }


    /**
     * @deprecated Use {@link #getKey()}
     */
    public String getPackageKey() {
        return getKey();
    }

    /**
     *
     * @return the package of Java servlets that are bundled with this
     * package.
     * @deprecated This information is no longer used.
     */
    public String getServletPackage() {
        return (String)get("servletPackage");
    }

    /**
     * @return the package of Java servlets that are bundled with this
     * package.
     * @deprecated This information is no longer used.
     */
    public void setServletPackage(String servletPackage) {
        set("servletPackage", servletPackage);
    }

    /**
     * @deprecated Use {@link #getDisplayName()}
     */
    public String getPrettyName() {
        return getDisplayName();
    }

    /**
     * @deprecated
     * @see #setDisplayName(String)
     */
    public void setPrettyName(String displayName) {
        setDisplayName(displayName);
    }

    /**
     * @deprecated
     * @see #getDisplayPlural()
     */
    public String getPrettyPlural() {
        return (String)get("prettyPlural");
    }

    /**
     * @deprecated
     * @see #setDisplayPlural(String)
     */
    public void setPrettyPlural(String prettyPlural) {
        set("prettyPlural", prettyPlural);
    }

    /**
     * Finds the package type of the specified key.
     * @deprecated
     * @see #findByKey(String)
     */
    public static PackageType findPackageTypeByKey(String key)
        throws DataObjectNotFoundException {
        return findByKey(key);
    }

    /**
     * Creates a package type and persists it to the database.
     * @deprecated
     * @see #create(String, String, String, String)
     */
    public static PackageType createPackageType(String key,
                                                String displayName,
                                                String displayPlural,
                                                String URI) {
        return create(key, displayName, displayPlural, URI);
    }

}
