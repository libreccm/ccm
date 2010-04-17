/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.kernel.security.Initializer;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Global navigation elements for the CMS admin UIs.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: GlobalNavigation.java 1942 2009-05-29 07:53:23Z terry $
 */
class GlobalNavigation extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger
        (GlobalNavigation.class);

    private final String m_centerPath;
    private final String m_wspcPath;
    private final String m_signOutPath;
    private final String m_helpPath;

    GlobalNavigation() {
        m_centerPath = Utilities.getWorkspaceURL();
        m_wspcPath = path(Initializer.WORKSPACE_PAGE_KEY);
        m_signOutPath = path(Initializer.LOGOUT_PAGE_KEY);
        m_helpPath = "/nowhere"; // We don't have this yet XXX.
    }

    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final HttpServletRequest sreq = state.getRequest();

            final Element nav = parent.newChildElement
                ("cms:globalNavigation", CMS.CMS_XML_NS);

            final String centerTitle = lz("cms.ui.content_center");
            final String wspcTitle = lz("cms.ui.my_workspace");
            final String signOutTitle = lz("cms.ui.sign_out");
            final String helpTitle = lz("cms.ui.help");

            link(sreq, nav, "cms:contentCenter", m_centerPath, centerTitle);
            link(sreq, nav, "cms:workspace", m_wspcPath, wspcTitle);
            link(sreq, nav, "cms:signOut", m_signOutPath, signOutTitle);
            link(sreq, nav, "cms:help", m_helpPath, helpTitle);
        }
    }

    private static String path(final String key) {
        return "/" + Initializer.getURL(key);
    }

    private static Element link(final HttpServletRequest sreq,
                                final Element parent,
                                final String name,
                                final String path,
                                final String title) {
        final Element link = parent.newChildElement(name, CMS.CMS_XML_NS);

        link.addAttribute("href", URL.there(sreq, path).toString());
        link.addAttribute("title", title);

        return link;
    }

    private static String lz(final String key) {
        return (String) ContentSectionPage.globalize(key).localize();
    }
}
