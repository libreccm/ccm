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

package com.arsdigita.london.portal;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Resource;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.portal.Portal;

public class WorkspacePage extends Portal {

	public static final String SORT_KEY = "sortKey";

	public static final String LAYOUT = "layout";

	public static final String WORKSPACE = "workspace";

	public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.london.portal.WorkspacePage";

	public WorkspacePage() {
		this(BASE_DATA_OBJECT_TYPE);
	}

	public WorkspacePage(String type) {
		super(type);
	}

	public WorkspacePage(DataObject dobj) {
		super(dobj);
	}

	public WorkspacePage(OID oid) {
		super(oid);
	}

	static WorkspacePage create(String title, String description,
			PageLayout layout, Workspace workspace, int sortKey) {
		WorkspacePage page = (WorkspacePage) Resource.createResource(
				WorkspacePage.BASE_DATA_OBJECT_TYPE, title, null);
		page.setup(description, layout, workspace, sortKey);
		return page;
	}

	protected void setup(String description, PageLayout layout,
			Workspace workspace, int sortKey) {
		setDescription(description);
		setLayout(layout);
		setWorkspace(workspace);
		setSortKey(sortKey);
	}

	void setSortKey(int key) {
		set(SORT_KEY, new Integer(key));
	}

	public int getSortKey() {
		return ((Integer) get(SORT_KEY)).intValue();
	}

	void setWorkspace(Workspace workspace) {
		setAssociation(WORKSPACE, workspace);
	}

	public Workspace getWorkspace() {
		return (Workspace) DomainObjectFactory
				.newInstance((DataObject) get(WORKSPACE));
	}

	public void setLayout(PageLayout layout) {
		setAssociation(LAYOUT, layout);
	}

	public PageLayout getLayout() {
		return (PageLayout) DomainObjectFactory
				.newInstance((DataObject) get(LAYOUT));
	}

}
