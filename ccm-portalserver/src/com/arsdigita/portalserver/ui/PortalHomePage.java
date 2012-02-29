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
package com.arsdigita.portalserver.ui;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.LoggedInLinkWrapper;
import com.arsdigita.portalserver.PortalPage;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portalserver.PortalTabCollection;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.portal.Portal;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portal.DefaultPortalModel;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 * @version $Id: PortalHomePage.java#7 $  $DateTime: 2004/08/17 23:19:25 $
 */
public class PortalHomePage extends PortalPage {

    private static final Logger s_log = Logger.getLogger(PortalHomePage.class);

    private static final Cookie[] EMPTY_COOKIES = {};

    public static final String PORTAL_XML_NS =
        "http://www.arsdigita.com/portalserver/1.0";

    List m_tabs;

    private RequestLocal m_hasAdmin = new RequestLocal() {
            public Object initialValue(PageState ps) {
                PortalSite psite = 
                            PortalSite.getCurrentPortalSite(ps.getRequest());
                if (psite.checkPrivilege(PrivilegeDescriptor.ADMIN)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        };

    /**
     * Constructor 
     */
    public PortalHomePage() {
        super();

        addRequestListener(new PortalRequestListener());
        lock();
    }

    /**
     * 
     * @param ps
     * @return 
     */
    private boolean hasAdmin(PageState ps) {
        return m_hasAdmin.get(ps).equals(Boolean.TRUE);
    }

    /**
     * 
     * @param pageState
     * @return     (Can return null.)
     */
    protected PortalSite getPortalSite(PageState pageState) {
        return PortalSite.getCurrentPortalSite(pageState.getRequest());
    }

    /**
     * 
     */
    @Override
    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new LoggedInLinkWrapper(new PersonalPortalLinkPrinter()));
        navbar.add(new Label(new CurrentPortalLabelPrinter()));

        getHeader().add(navbar);
    }

    /**
     * 
     * @param header 
     */
    @Override
    protected void buildHeader(Container header) {
        Link adminLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.configure_workspace")),  "./admin") {
            @Override
                public boolean isVisible(PageState ps) {
                    return hasAdmin(ps);
                }
            };
        adminLink.setClassAttr("portalControl");

        header.add(adminLink);

        class SearchComponent extends SimpleContainer {
            public void generateXML(PageState state, Element parent) {
/*XXXjbp - *************************************************
 ****This needs to be modified to use core search***********

                Element elem = new Element
                    ("km:search", KmConstants.KM_XML_NS);

                PortalSite portalsite = PortalSite.getCurrentPortalSite
                    (state.getRequest());

                ApplicationCollection apps =
                    portalsite.getChildApplicationsForType
                    (KnowledgeManager.BASE_DATA_OBJECT_TYPE);

                if (apps.next()) {
                    elem.addAttribute
                        ("url", apps.getPrimaryURL() + "search.jsp");
                } else {
                    elem.addAttribute
                        ("url", "#");
                }

                apps.close();

                exportAttributes(elem);

                parent.addContent(elem);
*/
            }
        }

