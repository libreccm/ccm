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

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.kernel.ui.DomainObjectSelectionModel;
import com.arsdigita.bebop.parameters.ParameterModel;




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
	throw new UnsupportedOperationException("no state parameter in request local selection models");
    }
}
