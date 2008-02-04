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
package com.arsdigita.cms.installer;


import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.dispatcher.Resource;
import com.arsdigita.cms.dispatcher.ResourceMapping;
import com.arsdigita.cms.dispatcher.ResourceType;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.PackageEventListener;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;


/**
 * <p>Provides methods to install the Content Management System.</p>
 *
 * <p>This class includes methods to:</p>
 *
 * <ul>
 *   <li><p>Create the CMS package type.</p></li>
 *   <li><p>Create the CMS package instance.</p></li>
 *   <li><p>Create a default content section.</p></li>
 *   <li><p>Mount a CMS package instance (and content section).</p></li>
 * </ul>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class Installer implements PackageEventListener {
//public class Installer {

    public static final String versionId = "$Id: Installer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public final static String PACKAGE_KEY = "content-section";

    public final static String DISPATCHER_CLASS =
        "com.arsdigita.cms.dispatcher.ContentSectionDispatcher";

    // To be updated soon...
    // "com.arsdigita.cms.dispatcher.ContentSectionDispatcher";

    public final static String LISTENER_CLASS =
        "com.arsdigita.cms.installer.Installer";

    public final static String STYLESHEET = "/packages/content-section/xsl/cms.xsl";

    /**
     * <p>Creates the CMS package type.</p>
     *
     * <p>This includes creating and registering XSL stylesheets to the CMS .
     * package type and registering privileges for use by CMS.</p>
     *
     * @return The CMS package type
     */
    protected static PackageType createPackageType()
        throws DataObjectNotFoundException {

        PackageType type = PackageType.create
            (PACKAGE_KEY, "Content Management System", "Content Management Systems",
             "http://cms-java.arsdigita.com/");
        type.setDispatcherClass(DISPATCHER_CLASS);
        type.addListener(LISTENER_CLASS);

        // Register a stylesheets to the CMS package.
        Stylesheet ss = Stylesheet.createStylesheet(STYLESHEET);
        ss.save();
        type.addStylesheet(ss);
        type.save();

        createPrivileges();

        return type;
    }

    /**
     * <p>Creates the CMS package type.</p>
     *
     * <p>This includes creating and registering XSL stylesheets to the CMS .
     * package type and registering privileges for use by CMS.</p>
     *
     * @param name The name of the package instance
     * @return A new CMS package instance
     */
    protected static PackageInstance createPackageInstance(String name)
        throws DataObjectNotFoundException {

        PackageType type = PackageType.findByKey(PACKAGE_KEY);
        PackageInstance instance = type.createInstance(name);
        instance.save();
        return instance;
    }

    /**
     * <p>Mounts a CMS package instance.</p>
     *
     * <p>This includes creating and registering XSL stylesheets to the CMS .
     * package type and registering privileges for use by CMS.</p>
     *
     * @param instance The package instance
     * @param location The location of the package instance (and content section)
     * @return The SiteNode where the content section is mounted at
     */
    protected static SiteNode mountPackageInstance(PackageInstance instance,
                                                   String location) {
        SiteNode node =
            SiteNode.createSiteNode(location, SiteNode.getRootSiteNode());
        node.mountPackage(instance);
        node.save();

        return node;
    }




    /**
     * Creates and maps default resources to the content section.
     *
     * @param section The content section
     *
     * MP: create resource types.
     * MP: use the resources API.
     * MP: only create resources once.
     */
    protected static void createDefaultResources(ContentSection section) {
	// XML resources
	ResourceType rt = ResourceType.findResourceType("xml");
	Resource r =
	    rt.createInstance("com.arsdigita.cms.ui.ContentSectionPage");
	r.save();
	ResourceMapping rm = r.createInstance(section, "admin");
	rm.save();
	rm = r.createInstance(section, "admin/index");
	rm.save();
	
	//This won't be served by the new application-oriented CMS dispatcher.  
	//The default folder template creates this instead now.
	//             r = rt.createInstance("com.arsdigita.cms.user.ItemIndexPage");
	//             r.save();
	//             rm = r.createInstance(section, "index");
	//             rm.save();
	
	r = rt.createInstance("com.arsdigita.cms.ui.ContentItemPage");
	r.save();
	rm = r.createInstance(section, "admin/item");
	rm.save();
    }

    /**
     * Creates the CMS privileges.
     */
    private static void createPrivileges() {

        final String CMS_PRIVILEGES = "com.arsdigita.cms.getPrivileges";
        final String PRIVILEGE = "privilege";

        DataQuery dq = SessionManager.getSession().retrieveQuery(CMS_PRIVILEGES);
        while ( dq.next() ) {
            String privilege = (String) dq.get(PRIVILEGE);
            if ( PrivilegeDescriptor.get(privilege) == null ) {
                PrivilegeDescriptor.createPrivilege(privilege);
            }
        }
        dq.close();
    }



    /**
     * Creates the root folder for a content section.
     *
     * @param name The name of the content section
     * @return The root folder
     */
    protected static Folder createRootFolder(String name) {
        Folder root = new Folder();
        root.setName("/");
        root.setLabel( (String) GlobalizationUtil.globalize("cms.installer.root_folder").localize());
        root.save();
        return root;
    }

    /**
     * Creates the root category for a content section.
     *
     * @param name The name of the content section
     * @return The root category
     */
    protected static Category createRootCategory(String name) {
        Category root = new Category("/", "Root Category");
        root.save();
        return root;
    }




    //////////////////////////////////
    //
    //  PackageEventListener methods
    //

    /**
     * This method is called when a package instance is mounted on a
     * siteNode. It does nothing.
     *
     * @param siteNode The SiteNode where the instance is mounted.
     * @param pkg The instance being mounted.
     */
    public void onMount(SiteNode siteNode, PackageInstance pkg) {}

    /**
     * This method is called when a package instance is unmounted from a
     * siteNode. It does nothing.
     *
     * @param siteNode The SiteNode where the instance is mounted.
     * @param pkg The instance being unmounted.
     */
    public void onUnmount(SiteNode siteNode, PackageInstance pkg) {}


    /**
     * This method is called when a new package instance is created.
     * This method creates a content section and a new instance of
     * the CMS package.
     *
     * @param pkg a <code>PackageInstance</code> value
     */
    public void onCreate(PackageInstance pkg) {
    }

    /**
     * This method is called when a package instance is deleted.
     * It does nothing.
     *
     * @param pkg a <code>PackageInstance</code> value
     */
    public void onDelete(PackageInstance pkg) {}

}
