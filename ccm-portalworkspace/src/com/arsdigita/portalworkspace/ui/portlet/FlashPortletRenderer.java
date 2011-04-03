/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
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
 */

package com.arsdigita.portalworkspace.ui.portlet;

import java.util.Iterator;
import java.util.Map;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.portalworkspace.portlet.FlashPortlet;
import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.xml.Element;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class FlashPortletRenderer extends AbstractPortletRenderer
{
    private final FlashPortlet m_portlet;

    /**
     * Construct a renderer for the {@link FlashPortlet}.
     * 
     * @param portlet
     *            the portlet
     */
    public FlashPortletRenderer(FlashPortlet portlet)
    {
        super();
        m_portlet = portlet;
    }

    /**
     * @see AbstractPortletRenderer#generateBodyXML
     */
    public void generateBodyXML(PageState state, Element parentElement)
    {
        Element portletElement = parentElement.newChildElement("portlet:flash", PortalConstants.PORTLET_XML_NS);

        portletElement.addAttribute(FlashPortlet.BACKGROUND_COLOUR, m_portlet.getBackgroundColour());
        portletElement.addAttribute(FlashPortlet.DETECT_KEY, m_portlet.getDetectKey());
        portletElement.addAttribute(FlashPortlet.SWF_FILE, m_portlet.getFile());
        portletElement.addAttribute(FlashPortlet.HEIGHT, m_portlet.getHeight());
        portletElement.addAttribute(FlashPortlet.QUALITY, m_portlet.getQuality());
        portletElement.addAttribute(FlashPortlet.REDIRECT_URL, m_portlet.getRedirectUrl());
        portletElement.addAttribute(FlashPortlet.VERSION, m_portlet.getVersion());
        portletElement.addAttribute(FlashPortlet.WIDTH, m_portlet.getWidth());
        portletElement.addAttribute(FlashPortlet.XI_REDIRECT_URL, m_portlet.getXiRedirectUrl());

        Map parameters = m_portlet.getParametersMap();
        if (!parameters.isEmpty())
        {
            Element parametersElement = portletElement.newChildElement("portlet:parameters",
                    PortalConstants.PORTLET_XML_NS);
            for (Iterator i = parameters.keySet().iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                String value = (String) parameters.get(key);
                Element parameterElement = parametersElement.newChildElement("portlet:parameter",
                        PortalConstants.PORTLET_XML_NS);
                parameterElement.addAttribute("name", key);
                parameterElement.addAttribute("value", value);
            }
        }
        Map variables = m_portlet.getVariablesMap();
        if (!variables.isEmpty())
        {
            Element variablesElement = portletElement.newChildElement("portlet:variables",
                    PortalConstants.PORTLET_XML_NS);
            for (Iterator i = variables.keySet().iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                String value = (String) variables.get(key);
                Element variableElement = variablesElement.newChildElement("portlet:variable",
                        PortalConstants.PORTLET_XML_NS);
                variableElement.addAttribute("name", key);
                variableElement.addAttribute("value", value);
            }
        }
    }
}
