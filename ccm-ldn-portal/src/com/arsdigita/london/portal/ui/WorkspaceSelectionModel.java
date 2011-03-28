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

package com.arsdigita.london.portal.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.london.portal.Workspace;

/**
 * 
 * 
 */
public abstract class WorkspaceSelectionModel {

	/** Workspace. Bound to the current HTTP request. */
    private RequestLocal m_workspace;

	/**
     * Constructor.
     */
    public WorkspaceSelectionModel() {
		m_workspace = new RequestLocal() {
            @Override
			public Object initialValue(PageState state) {
				return getDefaultWorkspace(state);
			}
		};
	}

	/**
     * 
     * @param state
     * @return
     */
    protected abstract Workspace getDefaultWorkspace(PageState state);

	/**
     * 
     * @param state
     * @return
     */
    public Workspace getSelectedWorkspace(PageState state) {
		return (Workspace) m_workspace.get(state);
	}

	/**
     * 
     * @param state
     * @param workspace
     */
    public void setSelectedWorkspace(PageState state, Workspace workspace) {
		m_workspace.set(state, workspace);
	}
}
