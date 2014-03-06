package org.undp.weblog.ui;

import java.math.BigDecimal;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogApplication;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogsList extends Table implements TableActionListener {

	public static final String[] HEADERS = { "Title", "Modified", "Lead",
			"Read", "Comments" };

	public static final String[] HEADERS_ACTION = { "Title", "Modified",
			"Lead", "Read", "Comments", "", "" };

	private final WebLogView m_parent;

	public WebLogsList(final WebLogApplication application) {
		super(new TableModelBuilder() {
			private boolean m_locked = false;

			public TableModel makeModel(Table t, PageState state) {
				return new WebLogsListTableModel(application, HEADERS.length);
			}

			public void lock() {
				m_locked = true;
			}

			public boolean isLocked() {
				return m_locked;
			}
		}, HEADERS);
		m_parent = null;
	}

	public WebLogsList(WebLogView parent) {
		super(new TableModelBuilder() {
			private boolean m_locked = false;

			public TableModel makeModel(Table t, PageState state) {
				return new WebLogsListTableModel((WebLogApplication) Web
						.getWebContext().getApplication(), HEADERS_ACTION.length);
			}

			public void lock() {
				m_locked = true;
			}

			public boolean isLocked() {
				return m_locked;
			}
		}, HEADERS_ACTION);
		m_parent = parent;

		addTableActionListener(this);
	}

	public void cellSelected(TableActionEvent e) {
		if (m_parent != null && m_parent.canUserAdminApplication()) {
			int col = e.getColumn().intValue();
			BigDecimal entityID = new BigDecimal((String) e.getRowKey());
			if (entityID != null) {
				PageState ps = e.getPageState();
				switch (col) {
				case 5:
					m_parent.setEntityID(ps, entityID);
					m_parent.displayWebLogEditForm(ps);
					break;
				case 6:
					WebLog entity = new WebLog(entityID);
					entity.delete();
					m_parent.displayWebLogsList(ps);
					break;
				}
			}
		}
	}

	public void headSelected(TableActionEvent e) {
		throw new UnsupportedOperationException();
	}
}
