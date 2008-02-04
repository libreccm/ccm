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

package com.arsdigita.london.util;

import com.arsdigita.bebop.ComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;

public abstract class ComponentSelectionModelFacade
    implements ComponentSelectionModel
{
    protected SingleSelectionModel m_model;

    public ComponentSelectionModelFacade( SingleSelectionModel model ) {
        m_model = model;
    }

    public void addChangeListener( ChangeListener l ) {
        m_model.addChangeListener( l );
    }

    public void clearSelection( PageState ps ) {
        m_model.clearSelection( ps );
    }

    public Object getSelectedKey( PageState ps ) {
        return m_model.getSelectedKey( ps );
    }

    public ParameterModel getStateParameter() {
        return m_model.getStateParameter();
    }

    public boolean isSelected( PageState ps ) {
        return m_model.isSelected( ps );
    }

    public void removeChangeListener( ChangeListener l ) {
        m_model.removeChangeListener( l );
    }

    public void setSelectedKey( PageState ps, Object key ) {
        m_model.setSelectedKey( ps, key );
    }
}
