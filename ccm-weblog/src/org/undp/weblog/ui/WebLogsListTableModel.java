package org.undp.weblog.ui;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogApplication;
import org.undp.weblog.WebLogComment;
import org.undp.weblog.WebLogConstants;
import org.undp.weblog.util.GlobalizationUtil;

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogsListTableModel implements TableModel, WebLogConstants {

	private final int m_columnCount;

	private final DataCollection m_coll;

	private WebLog m_entity = null;

	private boolean m_hasNext;

	private final String m_appURL;

	private final boolean m_userIsAdmin;

	public WebLogsListTableModel(WebLogApplication application, int columnCount) {
		m_columnCount = columnCount;
		m_coll = SessionManager.getSession().retrieve(
				WebLog.BASE_DATA_OBJECT_TYPE);
		m_coll.addEqualsFilter(WebLog.PARAM_APPLICATION + '.'
				+ WebLogApplication.ID, application.getID());
		m_coll.addOrder(WebLog.PARAM_MODIFIED + " desc");
		m_hasNext = true;
		m_appURL = application.getPath();
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, application, Web.getContext()
						.getUser());
		m_userIsAdmin = PermissionService.checkPermission(perm);
	}

	public int getColumnCount() {
		return m_columnCount;
	}

	public Object getElementAt(int columnIndex) {
		if (m_entity != null) {
			String entityID = m_entity.getID().toString();
			Link l;
			switch (columnIndex) {// HEADERS = {"Title", "Modified", "Lead",
			// "Read", "Comments"};
			case 0:
				l = new Link(m_entity.getTitle(), m_appURL);
				l.setVar(PARAM_WEBLOG_DETAIL_ID, entityID);
				return l;
			case 1:
				return new Label(dateTimeFormat.format(m_entity.getModified()));
			case 2:
				return new Label(m_entity.getLead(), false);
			case 3:
				l = new Link(
						GlobalizationUtil.localize("webLogsList.readMore"),
						m_appURL);
				l.setVar(PARAM_WEBLOG_DETAIL_ID, entityID);
				return l;
			case 4:
				DataCollection coll = SessionManager.getSession().retrieve(
						WebLogComment.BASE_DATA_OBJECT_TYPE);
				coll.addEqualsFilter(WebLogComment.PARAM_WEBLOG + '.'
						+ WebLog.ID, m_entity.getID());
				long commSize = coll.size();
				coll.close();
				String comments;
				if (commSize == 1) {
					comments = "1 comment";
				} else {
					comments = commSize + " comments";
				}
				return new Label(comments);
			case 5:
				if (m_userIsAdmin) {
					return new ControlLink(GlobalizationUtil
							.localize("webLogsList.edit"));
				}
				break;
			case 6:
				if (m_userIsAdmin) {
					ControlLink cl = new ControlLink(GlobalizationUtil
							.localize("webLogsList.delete"));
					cl.setOnClick(" return confirm('"
							+ GlobalizationUtil.localize("confirmDelete")
							+ "');");
					return cl;
				}
				break;
			}
		}
		return null;
	}

	public boolean nextRow() {
		if (m_hasNext) {
			m_hasNext = m_coll.next();
			if (m_hasNext) {
				m_entity = new WebLog(m_coll.getDataObject());
			} else {
				m_entity = null;
				m_coll.close();
			}
		}
		return m_hasNext;
	}

	public Object getKeyAt(int columnIndex) {
		if (m_entity != null) {
			return m_entity.getID().toString();
		}
		return null;
	}
}
