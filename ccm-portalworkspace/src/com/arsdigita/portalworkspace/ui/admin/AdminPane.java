/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Role;
import com.arsdigita.portalworkspace.ui.sitemap.ApplicationSelectionModel;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;

import org.apache.log4j.Logger;



/**
 * Entry page for PortalWorkspace administration. 
 * 
 * Invocation by (web)/templates/ccm-portalworkspace/admin/index.jsp
 *
 * Provides a list of participants (administrators and members) and functionaliy
 * to add and remove members, provides a way to assign a category for use as an
 * index page in navigation, and provides a link to delete this portal instance
 * at all.
 */
public class AdminPane extends SimpleContainer {

	private static final Logger s_log = Logger.getLogger(AdminPane.class);

	private ApplicationSelectionModel m_app;

	private CategoryComponent m_catComponent;

	private DeleteApplicationComponent m_deleteApplicationComponent;

	/**
     * 
     */
    public AdminPane() {

        setTag("portal:admin");
		setNamespace(WorkspacePage.PORTAL_XML_NS);

		m_app = new ApplicationSelectionModel("application", true);


        /* Add component to select a Navigation Category for this portal    */
        m_catComponent = new CategoryComponent(m_app);
		m_catComponent.setIdAttr("categoryComponent");
		add(m_catComponent);


        /* Add component "Extrem Action": Delete this portal                 */
		m_deleteApplicationComponent = new DeleteApplicationComponent(m_app,
				m_app.getDefaultApplication().getApplicationType());
		m_deleteApplicationComponent.setIdAttr("deleteComponent");
		add(m_deleteApplicationComponent);


        /* Add component to manage Members group members for this portal     */
		GroupMemberDisplay members = new GroupMemberDisplay() {
			public Group getGroup(PageState state) {
				Workspace workspace = (Workspace) Kernel.getContext()
						.getResource();
				return (Group) workspace.getParty();
			}
		};
		members.setIdAttr("memberDisplay");
		add(members);

		Form form = new Form("userPicker", new SimpleContainer(
				"portal:memberPicker", WorkspacePage.PORTAL_XML_NS));
		form.add(new GroupMemberPicker() {
			public Group getGroup(PageState state) {
				Workspace workspace = (Workspace) Kernel.getContext()
						.getResource();
				return (Group) workspace.getParty();
			}
		});
		form.setIdAttr("memberUserPicker");
		add(form);


        /* Add component to manage Admins group members for this portal       */
		GroupMemberDisplay admins = new GroupMemberDisplay() {
			public Group getGroup(PageState state) {
				Workspace workspace = (Workspace) Kernel.getContext()
						.getResource();
				Group members = ((Group) workspace.getParty());
				Role admins = members.getRole("Administrators");
				if (admins == null) {
					admins = members.createRole("Administrators");
					admins.save();
				}
				return admins.getGroup();
			}
		};
		admins.setIdAttr("adminDisplay");
		add(admins);

		Form adminForm = new Form("adminPicker", new SimpleContainer(
				"portal:adminPicker", WorkspacePage.PORTAL_XML_NS));
		adminForm.add(new GroupMemberPicker() {
			public Group getGroup(PageState state) {
				Workspace workspace = (Workspace) Kernel.getContext()
						.getResource();
				Group members = ((Group) workspace.getParty());
				Role admins = members.getRole("Administrators");
				if (admins == null) {
					admins = members.createRole("Administrators");
					admins.save();
				}
				return admins.getGroup();
			}
		});
		adminForm.setIdAttr("adminUserPicker");
		add(adminForm);
	}
}
