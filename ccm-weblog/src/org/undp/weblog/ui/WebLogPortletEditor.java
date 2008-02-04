package org.undp.weblog.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.Portlet;

/**
 * @author Peter Kopunec
 */
public class WebLogPortletEditor extends PortletConfigFormSection {

	private TextField m_title;

	private RequestLocal m_parentAppRL;

	private final boolean m_create;

	public WebLogPortletEditor(ResourceType resType, RequestLocal parentAppRL) {
		super(resType, parentAppRL);
		m_parentAppRL = parentAppRL;
		m_create = true;
	}

	public WebLogPortletEditor(RequestLocal application) {
		super(application);
		m_create = false;
	}

	public void addWidgets() {
		m_title = new TextField(new StringParameter("title"));
		m_title.setSize(35);
		m_title.addValidationListener(new NotNullValidationListener());
		m_title.addValidationListener(new StringInRangeValidationListener(1,
				200));
		add(new Label("Title:"));
		add(m_title);
		// TODO
	}

	public void initWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		if (portlet != null) {

		}
		// TODO
	}

	public void validateWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		// TODO
	}

	public void processWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		// TODO
	}
}