        header.add(new SearchComponent());
    }

    /**
     * 
     * @param body 
     */
    @Override
    protected void buildBody(Container body) {
        ListModelBuilder lmb = new ListModelBuilder() {
                boolean m_isLocked;

                public ListModel makeModel(List l, PageState pageState) {
                    return new TabsListModel(pageState);
                }

                public void lock() {
                    m_isLocked = true;
                }

                public boolean isLocked() {
                    return m_isLocked;
                }
            };

        m_tabs = new List(lmb);

        m_tabs.setClassAttr("portalTabs");

        m_tabs.setCellRenderer(new TabsListCellRenderer());

        m_tabs.addChangeListener(new CookieChangeListener());

        body.add(m_tabs);
    }

    /*
      protected void buildContextBar() {
      DimensionalNavbar navbar = new DimensionalNavbar();

      // To generate a link to the user's personal portal.
      class PWLinkPrintListener implements PrintListener {
      public void prepare(PrintEvent e) {
      Link link = (Link) e.getTarget();
      PageState pageState = e.getPageState();

      KernelRequestContext krc =
      KernelHelper.getKernelRequestContext
      (pageState.getRequest());

      UserContext uc = krc.getUserContext();

      link.setTarget("/personal-portal/" + uc.getUserID() + "/");
      }
      }

      navbar.add(new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.personal_workspace")),  new PWLinkPrintListener()));
      navbar.add(new Label(new TitlePrintListener()));
      navbar.setClassAttr("portalNavbar");

      setContextBar(navbar);
      }
    */

    private class PortalRequestListener implements RequestListener {


        public void pageRequested(RequestEvent e) {

            PageState ps = e.getPageState();
            boolean notFound = true;
            PortalSite portalsite = getPortalSite(ps);

        //  Assert.assertNotNull(portalsite, "workspace");
            Assert.exists(portalsite, "workspace");

            if (!portalsite.isReady()) {
                // ad_script_abort
            }

            //if no tab is selected, check cookies
            if (!m_tabs.isSelected(ps)) {
                String prtlID = portalsite.getID().toString();

                // form cookie name val by concatenating 'prtl_' + portal ID
                String cookiePRTLName = "prtl_" + prtlID;

                HttpServletRequest httpRequest = ps.getRequest();

                // get cookie array
                Cookie[] cookies = getCookies(httpRequest);

                // If name val exists, get value
                String result = getCookieValue(cookies, cookiePRTLName);

                // check if this tab ID is still present in this portal
                TabsListModel tlm = new TabsListModel(ps);
                if (result != null) {
                    while (tlm.next()) {
                        String tmpstr = tlm.getKey();
                        if (result.equals(tmpstr)) {
                            m_tabs.setSelectedKey(ps,tmpstr);
                            notFound = false;
                            break;
                        }
                    }
                    if (notFound) {
                        //use the first tab in the collection as the
                        //one to display...
                        tlm.reset();

                        if (tlm.next()) {
                            String key = tlm.getKey();
                            m_tabs.setSelectedKey(ps,key);
                        }
                    }
                } else {
                    if (tlm.next()) {
                        String key = tlm.getKey();
                        m_tabs.setSelectedKey(ps,key);
                    }
                }

                tlm.close();
            }
        }

        // Wraps getCookies on request, since it doesn't follow the
        // sane java convention of returning an empty array,
        // but instead returns null;
        private Cookie[] getCookies(HttpServletRequest httpRequest) {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies == null) {
                return EMPTY_COOKIES;
            }
            return cookies;
        }

        public String getCookieValue(Cookie[] cookies, String cookieName) {
            for (int i=0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName())) {
                    return (cookie.getValue());
                }
            }
            return null;
        }

    }

    private class TabsListModel implements ListModel {
        PortalTabCollection ptabs = null;
        PortalTab ptab = null;

        TabsListModel(PageState pageState) {
            PortalSite psite = getPortalSite(pageState);
            ptabs = psite.getTabsForPortalSite();
        }

        public boolean next() {
            return ptabs.next();
        }

        public Object getElement() {
            return ptabs.getPortalTab();
        }

        public String getKey() {
            return ptabs.getID().toString();
        }

        public void close() {
            ptabs.close();
        }

        public void reset() {
            ptabs.rewind();
        }
    } //end TabsListModel

    class TabsListCellRenderer implements ListCellRenderer {
        public Component getComponent
            (List list, PageState pageState, Object value, String key,
             int index, boolean isSelected)
        {
            PortalTab ptab = (PortalTab) value;

            if (isSelected) {
                String layout = ptab.getLayout();
                com.arsdigita.bebop.portal.Portal port =
                    new com.arsdigita.bebop.portal.Portal
                    (new PortalTabsModelBuilder(ptab));
                port.setStyleAttr(layout.toUpperCase());
                return port;
            } else {
                return new ControlLink(ptab.getTitle());
            }
        }
    } //end cell renderer

    class PortalTabsModelBuilder implements PortalModelBuilder {
        PortalTab m_ptab;

        PortalTabsModelBuilder(PortalTab ptab) {
            m_ptab = ptab;
        }

        public PortalModel buildModel(PageState pageState) {
            //Change to defaultTab model...
            return new DefaultPortalModel(m_ptab);
        }
    }

    private class CookieChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            PageState ps = e.getPageState();
            PortalSite psite = getPortalSite(ps);

         // Assert.assertNotNull(psite, "workspace");
            Assert.exists(psite, "workspace");

            HttpServletResponse response = ps.getResponse();
            String cookieNameString = "prtl_" + psite.getID().toString();
            String tabName = (String)m_tabs.getSelectedKey(ps);
            Cookie cookie = new Cookie(cookieNameString,tabName);
            cookie.setMaxAge(36000);
            cookie.setComment(
                              "This cookie returns you to the tab you " +
                              "had open when you last visited this portal."
                              );
            response.addCookie(cookie);
        }
    }
}
