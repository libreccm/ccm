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

package com.arsdigita.portalworkspace.ui;

import java.math.BigDecimal;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.portal.PortletType;

public class PortletTypeSelectionModel extends AbstractSingleSelectionModel {

	private ParameterSingleSelectionModel m_model;

	public PortletTypeSelectionModel(BigDecimalParameter p) {
		m_model = new ParameterSingleSelectionModel(p);
	}

	public Object getSelectedKey(PageState state) {
		return m_model.getSelectedKey(state);
	}

	public void setSelectedKey(PageState state, Object key) {
		m_model.setSelectedKey(state, key);
	}

	public ParameterModel getStateParameter() {
		return m_model.getStateParameter();
	}

	public PortletType getSelectedPortletType(PageState state) {
		BigDecimal key = (BigDecimal) getSelectedKey(state);

		return PortletType.retrievePortletType(key);
	}
}
