/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.london.shortcuts.ui;

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.PageState;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.shortcuts.Shortcut;
import com.arsdigita.london.shortcuts.ShortcutUtil;
import com.arsdigita.london.shortcuts.ShortcutCollection;
import java.math.BigDecimal;

import org.apache.log4j.Category;

import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.Component;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.bebop.ExternalLink;

/**
 * 
 * 
 */
public class ShortcutsTable extends Table {
    private static final Category log = 
        Category.getInstance(ShortcutsTable.class.getName());

	public static final String headers[] = { "URL Key", "Redirect", "", "" };

	public ShortcutsTable(final ACSObjectSelectionModel selected_shortcut) {
		super(new ShortcutsModelBuilder(), headers);

		setDefaultCellRenderer(new ShortcutsCellRenderer());

		addTableActionListener(new TableActionListener() {
			public void cellSelected(TableActionEvent e) {
				selected_shortcut.clearSelection(e.getPageState());
				String row = (String) e.getRowKey();
				if (e.getColumn().intValue() == 2) {
					// edit selected
					log.debug("selected edit shortcut " + row);
					try {
						selected_shortcut.setSelectedKey(e.getPageState(), new BigDecimal(row));
					} catch (DataObjectNotFoundException ex) {
						throw new UncheckedWrapperException(
								"cannot find shortcut", ex);
					}
				} else if (e.getColumn().intValue() == 3) {
					// delete selected
					log.fatal("selected delete shortcut " + row);
					try {
						Shortcut shortcut = new Shortcut(new BigDecimal(row));
						log.info("delete shortcut " +  shortcut.getUrlKey());
						shortcut.delete();
						ShortcutUtil.repopulateShortcuts();
					} catch (DataObjectNotFoundException ex) {
						throw new UncheckedWrapperException(
								"cannot find shortcut", ex);
					}
				}
			}

			public void headSelected(TableActionEvent e) {
			}
		});
	}

	protected static class ShortcutsModelBuilder extends LockableImpl implements
			TableModelBuilder {

		public TableModel makeModel(Table table, PageState ps) {
			return new ShortcutsModel();
		}

		protected class ShortcutsModel implements TableModel {
			private ShortcutCollection m_shortcuts;

			private Shortcut m_shortcut;

			public ShortcutsModel() {
				m_shortcuts = Shortcut.retrieveAll();
			}

			public int getColumnCount() {
				return headers.length;
			}

			public boolean nextRow() {
				if (m_shortcuts.next()) {
					m_shortcut = m_shortcuts.getShortcut();
					return true;
				}
				return false;
			}

			public Object getElementAt(int col) {
				return m_shortcut;
			}

			public Object getKeyAt(int col) {
				BigDecimal id = m_shortcut.getID();
				return id;
			}
		}
	}

	protected static class ShortcutsCellRenderer implements TableCellRenderer {
		public Component getComponent(Table table, PageState state,
				Object value, boolean isSelected, Object key, int row,
				int column) {
			Shortcut shortcut = (Shortcut) value;

			switch (column) {
			case 0:
				return new ExternalLink(shortcut.getUrlKey(), shortcut
						.getUrlKey());
			case 1:
				return new ExternalLink(shortcut.getRedirect(), shortcut
						.getRedirect());
			case 2:
				return new ControlLink(" edit ");
			case 3:
				return new ControlLink(" delete ");
			default:
				throw new UncheckedWrapperException("Column out of bounds");
			}
		}
	}
}
