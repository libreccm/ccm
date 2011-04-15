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
package com.arsdigita.portalserver.ui.admin;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import java.io.IOException;

import java.math.BigDecimal;


import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portalserver.PortalSite;

import com.arsdigita.portal.Portal;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;

import com.arsdigita.xml.Element;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;



/**
 * <p>This component provides UI for locking and unlocking 
 * the layout cells of a single portal.</p>
 **/
public class LockableLinks
    extends SimpleContainer
    implements BebopConstants {

    private static final String NARROW_UNLOCKED = "N";
    private static final String NARROW_LOCKED = "n";
    private static final String WIDE_UNLOCKED = "W";
    private static final String WIDE_LOCKED = "w";


    private final RequestLocal m_portalsiteRL;
    private final RequestLocal m_portalIDRL;

    ActionLink columnOneLink;
    ActionLink columnTwoLink;
    ActionLink columnThreeLink;


    public LockableLinks(RequestLocal portalsiteRL, RequestLocal portalIDRL) {

        m_portalsiteRL = portalsiteRL;
        m_portalIDRL = portalIDRL;


         Label columnOneLinkLabel = new Label("Lock/Unlock");
         columnOneLinkLabel.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             Label t = (Label)e.getTarget();
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 String frag = layout.substring(0,1);
                 if(frag.compareTo(NARROW_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if (frag.compareTo(NARROW_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } else if(frag.compareTo(WIDE_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if(frag.compareTo(WIDE_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } 
               }
             }
             else {
               return;
             }
          }
        });  
        columnOneLink = new ActionLink(columnOneLinkLabel);
        columnOneLink.setIdAttr("columnonelocklink");
        columnOneLink.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState(); 
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 char c = layout.charAt(0);
                 String frag; 
                 if(layout.length() > 1)
                   frag = layout.substring(1);
                 else
                   frag = ""; 
                 if(c == 'n') {
                   ptab.setLayout("N" + frag);
                 } else if(c == 'N') {
                   ptab.setLayout("n" + frag);
                 } else if(c == 'w') {
                   ptab.setLayout("W" + frag);
                 } else if(c == 'W') {
                   ptab.setLayout("w" + frag);
                 }
                 ptab.save();
               }
             }
           }
         });

        Label columnTwoLinkLabel = new Label("Lock/Unlock");
        columnTwoLinkLabel.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             Label t = (Label)e.getTarget();
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 if(layout.length() < 2)
                   return;
                 String frag = layout.substring(1,2);
                 if(frag.compareTo(NARROW_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if (frag.compareTo(NARROW_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } else if(frag.compareTo(WIDE_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if(frag.compareTo(WIDE_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } 
               }
             }
             else {
               return;
             }
          }
        });  
        columnTwoLink = new ActionLink(columnTwoLinkLabel) {
          public boolean isVisible(PageState s) {
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 if(layout.length() < 2)
                   return false;
                 else
                   return true;
               } 
             }
            return false;
          }
        };
        columnTwoLink.setIdAttr("columntwolocklink");
        columnTwoLink.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState(); 
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null && layout.length() > 1) {
                 char c = layout.charAt(1);
                 String fragbegin = layout.substring(0,1);
                 String fragend;
                 if(layout.length() == 3)
                   fragend = layout.substring(2,3);
                 else
                   fragend = "";
                 if(c == 'n') {
                   ptab.setLayout(fragbegin + "N" + fragend);
                 } else if(c == 'N') {
                   ptab.setLayout(fragbegin + "n" + fragend);
                 } else if(c == 'w') {
                   ptab.setLayout(fragbegin + "W" + fragend);
                 } else if(c == 'W') {
                   ptab.setLayout(fragbegin + "w" + fragend);
                 }
                 ptab.save();
               }
             }
           }
         });

        Label columnThreeLinkLabel = new Label("Lock/Unlock");
        columnThreeLinkLabel.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             Label t = (Label)e.getTarget();
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 if(layout.length() < 3)
                   return;
                 String frag = layout.substring(2,3);
                 if(frag.compareTo(NARROW_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if (frag.compareTo(NARROW_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } else if(frag.compareTo(WIDE_LOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.unlock_column"));
                 } else if(frag.compareTo(WIDE_UNLOCKED) == 0) {
                   t.setLabel(GlobalizationUtil.
                       globalize("portalserver.ui.admin.lock_column"));
                 } 
               }
             }
             else {
               return;
             }
          }
        });  
        columnThreeLink = new ActionLink(columnThreeLinkLabel) {
          public boolean isVisible(PageState s) {
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null) {
                 if(layout.length() < 3)
                   return false;
                 else
                   return true;
               } 
             }
            return false;
          }
        };
        columnThreeLink.setIdAttr("columnthreelocklink");
        columnThreeLink.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState(); 
             BigDecimal prtlID = (BigDecimal)m_portalIDRL.get(s);
             PortalTab ptab = PortalTab.retrieveTab(prtlID);
             if(ptab != null) {
               String layout = ptab.getLayout();
               if(layout != null && layout.length() > 2) {
                 char c = layout.charAt(2);
                 String fragbegin = layout.substring(0,2);
                 if(c == 'n') {
                   ptab.setLayout(fragbegin + "N");
                 } else if(c == 'N') {
                   ptab.setLayout(fragbegin + "n");
                 } else if(c == 'w') {
                   ptab.setLayout(fragbegin + "W");
                 } else if(c == 'W') {
                   ptab.setLayout(fragbegin + "w");
                 }
                 ptab.save();
               }
             }
           }
         });

        add(columnOneLink); 
        add(columnTwoLink); 
        add(columnThreeLink); 
    }

    public boolean isVisible(PageState s) {
       PortalSite ps = (PortalSite)m_portalsiteRL.get(s);
       if(!ps.isPersonalizable())
           return false;
       else
           return true;
    }
}
