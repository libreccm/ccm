/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.web.Web;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/** 
 *
 * @version $Id: AssignedTaskTable.java 1563 2007-04-18 15:58:17Z apevec $
 */
public final class AssignedTaskTable extends Table {

    private static final Logger s_log = Logger.getLogger
        (AssignedTaskTable.class);

    public AssignedTaskTable(final WorkflowRequestLocal workflow) {

        super(new AssignedTaskTableModelBuilder(workflow),
              new String[] { lz("cms.ui.name"), "", "" });

        // XXX The string array and setHeader(null) are a product of
        // messed up Table behavior.

        setEmptyView(new Label(gz("cms.ui.workflow.task.assigned.none")));

        addTableActionListener(new LockListener());
 
        getColumn(1).setCellRenderer(new CellRenderer());
        getColumn(2).setCellRenderer(new DefaultTableCellRenderer(true));
    }

    private static class LockListener extends TableActionAdapter {
        @Override
        public final void cellSelected(final TableActionEvent e) {
            final int column = e.getColumn().intValue();

            if (column == 1) {
                final CMSTask task = new CMSTask(new BigDecimal(e.getRowKey()
                        .toString()));
                User currentUser = Web.getWebContext().getUser();
                User lockingUser = task.getLockedUser();
                if (task.isLocked() && lockingUser != null
                        && lockingUser.equals(currentUser)) {
                    task.unlock(currentUser);
                } else {
                    task.lock(currentUser);
                }
                task.save();
            }
        }
    }

    private class CellRenderer implements TableCellRenderer {
        public final Component getComponent(final Table table,
                final PageState state, final Object value,
                final boolean isSelected, final Object key, final int row,
                final int column) {
            // SF patch [ 1587168 ] Show locking user
            BoxPanel p = new BoxPanel();
            User lockingUser = (User) value;
            if (lockingUser != null) {
                StringBuilder sb = new StringBuilder("Locked by <br />");
                if (lockingUser.equals(Web.getWebContext().getUser())) {
                    sb.append("you");
                    p.add(new ControlLink(new Label(
                            gz("cms.ui.workflow.task.unlock"))));
                } else {
                    sb.append(lockingUser.getName());
                    p.add(new ControlLink(new Label(
                            gz("cms.ui.workflow.task.takeover"))));
                }
                p.add(new Label( sb.toString(), false));
            } else {
                p.add(new ControlLink(
                        new Label(gz("cms.ui.workflow.task.lock"))));
            }
            return p;
        }
    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
