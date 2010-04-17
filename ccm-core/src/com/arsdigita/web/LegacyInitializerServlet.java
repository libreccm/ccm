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
package com.arsdigita.web;

import com.arsdigita.developersupport.Debug;
import com.arsdigita.initializer.Script;
import com.arsdigita.util.ResourceManager;
import java.io.InputStream;
import javax.servlet.ServletException;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: LegacyInitializerServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class LegacyInitializerServlet extends BaseServlet {

    private Script m_initializer = null;

    /**
     * Starts up the web environment for the ACS by loading the specified
     * initializer script.
     */
    protected final void doInit() throws ServletException {
        Debug.setLevel("com.arsdigita.initializer.Script", "info");

        final String script = getServletConfig().getInitParameter("init");

        final ResourceManager rm = ResourceManager.getInstance();
        rm.setServletContext(getServletContext());

        final InputStream is = rm.getResourceAsStream(script);

        if (is == null) {
            throw new ServletException("Couldn't find " + script);
        }

        m_initializer = new Script(is);
        m_initializer.startup();
    }

    protected final void doDestroy() {
        m_initializer.shutdown();
        m_initializer = null;
    }
}
