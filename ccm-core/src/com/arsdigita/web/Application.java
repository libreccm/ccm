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
// import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
// import com.arsdigita.kernel.KernelExcursion;
// import com.arsdigita.kernel.PackageInstance;
// import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.Resource;
// import com.arsdigita.kernel.SiteNode;
// import com.arsdigita.persistence.DataAssociation;
// import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
// import com.arsdigita.persistence.DataQuery;
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
 * <p>A base class for defining a web application. </p> 
 * 
 * An application has three important aspects:
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

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(Application.class);
    
    public static final String PRIMARY_URL = "primaryURL";
    private static final String SLASH = "/";

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.web.Application";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Creates a new Application instance encapsulating the given data object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     *
     * @param dataObject The data object to encapsulate in the Forum instance
     *                   (new domain object).
     */
    protected Application(DataObject dataObject) {
        super(dataObject);
    }

    protected Application(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Convenient class for creation of a new style / legacy free application
     * just based on its type and title,
     * without parent, no explicit URL fragment, no associated container group
     */
    public static Application createRootApplication(final ApplicationType type,
                                                    final String title) {
        return Application.createRootApplication(type, title, false);
    }

    /**
     * Convenient class for creation of a new style / legacy free application
     * just based on its type and title and whether to associate a container
     * group,
     * without parent and without an explicit URL fragment
     */
    public static Application createRootApplication(
                                           final ApplicationType type,
                                           final String title,
                                           final boolean createContainerGroup) {
        if (Assert.isEnabled()) {
            Assert.exists(type, ApplicationType.class);
            Assert.exists(title, String.class);
         // Assert.isTrue(type.m_legacyFree);
        }

        return Application.make(type, null, title, null, createContainerGroup);
    }

    /** 
     * Wrapper class to create a new application using a limited set of parameters,
     * here: createContainerGroup is false.
     * 
     * @param type
     * @param fragment URL fragment of the application
     * @param title  
     * @param parent parent Application
     * @return
     */
    public static Application createApplication(final ApplicationType type,
                                                final String fragment,
                                                final String title,
                                                final Application parent) {
        s_log.debug("Create Application");
        return Application.createApplication(type,fragment,title,parent,false);
    }

    // For convenience.
    /**
     * 
     * @param typeName
     * @param fragment URL fragment of the application
     * @param title
     * @param parent
     * @return
     */
    public static Application createApplication(final String typeName,
                                                final String fragment,
                                                final String title,
                                                final Application parent) {

        return Application.createApplication(typeName,fragment,title,parent,false);
    }

    /** 
     * 
     * @param typeName
     * @param fragment
     * @param title
     * @param parent
     * @param createContainerGroup
     * @return
     */
    public static Application createApplication(
                                  final String typeName,
                                  final String fragment,
                                  final String title,
                                  final Application parent,
                                  final boolean createContainerGroup) {
        final ApplicationType type = ApplicationType
                                     .retrieveApplicationTypeForApplication(
                                                                      typeName);
        if (type == null) {
            throw new IllegalArgumentException(
                      "No ApplicationType found for type name " + typeName);
        }
        return Application.createApplication(type,fragment,
                                             title,parent,createContainerGroup);
    }

    /**
     * Prepares the actual creation of an application either as a legacy free or
     * as a legacy compatible type, depending on the style of its applicationType.
     * (A legacy compatible app creates the corresponding entries in deprecated
     * kernel Package and Sitenode stuff as well, in parallel.)
     * 
     * @param type  application class (class name)
     * @param fragment URL fragment og the application
     * @param title
     * @param parent parent application
     * @param createContainerGroup
     * @return
     */
    public static Application createApplication(
                                        final ApplicationType type,
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
    //  s_log.debug("Application type legacy free: " + type.m_legacyFree );
    //  if (type.m_legacyFree) {
            return Application.make(type,fragment,title,parent,
                                    createContainerGroup);
    //  } else {
    //      s_log.debug("Creating legacy compatible app");
    //      return Application.legacyMake(type,fragment,title,parent,
    //                                    createContainerGroup);
    //  }
    }

    /** 
     * Creates (makes) a legacy free application.
     *
     * @param type   application type
     * @param fragment last part of the applications URL
     * @param title descriptive name 
     * @param parent 
     * @param createContainerGroup
     * @return
     */
    private static Application make(final ApplicationType type,
                                    final String fragment,
                                    final String title,
                                    final Application parent,
                                    final boolean createContainerGroup) {
        
	final Application app =	(Application) Resource.createResource(type, 
                                                                  title, 
                                                                  parent);
	if (createContainerGroup) {
	    app.createGroup();
	}
        if (Assert.isEnabled() && fragment != null) {
            Assert.isTrue(fragment.indexOf('/') == -1,
                         "The URL fragment must not contain " +
                         "slashes; I got '" + fragment + "'");
        }

        /* Problem with "slash or not slash"
         * String fragment (=url) is expected without any slash, just the name.
         * Given the original code below the fragment appears in database as
         * "/[fragment]" but all of the other code expects "/[fragment]/" and
         * all other applications created as legacy compatible have a trailing
         * slash!
         * So I experimentally changed the code to have a trailing slash.
         * Because no other code uses legacy free applications I suppose the
         * original code here is less tested.
         * pboy April 2011  see method setPath() as well!
         */
        if (parent == null) {
            if (fragment == null) {
                // app.setPath(""); original code modified see above
                app.setPath(SLASH);
            } else {
                // app.setPath(SLASH + fragment); original code modified see above
                app.setPath(SLASH + fragment + SLASH);
            }
        } else {
            // app.setPath(parent.getPath() + SLASH + fragment); original code
            //                                                   modified see above
            app.setPath(parent.getPath() + SLASH + fragment + SLASH);
        }

        return app;
    }

//  /**
//   * Creates (makes) a legacy compatible application (using deprecated kernel
//   * packageType and sitenode stuff).
//   * @param type
//   * @param fragment
//   * @param title
//   * @param parent
//   * @param createContainerGroup
//   * @return
//   */
/*private static Application legacyMake(final ApplicationType type,
                                          final String fragment,
                                          final String title,
                                          final Application parent,
                                          final boolean createContainerGroup) {

	final Application application =	(Application) Resource.createResource(
                                                           type, title, parent);
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
    }  */

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

//  /**
//   * 
//   * Can return null.
//   * @param siteNode
//   * @return
//   * @ deprecated 
//   */
/*  public static Application retrieveApplicationForSiteNode
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
    }   */

    // Can return null.
    public static Application retrieveApplicationForPath(String path) {

        s_log.debug("retrieveApplicationForPath: " + path);
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        dataCollection.addEqualsFilter(PRIMARY_URL, path);

        if (dataCollection.next()) {
            DataObject dataObject = dataCollection.getDataObject();
            dataCollection.close();
            return Application.retrieveApplication(dataObject);
        } else {
        s_log.debug("retrieveApplicationForPath: No application found on " + path);
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
//  /**
//   * @deprecated refactor not using deprecated class PackageType. Use
//   * ApplicationType instead
//   */
//  public PackageType getPackageType() {
//      return getApplicationType().getPackageType();
//  }

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

    /**
     * 
     * @param applicationType
     * @return 
     */
    public ApplicationCollection getChildApplicationsForType
                                                    (String applicationType) {
        ApplicationCollection children = getChildApplications();
        children.addEqualsFilter("objectType", applicationType);
        return children;
    }

//  /**
//   * 
//   * @return
//   * @deprecated refactor to use other methods of class aüpplication instead
//   */
/*  private PackageInstance getPackageInstance() {
        DataObject dataObject = (DataObject) get("packageInstance");

        Assert.exists(dataObject, DataObject.class);

        return new PackageInstance(dataObject);
    }  */

//  /**
//   * 
//   * @return
//   * @deprecated refactor to use other methods of class aüpplication instead
//   */
/*  private void setPackageInstance(PackageInstance packageInstance) {
        Assert.exists(packageInstance, PackageInstance.class);

        setAssociation("packageInstance", packageInstance);
    }   */

//  /** 
//    * 
//   * Needs to be getSiteNodes instead.
//   * @return Can return null.
//   * @deprecated 
//   */
/*  public SiteNode getSiteNode() {
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
    }    */

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
     * Returns the path to this application through the dispatcher. It does not
     * contain the static prefix (if configured, "ccm" by default), so it can
     * be used to construct url's for internal links.
     * 
     * The path does not end in a slash.  This method will not return
     * null.
     * 
     * @return Path string including w/o static prefix (if configured)
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

    // XXX primary URL doesn't keep in sync with sitenode hierarchy
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
/* Modified by pboy April 2011
 * This Assert statement prevents a trailing slash. setPath is currently called 
 * only by Applicatiom#make which creates a LEGACY FREE application.
 * Legacy compatible applications are currently created WITH a trailing slash
 * (see e.g. SiteNode#setURL oder SiteNode#getURLFromParent.) Therefore for the
 * time beeing if we must support legacy free and legacy compatible applications
 * in parallel we have to use a trailing slash for legacy free applications,
 * otherwise they will not be found by methods like retrieveApplicationForPath()
 * which is called by legacy compatible apps including a trailing slash. If 
 * legacy free apps are stored without trailing slash the search will never match.  
 */
//          Assert.isTrue
//              (path.equals("") || (path.startsWith(SLASH)
//                                   && !path.endsWith(SLASH)),
//               "The path must either be the empty string (for the " +
//               "default application) or it must start with '/' and *not* " +
//               "end in '/'; I got '" + path + "'");
      }

        set(PRIMARY_URL, path);
    }

    public Collection getRelevantPrivileges() {
        return getApplicationType().getRelevantPrivileges();
    }

    /**
     * Retrieve all installed applications (portlets excluded).
     * @return a collection of installed 
     */
    public static ApplicationCollection retrieveAllApplications() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        // exclude all portlets (no application at all) and portal panes
        // (no application but sort of "sub-application").
        dataCollection.addEqualsFilter
            ("resourceType.hasFullPageView", Boolean.TRUE);
             
        ApplicationCollection apps = new ApplicationCollection
            (dataCollection);

        return apps;
    }
    /**
     * Retrieve all installed applications (portlets excluded).
     * @return a collection of installed 
     */
    public static ApplicationCollection retrieveAllApplications(String 
                                                                applicationType) {
        DataCollection dataCollection = SessionManager.getSession()
                                        .retrieve(BASE_DATA_OBJECT_TYPE);

        // exclude all portlets (no application at all) and portal panes
        // (no application but sort of "sub-application").
        dataCollection.addEqualsFilter
            ("resourceType.hasFullPageView", Boolean.TRUE);
        dataCollection.addEqualsFilter("objectType", applicationType);
             
        ApplicationCollection apps = new ApplicationCollection(dataCollection);

        return apps;
    }

    public static boolean isInstalled (String applicationObjectType,
                                       String path) {
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
/*  private static SiteNode makeSiteNode(String urlName, Application parent) {
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
    }  */

    /**
     * Returns a canonical application URL.  This is a utility method
     * that constructs a URL fragment (just the path relative to the
     * protocol and server) by trimming white spaces and the ensuring both
     * leading and trailing slashes.
     *
     */
    public static String getCanonicalURL(String url) {
        
        String canonicalURL;
        url = url.trim();  // Remove whitespace

        // Verify leading and trailing characters
        canonicalURL = url.startsWith(SLASH) ? url : (SLASH + url);
        canonicalURL = url.endsWith(SLASH) ? canonicalURL : (canonicalURL + SLASH);

        return canonicalURL ;
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
     * NOTE: According to Servlet API the path always starts with a leading '/'
     * and includes either the servlet name or a path to the servlet, but does 
     * not include any extra path information or a query string. Returns an
     * empty string ("") is the servlet used was matched using the "/*" pattern.
     * 
     * @return path name to the applications servlet/JSP
     */
    public String getServletPath() {
        return URL.SERVLET_DIR + "/legacy-adapter";
    }

    @Override
    protected void beforeSave() {
        if (isPropertyModified(PRIMARY_URL) || isNew()) {
            BaseDispatcher.scheduleRefresh();
        }

        super.beforeSave();
    }

    // This should be done through PDL
    @Override
    public void beforeDelete() {
        super.beforeDelete();
    //  SiteNode node = getSiteNode();
    //  if (node != null) {
    //      node.delete();
    //  }
    }

    @Override
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


    /**
     * .
     * 
     * Note: If application name changes, change name of container group.
     * 
     * @param title 
     */
    @Override
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
