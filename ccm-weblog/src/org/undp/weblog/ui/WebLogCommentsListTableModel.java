package org.undp.weblog.ui;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogComment;
import org.undp.weblog.WebLogConstants;
import org.undp.weblog.util.GlobalizationUtil;

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogCommentsListTableModel implements TableModel,
		WebLogConstants {

	private final DataCollection m_coll;

	private WebLogComment m_entity = null;

	private boolean m_hasNext;

	private final User m_user;

	private final boolean m_userIsAdmin;

	// private final boolean m_userCanEdit;

	public WebLogCommentsListTableModel(WebLog webLog) {
		m_coll = webLog.getComments();
		m_hasNext = true;

		Application application = Web.getWebContext().getApplication();
		m_user = Web.getWebContext().getUser();
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, application, m_user);
		m_userIsAdmin = PermissionService.checkPermission(perm);
		// perm = new PermissionDescriptor(PrivilegeDescriptor.EDIT,
		// application, m_user);
		// m_userCanEdit = PermissionService.checkPermission(perm);
	}

	public int getColumnCount() {
		return WebLogCommentsList.HEADERS.length;
	}

	public Object getElementAt(int columnIndex) {
		if (m_entity != null) {
			String entityID = m_entity.getID().toString();
			Link l;
			switch (columnIndex) {// HEADERS = {"Comment", "Autor",
			// "Modified", "", ""};
			case 0:
				return new Label(m_entity.getComment(), false);
			case 1:
				return new Label(m_entity.getOwner().getDisplayName());
			case 2:
				return new Label(dateTimeFormat.format(m_entity.getModified()));
			case 3:
				if (m_userIsAdmin
						|| (/* m_userCanEdit && */m_user.equals(m_entity
								.getOwner()))) {
					return new ControlLink(GlobalizationUtil
							.localize("webLogCommentsList.edit"));
				}
				break;
			case 4:
				if (m_userIsAdmin
						|| (/* m_userCanEdit && */m_user.equals(m_entity
								.getOwner()))) {
					ControlLink cl = new ControlLink(GlobalizationUtil
							.localize("webLogCommentsList.delete"));
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
				m_entity = new WebLogComment(m_coll.getDataObject());
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
