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

// For Id.
import java.math.BigDecimal;

// Persistence Support.
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

// Globalization
import com.arsdigita.globalization.Locale;

// Logging
import org.apache.log4j.Logger;

/**
 * Represents an object that corresponds to a package type.
 * A package instance can have its own content, presentation,
 * and parameters associated with it.
 *
 * @version $Revision: #14 $, $Date: 2004/08/16 $
 * @since ACS 5.0
 * @see PackageType
 */
public class PackageInstance extends ACSObject {
    public static final String versionId = "$Id: PackageInstance.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(PackageInstance.class.getName());
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.PackageInstance";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // the package type for this package instance
    // this will only be cached for the lifetime of the domain
    // object.
    private PackageType m_packageType;

    /**
     * Constructs a new PackageInstance with no values set.
     * Use <code>PackageType.createInstance</code> to create
     * a new instance, or <code>PackageInstance(OID oid)</code>
     * to retrieve an existing one.<p>
     * This is for use only by subclasses of PackageInstance.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see PackageType#createInstance
     * @see #PackageInstance(OID)
     *
     * @param objectType The object type which the instance will be created
     *                   as.
     */
    protected PackageInstance( String objectType ) {
        super( objectType );
    }

    /**
     * Constructs a new PackageInstance with no values set.
     * Use <code>PackageType.createInstance</code> to create
     * a new instance, or <code>PackageInstance(OID oid)</code>
     * to retrieve an existing one.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see PackageType#createInstance
     * @see #PackageInstance(OID)
     **/
    protected PackageInstance() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructs a PackageInstance domain object from a package instance
     * data object.
     *
     * @param dataObject a PackageInstance data object
     */
    public PackageInstance(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Retrieves the PackageInstance domain object with the specified ID.
     *
     * @param id the primary key of the package instance
     * @exception DataObjectNotFoundException if the id does not
     * correspond to an existing package instance.
     */
    public PackageInstance(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Retrieves the PackageInstance domain object with the specified OID.
     *
     * @param oid the objectID of the package instance
     * @exception DataObjectNotFoundException If the OID does not
     * correspond to an existing package instance.
     */
    public PackageInstance(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public PackageType getType() {
        // cache the package type for the lifetime of the package instance
        // object.
        if (m_packageType == null) {
            DataObject dobj = (DataObject)get("packageType");
            m_packageType = new PackageType(dobj);
            dobj.disconnect();
        }
        return m_packageType;
    }

    protected void setType(PackageType type) {
        setAssociation("packageType", type);
        m_packageType = type;
    }

    /**
     *
     * Returns the first site node on which this package instance is mounted.
     * Currently, the order is determined by the site node's ID property.
     * In the future, an explicit sort key may be introduced on the links
     * between a package instance and its mount points.  Returns null
     * if this package instance is not mounted.
     * @return the first site node on which this package instance is mounted,
     * or null if this package instance is not mounted.
     **/
    public SiteNode getDefaultMountPoint() {
        SiteNode defaultMountPoint = null;
        DataAssociationCursor nodes =
            ((DataAssociation) get("mountPoint")).cursor();
        nodes.addOrder("id");
        if (nodes.next()) {
            defaultMountPoint = new SiteNode(nodes.getDataObject());
            nodes.close();
        }
        return defaultMountPoint;
    }

    /**
     * @deprecated use getType() instead
     * @see com.arsdigita.kernel.PackageInstance#getType()
     */
    public PackageType getPackageType() {
        return getType();
    }

    /**
     * @deprecated
     */
    public String getPackageKey() {
        return getKey();
    }

    /**
     * Gets the name of this package.
     * @return the package key.
     */
    public String getKey() {
        return getType().getKey();
    }

    /**
     * Sets the package key.
     * @param key the key for this package
     */
    public void setKey(String key) {
        set("packageKey", key);
    }

    /**
     * Gets the package name.
     * @return the package's name.
     */
    public String getName() {
        return (String)get("prettyName");
    }

    /**
     * Returns a display name for this package instance.
     *
     * @see ACSObject#getDisplayName()
     */
    public String getDisplayName() {
        return getName();
    }

    public void setName(String prettyName) {
        set("prettyName", prettyName);
    }

    public String getParameter(String v) {
        return getParameter(v, null);
    }
    public String getParameter(String v, String y) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    public int getParameter(String v,  int x) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * <p>
     * The name of a resource bundle, suitable for passing as the first
     * parameter to ResourceBundle.getBundle(String, Locale).  Should
     * be a fully qualified Java resource name.
     * </p>
     *
     * <p>
     * TODO: For now we calculate the name based on the name of the
     * Dispatcher for this package, using a hardcoded algorithm.
     * Specifically, we replace "Dispatcher" with "Resources"
     * (the corresponding bundle name for "MyDispatcher" would be
     * "MyResources"). Eventually, we will do away with this algorithm
     * and replace it with another attribute of a Package.
     * </p>
     *
     * @return the name of the resource bundle we should try loading
     * for ResourceBundle.
     */
    public String getTargetBundle() {
        String dispatcherClass = getType().getDispatcherClass();
        if (dispatcherClass.endsWith("Dispatcher")) {
            return dispatcherClass.substring(
                                             0,
                                             dispatcherClass.length() - "Dispatcher".length()
                                             ) + "Resources";
        } else {
            String key = getKey();
            return "com.arsdigita." + key + "." + key + "Resources";
        }
    }

    /**
     * Gets all the site nodes where this instance is mounted.
     * @return all the site nodes where this instance is mounted.
     */
    public SiteNodeCollection getMountPoints() {
        DataAssociation mountAssociation = (DataAssociation)(get("mountPoint"));
        return new SiteNodeCollection(mountAssociation);
    }

    /**
     * <p>
     * Returns the locale that is associated with this PackageInstance.
     * </p>
     *
     * @return java.util.Locale.
     */
    public java.util.Locale getLocale() {
        java.util.Locale locale = null;
        DataObject localeDataObject = (DataObject) get("locale");

        if (localeDataObject != null) {
            Locale localeObject = new Locale(localeDataObject);

            if (localeObject != null) {
                locale = localeObject.toJavaLocale();
            }
        }

        return locale;
    }

    /**
     * <p>
     * Sets the locale to be associated with this PackageInstance.
     * </p>
     *
     * @param locale java.util.Locale
     */
    public void setLocale(java.util.Locale locale) {
        Locale localeObject = Locale.fromJavaLocaleBestMatch(locale);
        setLocale(localeObject);
    }

    /**
     * <p>
     * Set the locale to be associated with this PackageInstance.
     * </p>
     *
     * @param locale com.arsdigita.globalization.Locale
     */
    private void setLocale(Locale locale) {
        setAssociation("locale", locale);
    }

    protected ACSObject getContainer() {
        return null;
    }

    public void delete() {
        PackageEventListener pels[] = getType().getListeners();
        for (int i = 0; i < pels.length; i++) {
            PackageEventListener listener = pels[i];
            listener.onDelete(this);
        }
        super.delete();
    }
}
