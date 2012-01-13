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
 */

package com.arsdigita.rssfeed.portlet;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
// import com.arsdigita.london.portal.Workspace;
import com.arsdigita.rssfeed.RSSChannel;
import com.arsdigita.rssfeed.RSSImage;
import com.arsdigita.rssfeed.RSSItem;
import com.arsdigita.rssfeed.RSSItemCollection;
import com.arsdigita.rssfeed.SimpleRSSItem;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;

public class WorkspaceDirectoryChannel implements RSSChannel {

    // XXX Quick Hack: Determine Object Type by method! Take care if the package name changes.
    private static final String WORKSPACE_OBJECT_TYPE = "com.arsdigita.london.portal.Workspace";

    private RSSImage m_image;

	private Party m_party;

	private String m_link;

	public WorkspaceDirectoryChannel(Party party, String link, RSSImage image) {
		m_image = image;
		m_party = party;
		m_link = link;
	}

	public String getTitle() {
		return "Workspace Directory";
	}

	public String getLink() {
		return m_link;
	}

	public String getDescription() {
		return "Workspace Directory for "
				+ (m_party == null ? "The Public" : m_party.getDisplayName());
	}

	public RSSImage getImage() {
		return m_image;
	}

	public RSSItemCollection getItems() {
		Application current = Web.getContext().getApplication();
            ApplicationCollection workspaces = Application
				.retrieveAllApplications();
		workspaces.addEqualsFilter(ACSObject.OBJECT_TYPE,
				WORKSPACE_OBJECT_TYPE);
		workspaces.addNotEqualsFilter(ACSObject.ID, current.getID());
		workspaces.addOrder(Resource.TITLE);
		PermissionService.filterObjects(workspaces, PrivilegeDescriptor.READ,
				m_party == null ? null : m_party.getOID());

		return new WorkspaceItemCollection(workspaces);
	}

	private class WorkspaceItemCollection implements RSSItemCollection {

		private ApplicationCollection m_workspaces;

		public WorkspaceItemCollection(ApplicationCollection workspaces) {
			m_workspaces = workspaces;
		}

		public boolean next() {
			return m_workspaces.next();
		}

		public RSSItem getItem() {
			Application app = m_workspaces.getApplication();
			return new SimpleRSSItem(app.getTitle(), URL.there(app, "/", null)
					.toString(), app.getDescription());
		}
	}
}
