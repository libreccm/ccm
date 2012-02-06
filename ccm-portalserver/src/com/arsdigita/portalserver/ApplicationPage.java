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

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.DimensionalNavbar;

import com.arsdigita.web.Application;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;

import com.arsdigita.util.Assert;





/**
 * ApplicationPage class
 *
 * This is a common page to be used by all applications for consistent
 * styling.
 *
 * @author <a href="mailto:teadams@arsdigita.com">Tracy Adams</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ApplicationPage.java#6 $
 */
public class ApplicationPage extends CWPage {

    /* XXX Have to control links with permissions and
     *      add access control
     */

    private static org.apache.log4j.Logger log =
        org.apache.log4j.Logger.getLogger(ApplicationPage.class.getName());

    protected ApplicationPage() {
        this(true);
    }

    protected ApplicationPage(boolean checkPermission) {
        if (checkPermission) {
            // XXX  Right now, we restrict this page to users with read
            //      access on the current application.
            addRequestListener(new ApplicationAuthenticationListener("read"));
        }
    }

    @Override
    public void lock() {
        buildPage();

        super.lock();
    }

    // Only the PortalPage.lock() should invoke this
    // method, though users of this class may sometimes want to
    // <em>override</em> this method.
    protected final void buildPage() {
        buildTitle();
        buildContextBar();
        buildGlobal(getGlobal());
        buildHeader(getHeader());
        buildBody(getBody());
        buildFooter(getFooter());
    }

    protected void buildTitle() {
        setTitle(new Label(new CurrentApplicationLabelPrinter()));
    }

    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new Link(new CurrentApplicationLinkPrinter()));

        getHeader().add(navbar);
    }

    protected void buildGlobal(Container global) {
        Link link = new Link( new Label(
                        GlobalizationUtil.globalize("cw.workspace.sign_out")),  
                       "/register/logout");

        link.setClassAttr("signoutLink");
        
        getGlobal().add(link);
    }

    protected void buildHeader(Container header) {
        // Nothing yet
    }

    protected void buildBody(Container body) {
        // Nothing by default
    }

    protected void buildFooter(Container footer) {
        // Nothing by default
    }


    //
    // Classes for use in generating dynamic labels and links
    //

    protected class PersonalPortalLinkPrinter implements PrintListener {
        public PersonalPortalLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();

            Party party = Kernel.getContext().getParty();

            // Assert.assertNotNull(party, "Party party");
            Assert.exists(party, "Party party");

            link.setChild(new Label(GlobalizationUtil
                                    .globalize("cw.workspace.personal_workspace")));
            link.setTarget("/personal-portal/" + party.getID() + "/");
        }
    }

    // No need for a PersonalPortalLabelPrinter...yet.

    protected class CurrentApplicationLinkPrinter implements PrintListener {
        public CurrentApplicationLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            PageState pageState = e.getPageState();

            Application app = Application.getCurrentApplication
                (pageState.getRequest());

            Assert.exists(app, "Application app");

            link.setChild(new Label(app.getTitle()));
            link.setTarget(app.getPrimaryURL());
        }
    }

    protected class CurrentApplicationLabelPrinter implements PrintListener {
        public CurrentApplicationLabelPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Label label = (Label) e.getTarget();
            PageState pageState = e.getPageState();

            Application app = Application.getCurrentApplication
                (pageState.getRequest());

            Assert.exists(app, "Application app");

            label.setLabel(app.getTitle());
        }
    }

    protected class ParentApplicationLinkPrinter implements PrintListener {
        public ParentApplicationLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            PageState pageState = e.getPageState();

            Application app = Application.getCurrentApplication
                (pageState.getRequest());

            Assert.exists(app, "Application app");

            Application parent = app.getParentApplication();

            link.setChild(new Label(parent.getTitle()));
            link.setTarget(parent.getPrimaryURL());
        }
    }
}
