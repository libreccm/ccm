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

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.util.Assert;

public class DomainObjectSelectionModel extends AbstractSingleSelectionModel
    implements com.arsdigita.kernel.ui.DomainObjectSelectionModel {
    
    private ParameterModel m_param;

    public DomainObjectSelectionModel(String name) {
        this(new OIDParameter(name));
    }

    public DomainObjectSelectionModel(OIDParameter param) {
        m_param = param;
    }
    
    public Object getSelectedKey(PageState state) {
        return state.getValue(m_param);
    }
    
    public void setSelectedKey(PageState state, Object key) {
        Assert.truth(key == null ||
                     key.getClass().equals(OID.class), 
                     "key must be an OID");

        state.setValue(m_param, key);

        fireStateChanged(state);
    }

    public DomainObject getSelectedObject(PageState state) {
        OID oid = (OID)getSelectedKey(state);

        return DomainObjectFactory.newInstance(oid);
    }

    public void setSelectedObject(PageState state, DomainObject object) {
        setSelectedKey(state, object == null ? null : object.getOID());
    }
    
    public ParameterModel getStateParameter() {
        return m_param;
    }
}
                                                   
