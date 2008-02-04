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

import org.apache.log4j.Logger;

/**
 * Sample listener for use in test case.
 *
 * @version 1.0
 */

public class SampleListener implements PackageEventListener {

    public static final String versionId = "$Id: SampleListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    public final static String versionID = "$Id: SampleListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static Logger s_cat =
        Logger.getLogger(SampleListener.class.getName());

    public void onMount(SiteNode siteNode, PackageInstance pkg) {
        s_cat.debug("onMount");
    }

    public void onUnmount(SiteNode siteNode, PackageInstance pkg) {
        s_cat.debug("onUnmount");
    }

    public void onCreate(PackageInstance pkg) {
        s_cat.debug("onCreate");
    }

    public void onDelete(PackageInstance pkg) {
        s_cat.debug("onDelete");
    }
}
