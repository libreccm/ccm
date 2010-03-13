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
package com.arsdigita.web;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * <p>A base class for defining a web application.  An application has
 * three important aspects:</p>
 *
 * <ol>
 *   <li><em>Each is a data partition.</em> An application is like a
 *   folder: it contains a user-driven subset of the objects in the
 *   system.  For instance, the travel forum has its own posts and the
 *   cooking forum has its own posts, even though both forums are the
 *   same type.</li>
 *
 *   <li><em>Each has its own configuration.</em> An application may
 *   be independently configured to change its behavior.</li>
 *
 *   <li><em>Each corresponds to a top-level UI.</em> An application
 *   has an associated self-contained UI, a servlet.  Generally, this
 *   UI will work off of the application's content (from the data
 *   partition) and configuration.  The main dispatcher knows
 *   about applications and dispatches to their servlets
 *   directly.</li>
 * </ol>
 *
 * @author Jim Parsons
 * @author Justin Ross
 * @version $Id: Application.java 1520 2007-03-22 13:36:04Z chrisgilbert23 $
 */
public class Application extends Resource {
    public static final String versionId =
		"$Id: Application.java 1520 2007-03-22 13:36:04Z chrisgilbert23 $"
			+ "$Author: chrisgilbert23 $"
			+ "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Application.class);
    public static final String PRIMARY_URL = "primaryURL";
    private static final String SLASH = "/";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.web.Application";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected Application(DataObject dataObject) {
        super(dataObject);
    }

    protected Application(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * create application without parent, and without an associated
     * container group
     */
    public static Application createRootApplication(final ApplicationType type,
                                                    final String title) {
	return Application.createRootApplication(type, title, false);
    }

    public static Application createRootApplication(final ApplicationType type,
						    final String title,
						    final boolean createContainerGroup) {
        if (Assert.isEnabled()) {
            Assert.exists(type, ApplicationType.class);
            Assert.exists(title, String.class);
            Assert.isTrue(type.m_legacyFree);
        }

	return Application.make(type, null, title, null, createContainerGroup);
    }

    public static Application createApplication(final ApplicationType type,
                                                final String fragment,
                                                final String title,
                                                final Application parent) {
	s_log.debug("Create Application");
	return Application.createApplication(type,fragment,title,parent,false);
    }

    public static Application createApplication(final ApplicationType type,
						final String fragment,
						final String title,
						final Application parent,
						final boolean createContainerGroup) {
        if (Assert.isEnabled()) {
            Assert.exists(type, ApplicationType.class);
            Assert.exists(fragment, String.class);
            Assert.exists(title, String.class);
            Assert.isTrue(!fragment.equals(""),
                         "The URL fragment must not be the empty string");
        }

        if (type.m_legacyFree) {
	    return Application.make(type,fragment,title,parent,createContainerGroup);
        } else {
	    s_log.debug("Creating legacy compatible app");
	    return Application.legacyMake(type,fragment,title,parent,createContainerGroup);
        }
    }

    // For convenience.
    public static Application createApplication(final String typeName,
                                                final String fragment,
                                                final String title,
                                                final Application parent) {

	return Application.createApplication(typeName,fragment,title,parent,false);
    }

    public static Application createApplication(final String typeName,
						final String fragment,
						final String title,
						final Application parent,
						final boolean createContainerGroup) {
	final ApplicationType type =
			ApplicationType.retrieveApplicationTypeForApplication(typeName);
        if (type == null) {
            throw new IllegalArgumentException("No ApplicationType found for type name " + typeName);
        }
	return Application.createApplication(type,fragment,title,parent,createContainerGroup);
    }

    private static Application make(final ApplicationType type,
                                    final String fragment,
                                    final String title,
				    final Application parent,
				    final boolean createContainerGroup) {
	final Application app =	(Application) Resource.createResource(type, title, parent);
	if (createContainerGroup) {
	    app.createGroup();
	}
        if (Assert.isEnabled() && fragment != null) {
            Assert.isTrue(fragment.indexOf('/') == -1,
                         "The URL fragment must not contain " +
                         "slashes; I got '" + fragment + "'");
        }

        if (parent == null) {
            if (fragment == null) {
                app.setPath("");
            } else {
                app.setPath(SLASH + fragment);
            }
        } else {
            app.setPath(parent.getPath() + SLASH + fragment);
        }

        return app;
    }

    // Actually does the work.
    private static Application legacyMake(final ApplicationType type,
                                          final String fragment,
                                          final String title,
					  final Application parent,
					  final boolean createContainerGroup) {
	final Application application =	(Application) Resource.createResource(type, title, parent);
	if (createContainerGroup) {
	    s_log.debug("Creating Group for application");
	    application.createGroup();
	}
        final DataObject dataObject =
            DomainServiceInterfaceExposer.getDataObject(application);

        final SiteNode[] siteNode = { null };

        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());

                PackageInstance packageInstance =
                    type.getPackageType().createInstance
                        (type.getTitle());
                // createInstance shouldn't do a save, but it
                // does. if we fix this at some point, we'll
                // need this call:
                //    packageInstance.save();

		dataObject.set("packageInstance",
                     DomainServiceInterfaceExposer.getDataObject
                         (packageInstance));

                if (fragment != null) {
                    siteNode[0] = makeSiteNode(fragment, parent);
                    siteNode[0].mountPackage(packageInstance);
                    siteNode[0].save();
                }
            }
        }.run();

        if (siteNode[0] != null) {
            application.setPrimaryURL(siteNode[0].getURL());
        }

        return application;
    }

    public static Application retrieveApplication(BigDecimal id) {
        OID oid = new OID(BASE_DATA_OBJECT_TYPE, id);

        return Application.retrieveApplication(oid);
    }

    public static Application retrieveApplication(OID oid) {
        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        if (dataObject == null) {
            return null;
        }

        return Application.retrieveApplication(dataObject);
    }

    public static Application retrieveApplication(DataObject dobj) {
        Assert.exists(dobj, DataObject.class);

        ACSObject obj = (ACSObject) DomainObjectFactory.newInstance(dobj);

        if (obj instanceof Application) {
            return (Application) obj;
        } else {
            return getContainingApplication(obj);
        }
    }

    public static final Application getContainingApplication(ACSObject obj) {
        Assert.exists(obj, ACSObject.class);
        ACSObject result = obj.gimmeContainer();

        while (result != null &&
               !(result instanceof Application)) {
            result = result.gimmeContainer();
        }

        return (Application) result;
    }

    // Can return null.
    public static Application retrieveApplicationForSiteNode
        (SiteNode siteNode) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.web.applicationForSiteNodeID");

        query.setParameter("siteNodeID", siteNode.getID());

        Application application = null;

        if (query.next()) {
            DataObject dataObject = (DataObject) query.get("application");
            application = Application.retrieveApplication(dataObject);
        }

        query.close();

        return application;
    }

    // Can return null.
    public static Application retrieveApplicationForPath(String path) {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        dataCollection.addEqualsFilter(PRIMARY_URL, path);

        if (dataCollection.next()) {
            DataObject dataObject = dataCollection.getDataObject();
            dataCollection.close();
            return Application.retrieveApplication(dataObject);
        } else {
            return null;
        }
    }

    //
    // Association properties
    //

    // Cannot return null.
    public ApplicationType getApplicationType() {
        DataObject dataObject = (DataObject) get("resourceType");

        Assert.exists(dataObject, DataObject.class);

        dataObject.specialize(ApplicationType.BASE_DATA_OBJECT_TYPE);

        return new ApplicationType(dataObject);
    }

    protected void setApplicationType(ApplicationType applicationType) {
        Assert.exists(applicationType, Application.class);

        setAssociation("resourceType", applicationType);
    }

    // COMPAT XXX
    public PackageType getPackageType() {
        return getApplicationType().getPackageType();
    }

    // Can return null.
    public Application getParentApplication() {
        return (Application) getParentResource();
    }

    // Ordered from most distant to closest ancestor.
    public List getAncestorApplications() {
        // This is the stupid implementation.

        ArrayList list = new ArrayList();
        Resource ancestor = getParentResource();

        while (true) {
            if (ancestor == null) {
                break;
            }

            // Skip other types of resources.
            if (ancestor instanceof Application) {
                list.add(0, ancestor);
            }

            ancestor = ancestor.getParentResource();
        }

        return list;
    }

    // Param application can be null.
    public void setParentApplication(Application application) {
        setParentResource(application);
    }

    // Cannot return null.
    public ApplicationCollection getChildApplications() {
        ApplicationCollection children = retrieveAllApplications();
        children.addEqualsFilter("parentResource.id", getID());
        return children;
    }

    public ApplicationCollection getChildApplicationsForType
        (String applicationType) {
        ApplicationCollection children = getChildApplications();
        children.addEqualsFilter("objectType", applicationType);
        return children;
    }

    private PackageInstance getPackageInstance() {
        DataObject dataObject = (DataObject) get("packageInstance");

        Assert.exists(dataObject, DataObject.class);

        return new PackageInstance(dataObject);
    }

    private void setPackageInstance(PackageInstance packageInstance) {
        Assert.exists(packageInstance, PackageInstance.class);

        setAssociation("packageInstance", packageInstance);
    }

    // Can return null.  XXX Needs to be getSiteNodes instead.
    public SiteNode getSiteNode() {
        DataObject packageInstance = (DataObject)get("packageInstance");

        DataAssociation siteNodes = (DataAssociation)packageInstance.get
            ("mountPoint");
        DataAssociationCursor siteNodesCursor = siteNodes.cursor();

        DataObject siteNode = null;

        if (siteNodesCursor.next()) {
            siteNode = siteNodesCursor.getDataObject();
        }

        siteNodesCursor.close();

        if (siteNode == null) {
            return null;
        } else {
            return new SiteNode(siteNode);
        }
    }

    // Can return null.
    /**
     * @deprecated Use {@link
     * com.arsdigita.web.WebContext#getApplication()} instead.
     */
    public static Application getCurrentApplication(HttpServletRequest req) {
        Resource result = Kernel.getContext().getResource();

        if (result instanceof Application) {
            return (Application) result;
        } else {
            return null;
        }
    }

    //
    // Member properties
    //

    /**
     * Returns the path to this application through the dispatcher.
     * This path ends in a slash. Returns <code>null</code> if the
     * application has no path.
     *
     * @deprecated Use {@link #getPath()} instead.
     */
    public String getPrimaryURL() {
        final String path = (String) get(PRIMARY_URL);

        if (path == null) {
            return null;
        } else if (path.endsWith(SLASH)) {
            return path;
        } else {
            return path + SLASH;
        }
    }

    /**
     * Returns the path to this application through the dispatcher.
     * The path does not end in a slash.  This method will not return
     * null.
     */
    public final String getPath() {
        final String path = (String) get(PRIMARY_URL);

        Assert.exists(path, String.class);

        if (path.endsWith(SLASH)) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

    // XXX primary URL doesn't keep in sync with sitenode hierarchY
    // We need to use a trigger-like mechanism to keep the primaryURL
    // denormalization correct.
    /**
     * @deprecated Use {@link #setPath(String)} instead
     */
    private void setPrimaryURL(String primaryURL) {
        set(PRIMARY_URL, primaryURL);
    }

    /**
     * Sets the dispatcher path of this application.  The
     */
    public final void setPath(String path) {
        if (Assert.isEnabled()) {
            Assert.exists(path, String.class);
            Assert.isTrue
                (path.equals("") || (path.startsWith(SLASH)
                                     && !path.endsWith(SLASH)),
                 "The path must either be the empty string (for the " +
                 "default application) or it must start with '/' and *not* " +
                 "end in '/'; I got '" + path + "'");
        }

        set(PRIMARY_URL, path);
    }

    public Collection getRelevantPrivileges() {
        return getApplicationType().getRelevantPrivileges();
    }

    public static ApplicationCollection retrieveAllApplications() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        dataCollection.addEqualsFilter
            ("resourceType.hasFullPageView", Boolean.TRUE);
             
        ApplicationCollection apps = new ApplicationCollection
            (dataCollection);

        return apps;
    }

    public static boolean isInstalled
        (String applicationObjectType, String path) {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(applicationObjectType);

        dataCollection.addEqualsFilter(PRIMARY_URL, path);

        if (dataCollection.next()) {
            dataCollection.close();
            return true;
        } else {
            return false;
        }
    }

    //
    // To support ACSObject services
    //
    private static SiteNode makeSiteNode(String urlName, Application parent) {
        SiteNode siteNode;

        if (parent == null) {
            siteNode = SiteNode.createSiteNode(urlName);
        } else {
            SiteNode parentSiteNode = parent.getSiteNode();

            Assert.exists(parentSiteNode, Application.class);

            siteNode = SiteNode.createSiteNode(urlName, parentSiteNode);
        }

        Assert.exists(siteNode, SiteNode.class);

        return siteNode;
    }

    /**
     * Returns a canonical application URL.  This is a utility method
     * that constructs a URL fragment (just the path relative to the
     * protocol and server) by trimming the with both leading and
     * trailing slashes.
     *
     */
    public static String getCanonicalURL(String url) {
        // Remove whitespace
        url = url.trim();

        // Verify leading and trailing characters
        String canonicalURL = url.startsWith(SLASH) ? url : (SLASH + url);

        return url.endsWith(SLASH) ? canonicalURL : (canonicalURL + SLASH);
    }

    public String getContextPath() {
        return "";
    }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * Application implementations may overwrite this method to provide an
     * application specific location, especially if an application (module) is
     * to be installed along with others in one context.
     *
     * If you install the module into its own context you may use a standard
     * location. In most cases though all modules (applications) of an
     * webapplication should be installed into one context.
     *
     * Frequently it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>applicationName-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-applicationName</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>applicationName-files</servlet-name>
     *   <url-pattern>/ccm-applicationName/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    public String getServletPath() {
        return URL.SERVLET_DIR + "/legacy-adapter";
    }

    public String getStylesheetPath() {
        return URL.XSL_DIR + "/core-platform.xsl";
    }

    protected void beforeSave() {
        if (isPropertyModified(PRIMARY_URL) || isNew()) {
            BaseDispatcher.scheduleRefresh();
        }

        super.beforeSave();
    }

    // This should be done through PDL
    public void beforeDelete() {
        super.beforeDelete();
        SiteNode node = getSiteNode();
        if (node != null) {
            node.delete();
        }
    }

    public void afterDelete() {
        BaseDispatcher.scheduleRefresh();
    }

    public void setGroup(Group group) {
	setAssociation("containerGroup", group);
	Group parentGroup = getApplicationType().getGroup();
	if (parentGroup != null) {
	    parentGroup.addSubgroup(group);
	}
    }

    // note group is  deleted if application is deleted
    // but subgroups may be orphaned
    /**
     * create a container group for this application.
     * If the application has a parent application with 
     * a container group, set this group as subgroup of that
     * Otherwise, if application type has a container group, set
     * this group as subgroup of that.
     * 
     * Else this is a root level group
     * 
     */
    public void createGroup() {
	Assert.isEqual(getGroup(), null,
			"Group has already been created for Application " + getTitle());

	Group group = new Group();
	group.setName(getTitle() + " Groups");
	s_log.debug("created group " + group.getName());
		
	setAssociation("containerGroup", group);
	Application parentApp = getParentApplication();
	Group parentGroup = parentApp == null ? null : parentApp.getGroup();
	if (parentGroup == null) {
	    parentGroup = getApplicationType().getGroup();
	}
	if (parentGroup != null) {
	    parentGroup.addSubgroup(group);
	    s_log.debug("setting new group as subgroup of " + parentGroup.getName());
        }

    }


	// if application name changes, change name of container group
	
    public void setTitle (String title) {
	super.setTitle(title);
	Group containerGroup = getGroup();
	if (containerGroup != null) {
	    containerGroup.setName(getTitle() + " Groups");
        }
    }
    /**
     * Group associated with this application type. Usually
     * used as a container group to keep group admin tidy.
     * 
     * @return null if no group is associated with this application type
     */
    public Group getGroup() {
	return (Group) DomainObjectFactory.newInstance(
			(DataObject) get("containerGroup"));
    }
}
