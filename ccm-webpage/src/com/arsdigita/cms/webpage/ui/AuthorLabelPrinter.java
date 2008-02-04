package com.arsdigita.cms.webpage.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.kernel.User;
import com.arsdigita.web.Web;

public class AuthorLabelPrinter implements PrintListener {
	
	public AuthorLabelPrinter() {
		// Empty
	}
	
	public void prepare(PrintEvent e) {
		Label label = (Label) e.getTarget();
		PageState pageState = e.getPageState();
		
		User user = Web.getContext().getUser();
		if (user != null) {
			label.setLabel("Author: (if not " + user.getName() + ")");
		}
		else {
			label.setLabel("Author:");
		}
	}
}