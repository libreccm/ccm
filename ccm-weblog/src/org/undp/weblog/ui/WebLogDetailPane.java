package org.undp.weblog.ui;

import java.math.BigDecimal;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogConstants;
import org.undp.weblog.util.GlobalizationUtil;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class WebLogDetailPane extends GridPanel implements WebLogConstants {

	private static final String NAME_TITLE = "title";

	private static final String NAME_LEAD = "lead";

	private static final String NAME_BODY = "body";

	private final WebLogView m_parent;

	private final ActionLink m_edit;

	private final ActionLink m_delete;

	private final ActionLink m_addComment;

	public WebLogDetailPane(WebLogView parent) {
		super(1);
		m_parent = parent;

		final RequestLocal entityRequest = new RequestLocal() {
			protected Object initialValue(PageState ps) {
				BigDecimal detailID = m_parent.getEntityID(ps);
				return new WebLog(detailID);
			}
		};
		GridPanel detail = new GridPanel(1);
		detail.add(new WebLogDetail(entityRequest));
		detail.add(new WebLogCommentsList(m_parent, entityRequest));
		add(detail);

		m_addComment = new ActionLink(GlobalizationUtil
				.localize("webLogDetailPane.addComment"));
		m_addComment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if (m_parent.canUserAdminApplication() ||
				// m_parent.canUserEditApplication()) {
				PageState ps = e.getPageState();
				m_parent.setParentID(ps, (BigDecimal) m_parent
						.getEntityIDParam().transformValue(ps.getRequest()));
				m_parent.displayWebLogCommentCreateForm(ps);
				// }
			}
		});
		add(m_addComment);

		add(new Label("&nbsp;", false));

		ActionLink link = new ActionLink(GlobalizationUtil
				.localize("webLogDetailPane.detailsList"));
		link.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_parent.displayWebLogsList(e.getPageState());
			}
		});
		add(link);

		m_edit = new ActionLink(GlobalizationUtil
				.localize("webLogDetailPane.edit"));
		m_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_parent.canUserAdminApplication()) {
					PageState ps = e.getPageState();
					m_parent
							.setEntityID(ps, (BigDecimal) m_parent
									.getEntityIDParam().transformValue(
											ps.getRequest()));
					m_parent.displayWebLogEditForm(ps, true);
				}
			}
		});
		add(m_edit);

		m_delete = new ActionLink(GlobalizationUtil
				.localize("webLogDetailPane.delete"));
		m_delete.setOnClick(" return confirm('"
				+ GlobalizationUtil.localize("confirmDelete") + "');");
		m_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_parent.canUserAdminApplication()) {
					PageState ps = e.getPageState();
					BigDecimal detailID = (BigDecimal) m_parent
							.getEntityIDParam().transformValue(ps.getRequest());
					WebLog entity = new WebLog(detailID);
					entity.delete();
					m_parent.displayWebLogsList(ps);
				}
			}
		});
		add(m_delete);
	}

	public void generateXML(PageState ps, Element p) {
		boolean canAdmin = m_parent.canUserAdminApplication();
		// m_addComment.setVisible(ps, canAdmin ||
		// m_parent.canUserEditApplication());
		m_edit.setVisible(ps, canAdmin);
		m_delete.setVisible(ps, canAdmin);

		super.generateXML(ps, p);
	}

}
