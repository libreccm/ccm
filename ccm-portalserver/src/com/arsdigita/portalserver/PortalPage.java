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
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;

import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.web.RedirectSignal;

import com.arsdigita.util.Assert;

import com.arsdigita.portalserver.personal.PersonalPortal;

import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;


/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PortalPage.java  pboy $
 */
public class PortalPage extends ApplicationPage {

    protected PortalPage() {
        super();
        this.addRequestListener(new RequestListener() {
           public void pageRequested(RequestEvent e) {
               PageState s = e.getPageState();
               PortalSite psite = 
                     PortalSite.getCurrentPortalSite(s.getRequest());
                if(psite == null) 
                  return;

                if(!psite.isPersonalizable())
                  return;

                User user = Web.getContext().getUser();
                if(user == null)
                  return;  

                PortalSiteCollection psc = psite.getAllChildPortalSites();
                PortalSite p = null; 
                String path;
                String fragment;
                boolean found = false;
                while(psc.next()) {
                  p = psc.getPortalSite();
                  path = p.getPath();
                  fragment = path.substring(path.lastIndexOf("/"));
                  if(fragment.compareTo("/U-" + user.getID().toString()) == 0)
                    {
                      found = true;
                      break;
                  }                       
                }
               if(found) {
                throw new RedirectSignal(URL.there(s.getRequest(),
                                                   p.getPath()),false);
               } 
             }
          });
    }

    //
    // Classes for use in generating dynamic labels and links
    //

    @Override
    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new LoggedInLinkWrapper(new PersonalPortalLinkPrinter()));

        // This link will not show up if the current portal is a
        // personal portal.
        Link current = new Link(new CurrentPortalLinkPrinter()) {
                @Override
                public boolean isVisible(PageState state) {
                    PortalSite psite = PortalSite.getCurrentPortalSite
                        (state.getRequest());

                    return !(psite instanceof PersonalPortal);
                }
            };
        navbar.add(current);

        navbar.add(new Link(new CurrentApplicationLinkPrinter()));

        getHeader().add(navbar);
    }

    @Override
    protected void buildGlobal(Container global) {
        super.buildGlobal(global);

        //There are four requirements for the customize link
        //to be visible:
        // 0) The portal must be customizable
        // 1) This must not be an admin page
        // 2) A user must be logged in
        // 3) The user is allowed to customize this portal
        // 4) The user does not already have a custom version of this portal
        ActionLink personalizable = new ActionLink("Personalize This Portal!") {
            @Override
            public boolean isVisible(PageState s) {
                PortalSite psite = PortalSite.getCurrentPortalSite
                    (s.getRequest());
                if(psite == null) 
                  return false;

                if(!psite.isPersonalizable())
                  return false;  //failed req #0

                String url = Web.getContext().getRequestURL().getRequestURI();
                if(url.endsWith("admin/"))
                   return false;  //failed req #1
            
                User user = Web.getContext().getUser();
                if(user == null)
                  return false;  //failed #2

                //xxx-Need test for req #3 here

                PortalSiteCollection psc = psite.getAllChildPortalSites();
                PortalSite p; 
                String path;
                String fragment;
                while(psc.next()) {
                  p = psc.getPortalSite();
                  path = p.getPath();
                  fragment = path.substring(path.lastIndexOf("/"));
                  if(fragment.compareTo("/U-" + user.getID().toString()) == 0)
                    return false; //failed #4
                }
                return true;
             }
        };
        personalizable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PageState s = e.getPageState();
                PortalSite psite = PortalSite.getCurrentPortalSite
                                 (s.getRequest());
                User user = Web.getContext().getUser();
                PortalSite newsite = 
                       PortalSite.createSubPortal(psite, user); 
                throw new RedirectSignal(URL.there(s.getRequest(),
                                                   newsite.getPath()),true);
            }
        });

        personalizable.setIdAttr("personalize_link");
        global.add(personalizable);
                
    }

    protected class PersonalPortalLinkPrinter implements PrintListener {
        public PersonalPortalLinkPrinter() {}

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();

            Party party = Kernel.getContext().getParty();
            
            if ( party != null ) {
                link.setChild(new Label(GlobalizationUtil.globalize(
                                  "cw.workspace.personal_workspace")));
                link.setTarget("/personal-portal/" + party.getID() + "/");
            }
        }
    }

    // No need for a PersonalPortalLabelPrinter...yet.

    protected class CurrentPortalLinkPrinter implements PrintListener {
        public CurrentPortalLinkPrinter() {
            /* Empty */
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            PageState state = e.getPageState();

            PortalSite psite = 
             PortalSite.getCurrentPortalSite(state.getRequest());

            if ( psite == null ) {
                link.setChild(new Label(GlobalizationUtil.globalize(
                                  "cw.workspace.ecm_administration")));
                link.setTarget("/portal-admin/");
            } else {
                link.setChild(new Label(psite.getTitle()));
                link.setTarget(psite.getPrimaryURL());
            }
        }
    }

    protected class CurrentPortalLabelPrinter implements PrintListener {
        public CurrentPortalLabelPrinter() {
            /* Empty */
        }

        public void prepare(PrintEvent e) {
            Label label = (Label) e.getTarget();
            PageState pageState = e.getPageState();

            PortalSite psite = PortalSite.getCurrentPortalSite
                (pageState.getRequest());

            // Assert.assertNotNull(psite, "PortalSite psite");
            Assert.exists(psite, "PortalSite psite") ;

            label.setLabel(psite.getTitle());
        }
    }

    @Override
    public void generateXML(PageState state, Document parent) {
        super.generateXML(state,parent);

        this.addStyleBlock(state, parent);
    }
   
    public void addStyleBlock(PageState state, Document parent) {

        Theme theme = PortalSite.getCurrentPortalSite(state.getRequest()).getTheme();
        if(theme == null)
        {
          ThemeCollection themes = Theme.retrieveAllThemes();
          while(themes.next())
          {
            theme = themes.getTheme();
            if(theme.getName().equals("Red Hat"))
              break;
          }  
          themes.close();
        }
  
        if(theme == null)
          return;

        StringBuffer buffer = theme.buildStyleBlock();

        Element rootElement = parent.getRootElement();

        Element styleBlock = rootElement.newChildElement("portalserver:styleblock",
                                    "http://www.redhat.com/portalserver/1.0");
    
        styleBlock.setCDATASection(buffer.toString());
    }
}
