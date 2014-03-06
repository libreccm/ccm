package org.undp.weblog.ui;

import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogPage extends Page {

	public static final String HEADER_ELEMENT = "weblog:header";

	public static final String BODY_ELEMENT = "weblog:body";

	public static final String FOOTER_ELEMENT = "weblog:footer";

	public static final String XML_NS = "http://www.undp.org/weblog/1.0";

	private final BigDecimalParameter m_entityIDparam = new BigDecimalParameter(
			"e_id");

	private final BigDecimalParameter m_parentIDparam = new BigDecimalParameter(
			"p_id");

	public WebLogPage() {
		super("WebLog", new SimpleContainer());

		setClassAttr("weblog");

		SimpleContainer header = new SimpleContainer(HEADER_ELEMENT, XML_NS);
		SimpleContainer body = new SimpleContainer(BODY_ELEMENT, XML_NS);
		SimpleContainer footer = new SimpleContainer(FOOTER_ELEMENT, XML_NS);

		DimensionalNavbar navbar = new DimensionalNavbar();
		navbar.setClassAttr("portalNavbar");
		navbar.add(new Link(new PrintListener() {
			public void prepare(PrintEvent e) {
				Link link = (Link) e.getTarget();
				Application currApp = Web.getWebContext().getApplication();
				Application prevApp = currApp.getParentApplication();
				link.setChild(new Label(prevApp.getTitle()));
				link.setTarget(prevApp.getPath());
			}
		}));
		navbar.add(new Link(new PrintListener() {
			public void prepare(PrintEvent e) {
				Link link = (Link) e.getTarget();
				Application currApp = Web.getWebContext().getApplication();
				link.setChild(new Label(currApp.getTitle()));
				link.setTarget(currApp.getPath());
			}
		}));
		header.add(navbar);

		body.add(new WebLogView(m_entityIDparam, m_parentIDparam));

		add(header);
		add(body);
		add(footer);

		addGlobalStateParam(m_entityIDparam);
		addGlobalStateParam(m_parentIDparam);

		lock();
	}
}
