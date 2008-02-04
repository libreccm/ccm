/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.kernel.ui.DomainObjectSelectionModel;
import com.arsdigita.bebop.parameters.ParameterModel;

/**
 * @deprecated This class is used just once in all of the RHEA code
 * base; it will be moved there in the future, so please don't rely on
 * it
 */
public class RequestLocalSelectionModel extends AbstractSingleSelectionModel
        implements DomainObjectSelectionModel {
    private RequestLocal m_value;

    public RequestLocalSelectionModel() {
	this(new RequestLocal());
    }

    public RequestLocalSelectionModel(RequestLocal l) {
	m_value = l;
    }

    public void setSelectedKey(PageState state,
			       Object value) {
	m_value.set(state, value);
    }

    public Object getSelectedKey(PageState state) {
	return m_value.get(state);
    }

    public void setSelectedObject(PageState state,
				  DomainObject value) {
	m_value.set(state, value);
    }

    public DomainObject getSelectedObject(PageState state) {
	return (DomainObject)m_value.get(state);
    }

    public ParameterModel getStateParameter() {
	throw new UnsupportedOperationException
            ("No state parameter in request local selection models");
    }
}
