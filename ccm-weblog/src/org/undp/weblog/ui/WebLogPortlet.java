package org.undp.weblog.ui;

import org.apache.log4j.Logger;
import org.undp.weblog.WebLogApplication;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class WebLogPortlet extends AppPortlet {

	public static final String BASE_DATA_OBJECT_TYPE = WebLogPortlet.class
			.getName();

	private static final Logger s_log = Logger.getLogger(WebLogPortlet.class);

	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public WebLogPortlet(DataObject dataObject) {
		super(dataObject);
		s_log
				.debug("XXX constructor WebLogPortlet(DataObject dataObject) called");
	}

	protected AbstractPortletRenderer doGetPortletRenderer() {
		PortletRenderer pr = new PortletRenderer(this);
		pr.setPortletAttribute("type", BASE_DATA_OBJECT_TYPE);
		return pr;
	}

	class PortletRenderer extends AbstractPortletRenderer {
		private WebLogPortlet m_portlet;

		public PortletRenderer(WebLogPortlet portlet) {
			m_portlet = portlet;
		}

		protected void generateBodyXML(PageState pageState,
				Element parentElement) {
			WebLogApplication app = (WebLogApplication) m_portlet
					.getParentApplication();
			WebLogsList list = new WebLogsList(app);
			list.generateXML(pageState, parentElement);
		}
	}
}
