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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.PackageInstanceCollection;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.SiteNodeCollection;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.UI;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;


import org.apache.log4j.Logger;

/**
 * A reusable Bebop component to display the primary attributes of the
 * currently logged in user. Users can extend this class to provide
 * information about another user by overriding the {@link
 * UserInfo#register(Page)} and {@link UserInfo#getUser(PageState)}
 * methods.
 *
 *
 * @author Michael Bryzek
 * @author Roger Hsueh
 * @author Sameer Ajmani
 * @since 2001-06-01
 * @version 1.0
 * @version $Id: UserInfo.java 287 2005-02-22 00:29:02Z sskracic $
 *
 **/
public class UserInfo extends SimpleContainer {

    private static final Logger s_log =
        Logger.getLogger(UserInfo.class.getName());

    /** m_contentCenters holds a list of content centers that exist
        on this installation */
    private List m_contentCenters;
    /** m_centerSiteNodes contains a mapping of content centers to the
        site nodes where they are mounted.  */
    private HashMap m_centerSiteNodes;

    private UserAuthenticationListener m_listener =
                new UserAuthenticationListener();

    /** 
     * Constructor. 
     */
    public UserInfo() {
        // add list of links
        ListPanel list = new ListPanel(false);
        list.add(new DynamicLink("login.userInfo.logoutLink",
                                 UI.getLogoutPageURL()));
        list.add(new DynamicLink("login.userInfo.editProfileLink",
                                 UI.getEditUserProfilePageURL()));
        list.add(new DynamicLink("login.userInfo.changePasswordLink",
                                 UI.getRecoverPasswordPageURL()));
        add(list);
        // add user info text
        add(new SimpleComponent() {
            @Override
                public void generateXML(PageState state, Element parent) {
                    if (!isLoggedIn(state)) {
                        s_log.debug("user is not logged in, so no XML generated");
                        return;
                    }
                    User user = getUser(state);

                    Element userElement = new Element
                        ("subsite:userInfo", SubsiteDispatcher.SUBSITE_NS_URI);

                    if (m_contentCenters == null) {
                        m_contentCenters = new ArrayList();
                        m_centerSiteNodes = new HashMap();

       //  XXX has to refactored!
       //  used old style packageType to retrieve a list of installed
       //  content-centers. Odd, there may exist only one content-center per
       //  installation!
       //  Currently (version 6.6.0 2011-02-06) content-center = workspace
       //  exists as legacy application only, not as legacy compatible application!
                        // retrieve all packages of type content-center
                        // works because there may be only one.
                        DataCollection dc = SessionManager.getSession().retrieve
                            (PackageType.BASE_DATA_OBJECT_TYPE);
                        dc.addEqualsFilter("packageKey", "content-center");

                        if (dc.next()) {
                            // works because there may be only one entry of
                            // package type content-center
                            PackageType pt = new PackageType(dc.getDataObject());
                            dc.close();

                            PackageInstanceCollection collection = pt.getInstances();
                            while (collection.next()) {
                                PackageInstance instance = collection.getPackageInstance();
                                instance.disconnect();
                                m_contentCenters.add(instance);
                                SiteNodeCollection nodes = instance.getMountPoints();
                                ArrayList list = new ArrayList();
                                while (nodes.next()) {
                                    SiteNode sn = nodes.getSiteNode();
                                    sn.disconnect();
                                    list.add(sn);
                                }
                                m_centerSiteNodes.put(instance, list);
                            }
                        }
                    }

                    if (m_contentCenters.size() > 0) {
                        Element center;
                        Element nodeURL;
                        List nodes;
                        Iterator nodesIterator;
                        Element contentCenters = userElement.newChildElement
                            ("subsite:contentCenters", SubsiteDispatcher.SUBSITE_NS_URI);
                        Iterator centers = m_contentCenters.iterator();
                        while (centers.hasNext()) {
                            PackageInstance instance = (PackageInstance) centers.next();
                            center = contentCenters.newChildElement
                                ("subsite:center", SubsiteDispatcher.SUBSITE_NS_URI);
                            center.addAttribute("name", instance.getName());
                            nodes = (List)m_centerSiteNodes.get(instance);
                            if (nodes != null) {
                                nodesIterator = nodes.iterator();
                                while (nodesIterator.hasNext()) {
                                    SiteNode node = (SiteNode) nodesIterator.next();
                                    nodeURL = center.newChildElement
                                        ("subsite:url", SubsiteDispatcher.SUBSITE_NS_URI);

                                    final URL url = URL.there
                                        (state.getRequest(), node.getURL());

                                    nodeURL.setText(url.toString());
                                }
                            }
                        }
                    }

                    userElement.addAttribute("id",
                                             user.getID().toString());
                    userElement.addAttribute
                        ("email",
                         user.getPrimaryEmail().getEmailAddress());
                    userElement.addAttribute("name", user.getName());
                    userElement.addAttribute("screenName",
                                             user.getScreenName());
                    userElement.addAttribute("URI", user.getURI());

                    parent.addContent(userElement);
                }
            });
    }

    /**
     * Adds a request listener to the page to ensure that the user is logged
     * in.  Subclasses should override this method if they do not require
     * users to be logged in.  This method may be changed as we find more
     * examples of how people are using this class.
     *
     * @pre p != null
     **/
    public void register(Page p) {
        super.register(p);
        p.addRequestListener(m_listener);
    }

    /**
     * @return true if the user is logged in
     **/
    protected boolean isLoggedIn(PageState state) {
        return m_listener.isLoggedIn(state);
    }

    /**
     * @return the User object for which we are generating information
     *
     * @throws IllegalStateException if user is not logged in.  Call
     * isLoggedIn(state) to check for this case.
     *
     * @pre state != null
     * @post return != null
     **/
    protected User getUser(PageState state) {
        if (!isLoggedIn(state)) {
            throw new IllegalStateException("user is not logged in");
        }
        return m_listener.getUser(state);
    }
}
