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
package com.arsdigita.portalserver.ui.admin;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import java.io.IOException;

import java.math.BigDecimal;


import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portalserver.PortalSite;

import com.arsdigita.portal.Portal;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.portal.AgentPortlet;


import com.arsdigita.xml.Element;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.util.BebopConstants;



/**
 * <p>This component provides UI for modifying the layout of a single
 * portal and for adding new portlets to it.</p>
 **/
abstract class PortletLayoutComponent
    extends SimpleComponent
    implements BebopConstants {

    private static final String PREV_EVENT = "prev";
    private static final String NEXT_EVENT = "next";
    private static final String LEFT_EVENT = "left";
    private static final String RIGHT_EVENT = "right";
    private static final String DEL_EVENT = "del";
    private static final String CONFIG_EVENT = "cfg";

    private static final String PORTAL_XML_NS =
        "http://www.arsdigita.com/portalserver/1.0";


    private final RequestLocal m_portalsiteRL;
    private final RequestLocal m_portalIDRL;


    public PortletLayoutComponent(RequestLocal portalsiteRL,
                                  RequestLocal portalIDRL) {

        m_portalsiteRL = portalsiteRL;
        m_portalIDRL = portalIDRL;

    }


    // Generate XML for the given portlet
    private void generateXMLHelper(PageState ps,
                                   Portlet portlet,
                                   Element portalElt,
                                   boolean hasPrev,
                                   boolean hasNext,
                                   int numCols,
                                   String layout) {

        //These boolean values are used to prevent movement
        //of portlets into, out of, or within locked cell regions
        boolean isLocked = false;
        boolean canMoveLeft = true;
        boolean canMoveRight = true;

        Element portletElt =
            portalElt.newChildElement(BEBOP_PORTLET,
                                      Component.BEBOP_XML_NS);

        // Standard portlet attributes
        portletElt.addAttribute("title", portlet.getTitle());
        portletElt.addAttribute("cellNumber",
                                Integer.toString(portlet.getCellNumber()));
        portletElt.addAttribute("sortKey",
                                Integer.toString(portlet.getSortKey()));
        portletElt.addAttribute("profile", portlet.getProfile());
        portletElt.addAttribute("configure", "true");

        String portletIDstr = portlet.getID().toString();

        // Add attributes containing URLs that fire control events
        // for various portlet actions
        try {

            int cellNum = portlet.getCellNumber();
            if(layout.charAt(cellNum - 1) == 'n' ||
               layout.charAt(cellNum - 1) == 'w')
                isLocked = true;

            if(layout.length() == 2) {
                if(cellNum == 1) {
                  if(layout.charAt(1) == 'n' ||
                     layout.charAt(1) == 'w') {
                       canMoveRight = false;
                  }
                }
                if(cellNum == 2) {
                  if(layout.charAt(0) == 'n' ||
                     layout.charAt(0) == 'w') {
                       canMoveLeft = false;
                  } 
                }
            } 

            if(layout.length() == 3) {
                if(cellNum == 1) {
                  if(layout.charAt(1) == 'n' ||
                     layout.charAt(1) == 'w') {
                       canMoveRight = false;
                  }
                } 
                if(cellNum == 3) {
                  if(layout.charAt(1) == 'n' ||
                     layout.charAt(1) == 'w') {
                       canMoveLeft = false;
                  }
                }
                if(cellNum == 2) {
                  if(layout.charAt(0) == 'n' ||
                     layout.charAt(0) == 'w') {
                       canMoveLeft = false;
                  }
                  if(layout.charAt(2) == 'n' ||
                     layout.charAt(2) == 'w') {
                       canMoveRight = false;
                  }
                }

            }
             

            // Add attribute containing URL for "delete" link
            // unless this portlet is in a locked cell.
            if(!isLocked) {
                ps.setControlEvent(PortletLayoutComponent.this,
                               DEL_EVENT, portletIDstr);
                portletElt.addAttribute("delURL", ps.stateAsURL());
            }

            // Add attribute containing URL for "config" link
            // unless the portlet is an agent portlet for
            // another superportal portlet.
            if(! (portlet instanceof AgentPortlet)) {
                ps.setControlEvent(PortletLayoutComponent.this,
                               CONFIG_EVENT, portletIDstr);
                portletElt.addAttribute("cfgURL", ps.stateAsURL());
            }

            // Maybe add attribute containing URL for "move up" link
            if(!isLocked) {
              if (hasPrev) {
                  ps.setControlEvent(PortletLayoutComponent.this,
                                     PREV_EVENT, portletIDstr);
                  portletElt.addAttribute("prevURL", ps.stateAsURL());
              }
            }

            // Maybe add attribute containing URL for "move down" link
            if(!isLocked) {
              if (hasNext) {
                  ps.setControlEvent(PortletLayoutComponent.this,
                                     NEXT_EVENT, portletIDstr);
                  portletElt.addAttribute("nextURL", ps.stateAsURL());
              }
            } 

            //When to draw move left arrow:
            //1 column...never
            //2 columns...when cell num == 2
            //3 columns...when cell num > 1
            if((!isLocked) && (canMoveLeft)) {
              if (numCols != 1 &&
                  ((numCols == 2 && cellNum == 2) ||
                   (numCols == 3 && cellNum > 1))) {
                  ps.setControlEvent(PortletLayoutComponent.this,
                                     LEFT_EVENT, portletIDstr);
                  portletElt.addAttribute("leftURL", ps.stateAsURL());
              }
            }

            //When to draw move right arrow
            //1 column...never
            //2 columns...when cell num != 2
            //3 columns...when cell num < numColumns
            if((!isLocked) && (canMoveRight)) {
              if (numCols != 1 &&
                  ((numCols == 2 && cellNum != 2) ||
                   (numCols == 3 && cellNum < numCols))) {
                  ps.setControlEvent(PortletLayoutComponent.this,
                                     RIGHT_EVENT, portletIDstr);
                  portletElt.addAttribute("rightURL", ps.stateAsURL());
              }
            }

            ps.clearControlEvent();

        } catch (IOException ex) {
            throw new IllegalStateException("Caught IOException: " +
                                            ex.getMessage());
        }

        // Put in the "guts" of the portlet - a description taken from
        // the portlet type.
        Element labelElt =
            portletElt.newChildElement("bebop:label",
                                       Component.BEBOP_XML_NS);
        String desc = portlet.getPortletType().getDescription();
        if (desc == null) {
            desc = "";
        }
        labelElt.setText(desc);
    }


    /**
     * <p>Generate the XML for the configurable portal display.  Much
     * of the code for this is cribbed from {@link
     * com.arsdigita.bebop.Portal com.arsdigita.bebop.Portal's}
     * generateXML.</p>
     **/
    public void generateXML(PageState ps, Element parentElt) {

        BigDecimal portalID = (BigDecimal)m_portalIDRL.get(ps);
        if (portalID == null) {
            return;
        }

        if (!(PortalTab.doesTabExist(portalID))) {
            return;
        }

        int numCols = 0;

        PortalTab ptab = PortalTab.retrieveTab(portalID);
        String layout = ptab.getLayout();
        //Layout is W for 1 column, WN or NW for 2 cols, or NWN or NNN 3 cols..
        if(layout == null)
            numCols = 2;
        else
            numCols = layout.length();

        Element portalElt =
            parentElt.newChildElement(BEBOP_PORTAL, Component.BEBOP_XML_NS);

        exportAttributes(portalElt);

        portalElt.addAttribute("title", ptab.getTitle());
        portalElt.addAttribute("configure", "true");

        //If tab has locked cell regions, these are represented by 
        //lower case style chars...we need to upper case them before 
        //using them as a portaltab attr.
        String layoutstyle = ptab.getLayout();
        if(layoutstyle != null)
            layoutstyle = layoutstyle.toUpperCase();
        portalElt.addAttribute("style", layoutstyle);

        PortletCollection portletCollection = ptab.getPortlets();

        if (!portletCollection.next()) {
            return;
        }

        // Loop through portlets, generating XML for each
        boolean hasPrev = false;
        Portlet curr = portletCollection.getPortlet();
        Portlet next = null;
        while (portletCollection.next()) {
            next = portletCollection.getPortlet();
            // If the next portlet is not in the same cell as the
            // current portlet, then the current portlet will have no
            // "move down" link, and the next portlet will have
            // no "move up" link.
            if (curr.getCellNumber() != next.getCellNumber()) {
                generateXMLHelper(ps, curr, portalElt, hasPrev, false, numCols,
                                  layout);
                hasPrev = false;
            } else {
                generateXMLHelper(ps, curr, portalElt, hasPrev, true, numCols,
                                  layout);
                hasPrev = true;
            }
            curr = next;
        }

        // Generate XML for the last portlet
        generateXMLHelper(ps, curr, portalElt, hasPrev, false, numCols, layout);
    }


    public void respond(PageState ps) {
        String event = ps.getControlEventName();

        if (CONFIG_EVENT.equals(event)) {
            BigDecimal portletID = new BigDecimal(ps.getControlEventValue());
            handleConfigure(ps, portletID);
            return;
        }

        BigDecimal portletID = new BigDecimal(ps.getControlEventValue());
        Portlet portlet = Portlet.retrievePortlet(portletID);
        Portal portal = portlet.getPortal();

        if (NEXT_EVENT.equals(event)) {
            portal.swapPortletWithNext(portlet);
        } else if (PREV_EVENT.equals(event)) {
            portal.swapPortletWithPrevious(portlet);
        } else if (LEFT_EVENT.equals(event)) {
            int cell = portlet.getCellNumber();
            cell = cell - 1;
            if (cell < 1) { cell = 1; }
            portlet.setCellNumber(cell);
            portlet.save();
        } else if (RIGHT_EVENT.equals(event)) {
            int cello = portlet.getCellNumber();
            cello = cello + 1;
            if (cello > 3) { cello = 3; }
            portlet.setCellNumber(cello);
            portlet.save();
        } else if (DEL_EVENT.equals(event)) {
            portlet.delete();
        } else {
            throw new IllegalStateException("Unknown portlet layout event");
        }

        // Portal should make sure a moved portlet gets saved, so
        // saving the portlet is unnecessary.
        //portlet.save();
        portal.save();
    }


    abstract protected void handleConfigure(PageState ps, BigDecimal portletID);
}
