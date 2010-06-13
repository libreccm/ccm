/*
 * Created on 09-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.forum.categorised;

// import javax.servlet.ServletException;

import com.arsdigita.bebop.PageState;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.ForumComponent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.xml.Element;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 *
 * @author cgyg9330
 * @version $Id: $
 */
public class CategorisedForumComponent extends ForumComponent {

	public static final String MODE_CATEGORIES = "categories";

	private AssignCategoryView m_categoryView;

	/**
	 * 
	 */
	public CategorisedForumComponent() {
		super();
		m_categoryView = new AssignCategoryView();
		add(m_categoryView);

		// TODO Auto-generated constructor stub
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
