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
package com.arsdigita.faq.ui;


import com.arsdigita.faq.util.GlobalizationUtil; 

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.web.Application;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.xml.Element;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <p>BasePage class</p>
 *
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */

public class FaqBasePage extends Page {

    private final Container m_global;
    private final Container m_header;
    private final Container m_body;
    private final Container m_footer;

    private IntegerParameter m_selected = new IntegerParameter("m");

    public static final String FAQ_GLOBAL_ELEMENT = "faq:global";
    public static final String FAQ_HEADER_ELEMENT = "faq:header";
    public static final String FAQ_BODY_ELEMENT = "faq:body";
    public static final String FAQ_FOOTER_ELEMENT = "faq:footer";
    public static final String FAQ_XML_NS = "http://www.redhat.com/faq/1.0";

    private static final Logger s_log = Logger.getLogger(FaqBasePage.class);

    /*
     * There are 2 views: user and admin.
     * m_view determines which context bar and
     * view link to show.
     */
    private String m_view;

    private Link m_viewLink;

    private boolean CHECK_PERMISSION = true;

    /**
     * Default Constructor
     */
    public FaqBasePage() {
        this(null);
    }

    /**
     * @param view A String that specifies which application view to
     * show.  Valid values: "user", "admin" and "null".  If view is
     * "null", there will be no right-hand navigation link.  Note -
     * We've decided not to have the right-hand navigation link at
     * all. Instead, you should create tabs.  So, once the
     * applications are migrated, view will always be null and we can
     * remove view altogether.
     */
    public FaqBasePage(String view) {
        super(new Label(), new SimpleContainer());

        setClassAttr("faq");

        m_panel = new Panel();

        addGlobalStateParam(m_selected);

        m_global = new SimpleContainer
            (FAQ_GLOBAL_ELEMENT, FAQ_XML_NS);
        m_header = new SimpleContainer
            (FAQ_HEADER_ELEMENT, FAQ_XML_NS);
        m_body = new SimpleContainer
            (FAQ_BODY_ELEMENT, FAQ_XML_NS);
        m_footer = new SimpleContainer
            (FAQ_FOOTER_ELEMENT, FAQ_XML_NS);

        super.add(m_global);
        super.add(m_header);
        super.add(m_body);
        super.add(m_footer);


        m_view = view;

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
        class ApplicationAdminLabelPrinter implements PrintListener {
            public void prepare(PrintEvent e) {
                Label targetLabel = (Label)e.getTarget();
                PageState pageState = e.getPageState();

                Application application = Application.getCurrentApplication
                    (pageState.getRequest());

                Assert.exists(application, "application");

                targetLabel.setLabel
                    (application.getTitle() + " Administration");
            }
        }

        if (m_view != null && m_view.equals("admin")) {
            setTitle(new Label(new ApplicationAdminLabelPrinter()));
        } else {
            setTitle(new Label(new CurrentApplicationLabelPrinter()));
        }
    }

    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new Link(new ParentApplicationLinkPrinter()));

        navbar.add(new Link(new CurrentApplicationLinkPrinter()));

        getHeader().add(navbar);
    }

    protected void buildGlobal(Container global) {
        Link link = new Link( new Label(GlobalizationUtil
                                        .globalize("cw.workspace.sign_out")),
                                        "/register/logout");

        link.setClassAttr("signoutLink");

        getGlobal().add(link);
    }

    protected void buildHeader(Container header) {
        if (m_view != null) {
            if (m_view.equals("user")) {
                m_viewLink = new Link( new Label(GlobalizationUtil
                                                 .globalize("cw.faq.ui.admin_view")), 
                                                 "./admin/index.jsp") {
                    @Override
                    public boolean isVisible(PageState ps) {
                        return userIsAdmin(ps);
                    }};
            } else if (m_view.equals("admin")) {
                m_viewLink = new Link( new Label(GlobalizationUtil
                                                 .globalize("cw.faq.ui.user_view")),  
                                                 "../index.jsp");
            }
        }

        if (m_viewLink != null) {
            m_viewLink.setClassAttr("portalControl");

            header.add(m_viewLink);
        }
    }

    protected void buildBody(Container body) {
        // Nothing by default
    }

    protected void buildFooter(Container footer) {
        // Nothing by default
    }



    private class Panel extends SimpleContainer {
        @Override
        public void generateXML(PageState ps, Element p) {
            Component selected = getSelected(ps);
            if (selected == null) {
                super.generateXML(ps, p);
            } else {
                SimpleContainer fakeBody =
                    new SimpleContainer(FAQ_BODY_ELEMENT,
                                        FAQ_XML_NS);
                fakeBody.add(selected);

                Element parent = generateParent(p);

                m_header.generateXML(ps, parent);
                fakeBody.generateXML(ps, parent);
                m_footer.generateXML(ps, parent);
            }
        }
    }


    /**
     * Makes the given component the only visible component between
     * the header and footer of this page.
     */
    public void goModal(PageState ps, Component c) {
        Component old = getSelected(ps);
        if (old != null) {
            old.setVisible(ps, false);
        }
        c.setVisible(ps, true);
        setSelected(ps, c);
    }

  private Component getSelected(PageState ps) {
        Integer stateIndex = (Integer) ps.getValue(m_selected);
        Component c = null;
        if (stateIndex != null) {
            c = getComponent(stateIndex.intValue());
        }

        return c;
    }

    private void setSelected(PageState ps, Component c) {
        if (c == null) {
            ps.setValue(m_selected, null);
        } else {
            ps.setValue(m_selected, new Integer(stateIndex(c)));
        }
    }

    /**
     * Clears the currently selected modal component if it has been set.
     */
    public void goUnmodal(PageState ps) {
        Component old = getSelected(ps);
        if (old != null) {
            old.setVisible(ps, false);
        }
        setSelected(ps, null);
    }

    private boolean userIsAdmin(PageState ps) {
        PermissionDescriptor permDescriptor =
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     Application.getCurrentApplication(ps.getRequest()),
                                     Kernel.getContext().getParty());
        return PermissionService.checkPermission(permDescriptor);
    }

    /**
     * Adds a component to the body.
     *
     * @param pc the component to be added
     */
    public void add(Component pc) {
        Assert.isUnlocked(this);
        m_body.add(pc);
    }

    public Container getGlobal() {
        return m_global;
    }

    public Container getHeader() {
        return m_header;
    }

    public Container getBody() {
        return m_body;
    }

    public Container getFooter() {
        return m_footer;
    }

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

    /**
     *
     */
    protected class ParentApplicationLinkPrinter implements PrintListener {

        /**
         * Empty Default Constructor
         */
        public ParentApplicationLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            PageState pageState = e.getPageState();

            Application app = Application
                             .getCurrentApplication(pageState.getRequest());

            Assert.exists(app, "Application app");
            Application parent = app.getParentApplication();

            if (parent != null) {
                link.setChild(new Label(parent.getTitle()));
                link.setTarget(parent.getPath());
            }
        }
    }

    /**
     * 
     */
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


}
