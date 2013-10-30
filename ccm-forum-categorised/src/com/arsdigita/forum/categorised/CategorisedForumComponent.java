/*
 * Copyright (C) 2006 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.categorised;

// import javax.servlet.ServletException;

import com.arsdigita.bebop.PageState;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.AForumUserCompactView;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.xml.Element;

/**
 *
 *
 *
 * @author Chris Gilbert
 * @version $Id: $
 */
public class CategorisedForumComponent extends AForumUserCompactView {

	public static final String MODE_CATEGORIES = "categories";

	private AssignCategoryView m_categoryView;

	/**
	 * 
	 */
	public CategorisedForumComponent() {
		super();
		m_categoryView = new AssignCategoryView();
		add(m_categoryView);

	}

	protected void setVisible(
		PageState state,
		Party party,
		Forum forum,
		String mode) {

		PermissionDescriptor forumAdmin =
			new PermissionDescriptor(PrivilegeDescriptor.ADMIN, forum, party);

		if (MODE_CATEGORIES.equals(mode)) {
			if (party == null) {
				UserContext.redirectToLoginPage(state.getRequest());
			}
			PermissionService.assertPermission(forumAdmin);
			setVisibleComponent(state, m_categoryView);
		} else {
			super.setVisible(state, party, forum, mode);
		}
	}

	protected void generateModes(
		PageState state,
		Element content,
		Party party,
		Forum forum) {
		super.generateModes(state, content, party, forum);
		PermissionDescriptor permission =
			new PermissionDescriptor(PrivilegeDescriptor.ADMIN, forum, party);

		if (PermissionService.checkPermission(permission)) {
			generateModeXML(state, content, MODE_CATEGORIES);
		}
	}

}
