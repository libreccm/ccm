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


/**
 * An interface for listening to the events of a package listener.
 * Package Listeners are registered according to PackageTypes using
 * the <code>addListener()</code> method.
 *
 * <font color="ff0000">
 * Note: The APIs for this class have not been reviewed and are
 * subject to change after review.
 * </font>
 *
 * @see PackageType#addListener
 * @since ACS 5.0
 * @version $Revision: #7 $, $Date: 2004/08/16 $
 * @version $Id: PackageEventListener.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated without direct replacement. Refactor to use
 *             {@link com.arsdigita.web.Application} instead.
 */
public interface PackageEventListener {

    /**
     * Called when a package instance is mounted on a
     * siteNode.
     *
     * @param siteNode the SiteNode where the instance is mounted
     * @param pkg the instance being mounted
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void onMount(SiteNode siteNode, PackageInstance pkg);

    /**
     * Called when a package instance is unmounted from a
     * siteNode.
     *
     * @param siteNode the SiteNode where the instance is mounted
     * @param pkg the instance being unmounted
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void onUnmount(SiteNode siteNode, PackageInstance pkg);


    /**
     * Called when a new package instance is created.
     *
     * @param pkg a PackageInstance value
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void onCreate(PackageInstance pkg);

    /**
     * Describe onDelete method here.
     *
     * @param pkg a PackageInstance value
     * @deprecated without direct replacement. Refactor to use
     *             {@link com.arsdigita.web.Application} instead.
     */
    public void onDelete(PackageInstance pkg);

}
