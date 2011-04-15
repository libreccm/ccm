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
package com.arsdigita.portalserver.test;

import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.*;
import com.arsdigita.web.*;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.Role;


import org.apache.log4j.Logger;

public class TestPortalSetup {


    private static Logger s_log = Logger.getLogger(TestPortalSetup.class);
    private PortalSite m_portalsite;
    private Party m_member;
    private Party m_editor;
    private Party m_manager;

    private Role m_members;
    private Role m_editors;
    private Role m_managers;


    public void setupPortal() {

        s_log.warn("Calling setupPortal");
        if( portalDoesNotExist() ) {
            s_log.warn("Creating test portal titled: " + TestApps.PORTAL);
            setupDefaultPortal();
            setupDefaultRoles();
            setupDefaultUsers();
        }
    }


    private boolean  portalDoesNotExist() {
        s_log.warn("Checking for portals!");
        PortalSiteCollection psites = PortalSite.retrieveAllPortalSites();
        boolean doesNotExist = true;
        int portalCount = 0;
        try {
            s_log.warn("Starting loop!");
            while(psites.next() && doesNotExist) {
                String title = psites.getTitle();
                s_log.warn("Portal: " + title);
                doesNotExist = !title.equals(TestApps.PORTAL);
                portalCount++;
            }
        } finally {
            psites.close();
        }

        s_log.warn("Portal count: " + portalCount);
        return doesNotExist;
    }
    /**
     *  Creates a new role for a portalsite with Read-Only access.
     *
     * @param portalsite The portalsite to add the role to.
     * @param roleName The name of the role.
     * @param assigneeTitle The tile of people assigned to the role.
     * @param description A description of the role.
     *
     * @return The newly created role.
     */
    private Role createRole(PortalSite portalsite,
                            String roleName,
                            String assigneeTitle,
                            String description) {

        Role newRole = createRole(portalsite,
                                  roleName,
                                  assigneeTitle,
                                  description,
                                  PrivilegeDescriptor.READ);

        return newRole;

    }

    /**
     *  Creates a new role for a portalsite.
     *
     * @param portalsite The portalsite to add the role to.
     * @param roleName The name of the role.
     * @param assigneeTitle The tile of people assigned to the role.
     * @param description A description of the role.
     * @param privilege The privilege to grant this role on the workspace.
     *
     * @return The newly created role.
     */
    private Role createRole(PortalSite portalsite,
                            String roleName,
                            String assigneeTitle,
                            String description,
                            PrivilegeDescriptor privilege) {
        Role newRole =
            Role.createRole(portalsite,
                            roleName, assigneeTitle, description);
        newRole.save();


        PermissionService.grantPermission(
            new PermissionDescriptor(PrivilegeDescriptor.CREATE, portalsite,
                                                                   newRole));

        PermissionService.grantPermission(
                                          new PermissionDescriptor(privilege,
                                                                   portalsite,
                                                                   newRole));

        return newRole;

    }


    private void setupDefaultPortal() {
        m_portalsite = PortalSite.createPortalSite(TestApps.PORTAL, "test", null);
        m_portalsite.save();

        // By default, add some portlets.
        PortalTab mainTab = PortalTab.createTab("Main");

        Portlet portlet = Portlet.createPortlet
            (ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE, m_portalsite);
        mainTab.addPortlet(portlet, 1);

        portlet = Portlet.createPortlet
            (PortalNavigatorPortlet.BASE_DATA_OBJECT_TYPE, m_portalsite);
        mainTab.addPortlet(portlet, 1);

        portlet = Portlet.createPortlet
            (PortalSummaryPortlet.BASE_DATA_OBJECT_TYPE, m_portalsite);
        mainTab.addPortlet(portlet, 1);


        mainTab.setPortalSite(m_portalsite);
        mainTab.save();
        m_portalsite.addPortalTab(mainTab);
        m_portalsite.save();

    }

    private Application addApplication(String applicationObjectType, String urlName, String title) {
        Application app = Application.createApplication(applicationObjectType, urlName, title, m_portalsite);
        app.save();

        return app;
    }

    private void setupDefaultUsers() {
        m_member = createUser("Joe", "User");
        m_editor = createUser("Joe", "Editor");

        m_manager = createUser("Joe", "Manager");

        m_portalsite.addParticipant(m_member);
        m_portalsite.addParticipant(m_editor);
        m_portalsite.addParticipant(m_manager);

        m_portalsite.save();

        m_members.addMemberOrSubgroup(m_member);
        m_members.save();
        m_editors.addMemberOrSubgroup(m_editor);
        m_editors.save();
        m_managers.addMemberOrSubgroup(m_manager);
        m_managers.save();

    }

    private User createUser(String firstName, String lastName) {
        String firstLast = firstName.trim().toLowerCase() + lastName.trim().toLowerCase();
        EmailAddress email = new EmailAddress(firstLast + "@" + TestApps.DEFAULT_DOMAIN);
        User user = UserFactory.newUser(email, firstName, lastName, firstLast, firstLast, firstLast, null, null, null);
        user.save();
        return user;

    }
    private void setupDefaultRoles() {

        m_members = m_portalsite.getMemberRole();

        m_editors = createRole(m_portalsite, "Editors", "Editor", "", PrivilegeDescriptor.EDIT);
        m_managers = createRole(m_portalsite, "Managers", "Manager", "", PrivilegeDescriptor.ADMIN);


    }


}
