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

package com.arsdigita.london.portal.ui.admin;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Role;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.ui.PortalConstants;

public class AdminPane extends SimpleContainer {

	private ApplicationSelectionModel m_app;

	private CategoryComponent m_catComponent;

	private DeleteApplicationComponent m_deleteApplicationComponent;

	private static final Logger s_log = Logger.getLogger(AdminPane.class);

	public AdminPane() {
		setTag("portal:admin");
		setNamespace(PortalConstants.PORTAL_XML_NS);

		m_app = new ApplicationSelectionModel("application", true);

		m_catComponent = new CategoryComponent(m_app);
		m_catComponent.setIdAttr("categoryComponent");
		add(m_catComponent);

		m_deleteApplicationComponent = new DeleteApplicationComponent(m_app,
				m_app.getDefaultApplication().getApplicationType());
		m_deleteApplicationComponent.setIdAttr("deleteComponent");
		add(m_deleteApplicationComponent);

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
				"portal:memberPicker", PortalConstants.PORTAL_XML_NS));
		form.add(new GroupMemberPicker() {
			public Group getGroup(PageState state) {
				Workspace workspace = (Workspace) Kernel.getContext()
						.getResource();
				return (Group) workspace.getParty();
			}
		});
		form.setIdAttr("memberUserPicker");
		add(form);

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
				"portal:adminPicker", PortalConstants.PORTAL_XML_NS));
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
