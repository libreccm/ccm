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
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.xml.Element;
import com.arsdigita.ui.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;

public class DebugPanel extends SimpleComponent {

    private static GlobalizedMessage s_transformOn;
    private static GlobalizedMessage s_transformOff;
    private static GlobalizedMessage s_xml;
    private static GlobalizedMessage s_xsl;

    private static final String TYPE_GENERIC = "generic";
    private static final String TYPE_ENABLE = "add";
    private static final String TYPE_DISABLE = "delete";

    private static boolean initialized = false;
    
    static void init() {
        if (initialized) {
            return;
        }
        
        s_transformOn = GlobalizationUtil.globalize("ui.debug.transform.on");
        s_transformOff = GlobalizationUtil.globalize("ui.debug.transform.off");
        s_xml = GlobalizationUtil.globalize("ui.debug.xml");
        s_xsl = GlobalizationUtil.globalize("ui.debug.xsl");

        initialized = true;
    }

    public void generateXML(PageState state,
                            Element parent) {
        if (!Kernel.getConfig().isDebugEnabled()) {
            return;
        }
        init();

        Element content = parent.newChildElement("ui:debugPanel",
                                                 UIConstants.UI_XML_NS);
        exportAttributes(content);
        
        URL here = Web.getContext().getRequestURL();
        
        URL xmlURL = selfURL(state, here, "output", "xml");
        URL xslURL = selfURL(state, here, "output", "xsl");
        URL transformOnURL = selfURL(state, here, "debug", "transform");
        URL transformOffURL = selfURL(state, here, "debug", null);
        
        generateLinkXML(state, content, s_xml, xmlURL, TYPE_GENERIC);
        generateLinkXML(state, content, s_xsl, xslURL, TYPE_GENERIC);
        if (here.getParameter("debug") == null) {
            generateLinkXML(state, content, s_transformOn, 
                            transformOnURL, TYPE_ENABLE);
        } else {
            generateLinkXML(state, content, s_transformOff, 
                            transformOffURL, TYPE_DISABLE);
        }
    }
    
    private void generateLinkXML(PageState state,
                                 Element parent,
                                 GlobalizedMessage msg,
                                 URL url,
                                 String type) {
        init();
        Element link = parent.newChildElement("ui:debugLink",
                                              UIConstants.UI_XML_NS);
        link.addAttribute("title", (String)msg.localize(state.getRequest()));
        link.addAttribute("url", url.toString());
        link.addAttribute("type", type);
    }
    
    private URL selfURL(PageState state,
                        URL here,
                        String name,
                        String value) {
        init();
        ParameterMap params = new ParameterMap(here.getParameterMap());
        params.clearParameter(name);
        if (value != null) {
            params.setParameter(name, value);
        }
        
        // XXX, can't use this because it triggers the
        // parameter listeners which clobber the 'debug'
        // parameter we're already setting.
        /*
        return URL.there(state.getRequest(),
                         here.getPathInfo(),
                         params);
        */
        
        return new URL(here.getScheme(),
                       here.getServerName(),
                       here.getServerPort(),
                       here.getContextPath(),
                       here.getServletPath(),
                       here.getPathInfo(),
                       params);
    }
}
