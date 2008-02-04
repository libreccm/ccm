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
package com.arsdigita.portal;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Web;


/**
 * 
 *
 * @author Justin Ross
 */
public class PortletType extends ResourceType {

    private static final Logger s_cat =
        Logger.getLogger(PortletType.class.getName());

    // PortletType uses the data object type of ApplicationType.
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.portal.PortletType";


    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public static final String WIDE_PROFILE = "wide";
    public static final String NARROW_PROFILE = "narrow";

    protected PortletType(DataObject dataObject) {
        super(dataObject);
    }

    // Create from packageType.
    protected PortletType
        (String dataObjectType, String title, String profile,
         String portletObjectType) {
        super(dataObjectType);

        Assert.assertNotNull(title, "title");
        Assert.assertNotNull(profile, "profile");
        Assert.assertNotNull(portletObjectType, "portletObjectType");

        PackageType packageType = null;

        try {
            packageType = PackageType.findByKey("portal");
        } catch (DataObjectNotFoundException nfe) {
            String message =
                "The PackageType 'portal' is not installed.  It must be " +
                "installed in order to create a new PortletType.";

            s_cat.error(message);

            throw new IllegalStateException(message);
        }

        setTitle(title);
        setResourceObjectType(portletObjectType);

        // Defaults for portlets.
        setProfile(profile);
        setFullPageView(false);
        setEmbeddedView(true);
    }

    public static PortletType createPortletType
        (String title, String profile, String portletObjectType) {
        return new PortletType
            (BASE_DATA_OBJECT_TYPE, title, profile, portletObjectType);
    }

    public static PortletType retrievePortletType(BigDecimal id) {
        Assert.assertNotNull(id, "id");

        return PortletType.retrievePortletType
            (new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static PortletType retrievePortletType(OID oid) {
        Assert.assertNotNull(oid, "oid");

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        Assert.assertNotNull(dataObject);

        return PortletType.retrievePortletType(dataObject);
    }

    public static PortletType retrievePortletType(DataObject dataObject) {
        Assert.assertNotNull(dataObject, "dataObject");

        return new PortletType(dataObject);
    }

    public static PortletType retrievePortletTypeForPortlet
        (String portletObjectType) {
        Assert.assertNotNull(portletObjectType, "portletObjectType");

        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        Assert.assertNotNull(collection, "collection");

        collection.addEqualsFilter("objectType", portletObjectType);

        PortletType portletType = null;

        if (collection.next()) {
            portletType = PortletType.retrievePortletType
                (collection.getDataObject());
        } else {
            s_cat.warn("No portlet type found that matches \"" +
                       portletObjectType + ".\"  Check that the portlet " +
                       "type is registered in the system.");
        }

        collection.close();
        return portletType;
    }

    public static PortletTypeCollection retrieveAllPortletTypes() {
        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        Assert.assertNotNull(collection, "collection");
        collection.addEqualsFilter("hasEmbeddedView", Boolean.TRUE);

        return new PortletTypeCollection(collection);
    }

    //
    // Association properties
    //

    public void setProviderApplicationType(ApplicationType applicationType) {
        Assert.assertNotNull(applicationType, "applicationType");

        setAssociation("providerApplicationType", applicationType);
    }

    public void setProviderApplicationType(String applicationObjectType) {
        ApplicationType applicationType =
            ApplicationType.retrieveApplicationTypeForApplication
            (applicationObjectType);

        setProviderApplicationType(applicationType);
    }

    public ApplicationType getProviderApplicationType() {
        DataObject dobj = (DataObject) get("providerApplicationType");
        if (dobj == null) { return null; }
        return ApplicationType.retrieveApplicationType(dobj);
    }

    public boolean hasFullPageView() {
        Boolean hasFullPageView = (Boolean) get("hasFullPageView");

        Assert.assertNotNull(hasFullPageView, "hasFullPageView");

        return hasFullPageView.booleanValue();
    }

    protected void setFullPageView(boolean hasFullPageView) {
        set("hasFullPageView", new Boolean(hasFullPageView));
    }

    public boolean hasEmbeddedView() {
        Boolean hasEmbeddedView = (Boolean)get("hasEmbeddedView");

        Assert.assertNotNull(hasEmbeddedView, "hasEmbeddedView");

        return hasEmbeddedView.booleanValue();
    }

    protected void setEmbeddedView(boolean hasEmbeddedView) {
        set("hasEmbeddedView", new Boolean(hasEmbeddedView));
    }

    // Can return null.
    public String getProfile() {
        String profile = (String) get("profile");

        return profile;
    }

    // Param profile can be null.
    protected void setProfile(String profile) {
        set("profile", profile);
    }

    // SF patch [ 1181342 ] Allow portlets to be packaged up as applications
    private static Set s_xsl = new HashSet();

    /**
	 * 
	 * NB - Based on ccm-cms/com.arsdigita.cms.ContentType
	 * interface is liable to change.
	 * 
	 * Registers an XSL file against a portlet type. 
	 * @param portletType the portlet's base data object type
	 * @param path the path relative to the server root
	 */
	public static void registerXSLFile(String portletType, String path) {
		s_cat.debug("registering xsl file " + path + " to portlet type "
				+ portletType);

		s_xsl.add(new XSLEntry(portletType, path));
	}

	/**
	 * NB this interface is liable to change.
	 * 
	 * Unregisters an XSL file against a content type. 
	 * @param type the content type
	 * @param path the path relative to the server root
	 */
	public static void unregisterXSLFile(String portletType, String path) {
		s_xsl.remove(new XSLEntry(portletType, path));
	}

	/**
	 * Gets an iterator of java.net.URL objects for
	 * all registered XSL files
	 */
	public static Iterator getXSLFileURLs() {
		s_cat.debug("getXSLFileURLs - returningan iterator over "
				+ s_xsl.size() + " entries");
		return new EntryIterator(s_xsl.iterator());
	}

	private static class EntryIterator implements Iterator {
		private Iterator m_inner;

		public EntryIterator(Iterator inner) {
			m_inner = inner;
		}

		public boolean hasNext() {
			return m_inner.hasNext();
		}

		public Object next() {
			XSLEntry entry = (XSLEntry) m_inner.next();
			String path = entry.getPath();

			try {
				return new URL(Web.getConfig().getDefaultScheme(), Web
						.getConfig().getHost().getName(), Web.getConfig()
						.getHost().getPort(), path);
			} catch (MalformedURLException ex) {
				throw new UncheckedWrapperException("path malformed" + path, ex);
			}
		}

		public void remove() {
			m_inner.remove();
		}
	}

	private static class XSLEntry {
		private String m_type;

		private String m_path;

		public XSLEntry(String portletType, String path) {
			m_type = portletType;
			m_path = path;
		}

		public PortletType getType() {
			return PortletType.retrievePortletTypeForPortlet(m_type);
		}

		public String getPath() {
			return m_path;
		}

		public boolean equals(Object o) {
			if (!(o instanceof XSLEntry)) {
				return false;
			}
			XSLEntry e = (XSLEntry) o;
			return m_path.equals(e.m_path) && m_type.equals(e.m_type);
		}

		public int hashCode() {
			return m_path.hashCode() + m_type.hashCode();
		}
	}

}
