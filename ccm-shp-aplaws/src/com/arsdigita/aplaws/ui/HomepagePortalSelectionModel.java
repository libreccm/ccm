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

package com.arsdigita.aplaws.ui;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.portalworkspace.ui.PortalSelectionModel;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.domain.DomainObject;

public class HomepagePortalSelectionModel extends AbstractSingleSelectionModel
    implements PortalSelectionModel {

    private HomepageWorkspaceSelectionModel m_workspace;
    private int m_column;

    public HomepagePortalSelectionModel(HomepageWorkspaceSelectionModel workspace,
                                        int column) {
        m_workspace = workspace;
        m_column = column;
    }

    public void onCustomize(PageState state) {
        m_workspace.onCustomize(state, m_column);
    }

    public void onReset(PageState state) {
        m_workspace.onReset(state, m_column);
    }

    public HomepageWorkspaceSelectionModel getWorkspaceModel() {
        return m_workspace;
    }

    public Object getSelectedKey(PageState state) { 
        return getSelectedPortal(state).getID();
    }
    
    public void setSelectedKey(PageState state,
                               Object key) {
        throw new UnsupportedOperationException("cannot set key");
    }

    public void setSelectedObject(PageState state,
                               DomainObject key) {
        throw new UnsupportedOperationException("cannot set object");
    }

    public ParameterModel getStateParameter() {
        throw new UnsupportedOperationException("not state param");
    }


    public DomainObject getSelectedObject(PageState state) {
        return getSelectedPortal(state);
    }

    public WorkspacePage getSelectedPortal(PageState state) {
        return m_workspace.getPortal(state, m_column);
    }
}
