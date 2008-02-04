/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.simplesurvey.ui.admin;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.parameters.ParameterModel;

import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;

public class FormSelectionModel extends AbstractSingleSelectionModel {
    
    private RequestLocal m_key = new RequestLocal();
    
    private SurveySelectionModel m_survey;

    public FormSelectionModel(SurveySelectionModel survey) {
	m_survey = survey;
    }
    
    public Object getSelectedKey(PageState state) {
	if (!m_survey.isSelected(state))
	    return null;

	Survey survey = m_survey.getSelectedSurvey(state);
	PersistentForm form = survey.getForm();
	
	return form.getID();
    }

    public void setSelectedKey(PageState state, Object key) {
	throw new UnsupportedOperationException("No state parameter");
    }

    public ParameterModel getStateParameter() {
	throw new UnsupportedOperationException("No state parameter");
    }
}
