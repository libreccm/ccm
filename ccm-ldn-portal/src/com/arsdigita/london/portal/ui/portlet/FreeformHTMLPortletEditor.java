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

package com.arsdigita.london.portal.ui.portlet;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.london.portal.portlet.FreeformHTMLPortlet;
import com.arsdigita.london.portal.ui.PortletConfigFormSection;
import com.arsdigita.portal.Portlet;

public class FreeformHTMLPortletEditor extends PortletConfigFormSection {

	private TextArea m_content;

	public FreeformHTMLPortletEditor(ResourceType resType,
			RequestLocal parentAppRL) {
		super(resType, parentAppRL);
	}

	public FreeformHTMLPortletEditor(RequestLocal application) {
		super(application);
	}

	protected void addWidgets() {
		super.addWidgets();

		m_content = new TextArea(new StringParameter("content"));
		m_content.setRows(10);
		m_content.setCols(35);
		m_content.addValidationListener(new NotNullValidationListener());
        //m_content.addValidationListener(
        //    new StringInRangeValidationListener(1, 4000));

		add(new Label("Content:", Label.BOLD), ColumnPanel.RIGHT);
		add(m_content);
	}

	protected void initWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.initWidgets(state, portlet);

		if (portlet != null) {
			FreeformHTMLPortlet myportlet = (FreeformHTMLPortlet) portlet;

			m_content.setValue(state, myportlet.getContent());
		}
	}

	protected void processWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.processWidgets(state, portlet);

		FreeformHTMLPortlet myportlet = (FreeformHTMLPortlet) portlet;
		myportlet.setContent((String) m_content.getValue(state));
	}
}
