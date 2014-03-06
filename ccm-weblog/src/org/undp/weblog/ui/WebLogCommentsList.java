package org.undp.weblog.ui;

import java.math.BigDecimal;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogComment;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogCommentsList extends Table implements TableActionListener {

	public static final String[] HEADERS = { "Comment", "Autor", "Modified",
			"", "" };

	private final WebLogView m_parent;

	public WebLogCommentsList(WebLogView parent,
			final RequestLocal webLogRequestLocal) {
		super(new TableModelBuilder() {
			private boolean m_locked = false;

			public TableModel makeModel(Table t, PageState ps) {
				return new WebLogCommentsListTableModel(
						(WebLog) webLogRequestLocal.get(ps));
			}

			public void lock() {
				m_locked = true;
			}

			public boolean isLocked() {
				return m_locked;
			}
		}, HEADERS);
		m_parent = parent;

		addTableActionListener(this);
	}

	public void cellSelected(TableActionEvent e) {
		if (m_parent != null) {
			int col = e.getColumn().intValue();
			BigDecimal entityID = new BigDecimal((String) e.getRowKey());
			WebLogComment entity = new WebLogComment(entityID);
			if (entity != null) {
				if (m_parent.canUserAdminApplication() || (/* m_parent.canUserEditApplication() && */
				Web.getWebContext().getUser().equals(entity.getOwner()))) {

					PageState ps = e.getPageState();
					switch (col) {
					case 3: // edit
						m_parent.setEntityID(ps, entityID);
						m_parent.setParentID(ps, entity.getWebLog().getID());
						m_parent.displayWebLogCommentEditForm(ps);
						break;
					case 4: // delete
						m_parent.setEntityID(ps, entity.getWebLog().getID());
						entity.delete();
						m_parent.displayWebLogDetail(ps);
						break;
					}
				}
			}
		}
	}

	public void headSelected(TableActionEvent e) {
		throw new UnsupportedOperationException();
	}
}
