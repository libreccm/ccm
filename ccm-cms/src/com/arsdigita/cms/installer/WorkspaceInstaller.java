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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.PackageEventListener;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;



/**
 * <p>Provides methods to install the Content Center application, which
 * is used by the Content Management System.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #10 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: WorkspaceInstaller.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class WorkspaceInstaller implements PackageEventListener {

    public static final String PACKAGE_KEY   = "content-center";
    private static final String INSTANCE_NAME = "Content Center";
    private static final String DISPATCHER_CLASS =
        "com.arsdigita.cms.dispatcher.ContentCenterDispatcher";

    // To be updated soon...
    // "com.arsdigita.dispatcher.DefaultPackageDispatcher";

    /**
     * Returns this workspace's instance name
     **/
    public String getPackageKey() {
        return PACKAGE_KEY;
    }

    /**
     * Returns this workspace's instance name
     **/
    public String getInstanceName() {
        return INSTANCE_NAME;
    }

    /**
     * Returns the name of the dispatcher class to use
     **/
    protected String getDispatcherClass() {
        return DISPATCHER_CLASS;
    }

    /**
     * Create the CMS package type.
     *
     * @return The Content Center package type
     */
//  protected PackageType createPackageType()
    public PackageType createPackageType()
        throws DataObjectNotFoundException {

        PackageType type = PackageType.create
            (getPackageKey(), "Content Center",
             "Content Centers",
             "http://cms-workspace.arsdigita.com/");
        type.setDispatcherClass(getDispatcherClass());

        // Register a stylesheet to the Content Center package.
        Stylesheet ss =
            Stylesheet.createStylesheet(
                       "/packages/content-section/xsl/content-center.xsl");
        ss.save();
        type.addStylesheet(ss);

        type.save();

        return type;
    }

    /**
     * Create the Content Center application instance.
     *
     * @return The Content Center package instance
     */
//  protected PackageInstance createPackageInstance()
    public PackageInstance createPackageInstance()
        throws DataObjectNotFoundException {

        PackageType type =
            PackageType.findByKey(getPackageKey());
        PackageInstance instance =
            type.createInstance(getInstanceName());
        instance.save();

        return instance;
    }

    /**
     * Mount the Content Center application instance.
     *
     * @param instance The package instance
     * @param location The location of the Content Center
     * @return The SiteNode where the Content Center is mounted at
     */
//  protected SiteNode mountPackageInstance(PackageInstance instance,
    public SiteNode mountPackageInstance(PackageInstance instance,
                                            String location) {
        SiteNode node =
            SiteNode.createSiteNode(location, SiteNode.getRootSiteNode());
        node.mountPackage(instance);
        node.save();

        return node;
    }

    /////////////////////////////////////
    //
    // PackageEventListener methods
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
     * It does nothing
     * @param pkg a <code>PackageInstance</code> value
     */
    public void onCreate(PackageInstance pkg) {}

    /**
     * Describe <code>onDelete</code> method here.
     *
     * @param pkg a <code>PackageInstance</code> value
     */
    public void onDelete(PackageInstance pkg) {}


}
