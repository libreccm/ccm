package org.undp.weblog;

import java.util.HashMap;

import org.undp.weblog.ui.WebLogPage;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopMapDispatcher;

/**
 * @author Peter Kopunec
 */
public class WebLogDispatcher extends BebopMapDispatcher {

	public WebLogDispatcher() {
		super();

		HashMap m = new HashMap();

		Page index = new WebLogPage();
		m.put("", index);
		m.put("index.jsp", index);

		setMap(m);
	}
}
