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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Link;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.security.Initializer;

/**
 * Delimited dimensional navbar.
 *
 * @author David Lutterkort &lt;dlutter@redhat.com&gt;
 * @version $Id: GlobalNavbar.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class GlobalNavbar extends DimensionalNavbar {
    public static final String versionId = 
        "$Id: GlobalNavbar.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    public GlobalNavbar() {
        super();
        setIdAttr("global-navbar");
        setDelimiter(" - ");
        // FIXME: Write online help, for the time being offer no link
        // add(new Link("Help", "help"));

        String signOutURL = Utilities.getWebappContext() + "/" +
            Initializer.getURL(Initializer.LOGOUT_PAGE_KEY);

        add(new Link((String) globalize("cms.ui.sign_out").localize(),
                     signOutURL));
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(String key) {
        return ContentSectionPage.globalize(key);
    }

}
