package org.undp.weblog.ui;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogConstants;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

/**
 * @author Peter Kopunec
 */
public class WebLogDetail extends GridPanel implements WebLogConstants {

	public WebLogDetail(final RequestLocal entityRequest) {
		super(2);
		add(new Label("id"));
		add(new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(entity.getID().toString());
			}
		}));

		add(new Label("title"));
		add(new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(entity.getTitle());
			}
		}));

		add(new Label("modified"));
		add(new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(dateTimeFormat.format(entity.getModified()));
			}
		}));

		add(new Label("lead"));
		Label detail = new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(entity.getLead());
			}
		});
		detail.setOutputEscaping(false);
		add(detail);

		add(new Label("body"));
		detail = new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(entity.getBody());
			}
		});
		detail.setOutputEscaping(false);
		add(detail);

		add(new Label("author"));
		add(new Label(new PrintListener() {
			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				WebLog entity = (WebLog) entityRequest.get(e.getPageState());
				label.setLabel(entity.getOwner().getDisplayName());
			}
		}));
	}
}
