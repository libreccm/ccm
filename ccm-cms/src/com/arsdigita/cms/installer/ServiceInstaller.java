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



/**
 * <p>Provides methods to install the CMS Service application, which
 * is used by the Content Management System as a store for global
 * resources and assets.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ServiceInstaller implements PackageEventListener {

    public static final String versionId = "$Id: ServiceInstaller.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public final static String PACKAGE_KEY = "cms-service";


    /**
     * Create the CMS services package type.
     * @return The package type
     */
    protected static PackageType createPackageType()
        throws DataObjectNotFoundException {

        PackageType type = PackageType.create
            (PACKAGE_KEY, "Content Management System Services",
             "Content Management System Services",
             "http://cms-service.arsdigita.com/");
        type.setDispatcherClass("com.arsdigita.cms.dispatcher.ServiceDispatcher");
        type.save();

        return type;
    }

    /**
     * Create an instance of the CMS service package type.
     * @return The package instance
     */
    protected static PackageInstance createPackageInstance()
        throws DataObjectNotFoundException {

        PackageType type = PackageType.findByKey(PACKAGE_KEY);
        PackageInstance instance = type.createInstance(PACKAGE_KEY);
        instance.save();

        return instance;
    }

    /**
     * Mount the CMS services package instance.
     *
     * @param instance The package instance
     * @param location The URL where the package instance will be mounted
     * @return The SiteNode where the instance was mounted
     */
    protected static SiteNode mountPackageInstance(PackageInstance instance,
                                                   String location) {
        SiteNode node =
            SiteNode.createSiteNode(location, SiteNode.getRootSiteNode());
        node.mountPackage(instance);
        node.save();

        return node;
    }



    ////////////////////////////////////
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
