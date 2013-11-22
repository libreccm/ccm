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
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 *
 *
 */
public class SiteBanner extends SimpleComponent {

    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement("ui:siteBanner",
                                                 UIConstants.UI_XML_NS);
        exportAttributes(content);

        content.addAttribute("hostname",
                             Web.getConfig().getServer().toString());
        content.addAttribute("sitename",
                             Web.getConfig().getSiteName());
        content.addAttribute("admin",
                             Kernel.getSecurityConfig().getAdminContactEmail());
    }
}
