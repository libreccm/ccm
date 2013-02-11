/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.portalworkspace.ui.sitemap.ApplicationSelectionModel;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * @version $Revision: #3 $ $Date: 2004/03/02 $
 * @author Nobuko Asakai (nasakai@redhat.com)
 */

public class CategoryTable extends Table {

	private static final Logger s_log = Logger.getLogger(CategoryTable.class);

	private ApplicationSelectionModel m_appModel;

	private ACSObjectSelectionModel m_catModel;

	private TableColumn m_titleCol;

	private TableColumn m_delCol;

	protected static final String DELETE_EVENT = "Delete";

	public CategoryTable(ApplicationSelectionModel appModel,
			ACSObjectSelectionModel catModel) {
		super();
		m_appModel = appModel;
		m_catModel = catModel;
		s_log.debug("Creating CategoryTable");
		addColumns();

		Label empty = new Label("no categories");
		setEmptyView(empty);
		addTableActionListener(new CategoryTableActionListener());
		setRowSelectionModel(m_catModel);
		setDefaultCellRenderer(new CategoryTableRenderer());
		setModelBuilder(new CategoryTableModelBuilder(m_appModel));
	}

	protected void addColumns() {
		TableColumnModel model = getColumnModel();

		int i = 0;
		m_titleCol = new TableColumn(i, "Link");
		m_delCol = new TableColumn(++i, "Delete");
		model.add(m_titleCol);
		model.add(m_delCol);
		setColumnModel(model);
	}

	private class CategoryTableRenderer extends DefaultTableCellRenderer {
		public Component getComponent(Table table, PageState state,
				Object value, boolean isSelected, Object key, int row,
				int column) {

			if (column == m_delCol.getModelIndex()) {
				return new ControlLink(DELETE_EVENT);
			} else {
				Category cat = (Category) value;
				return new Label(cat.getName());
			}
		}
	}

	private class CategoryTableActionListener implements TableActionListener {
		private Category getCategory(TableActionEvent e) {
			Object o = e.getRowKey();
			BigDecimal id;
			if (o instanceof String) {
				s_log.debug("row key is a string : " + o);
				id = new BigDecimal((String) o);
			} else {
				id = (BigDecimal) e.getRowKey();
			}

			Assert.exists(id, BigDecimal.class);
			Category cat;
			try {
				cat = new Category(id);
			} catch (DataObjectNotFoundException de) {
				throw new UncheckedWrapperException(de);
			}
			return cat;
		}

		public void cellSelected(TableActionEvent e) {
			int col = e.getColumn().intValue();
			PageState state = e.getPageState();
			Category cat = getCategory(e);
			Workspace workspace = (Workspace) m_appModel
					.getSelectedObject(state);

			Assert.exists(cat, Category.class);
			Assert.exists(workspace, Workspace.class);

			if (col == m_delCol.getModelIndex()) {

				try {
					s_log.debug("About to delete");
					m_catModel.clearSelection(state);
					cat.removeChild(workspace);
					cat.save();
				} catch (PersistenceException pe) {
					throw new UncheckedWrapperException(pe);
				}
			} else {
				s_log.debug("Workspace already in the category");
			}
		}

		public void headSelected(TableActionEvent e) {
		}
	}

	private class CategoryTableModelBuilder extends AbstractTableModelBuilder {

		private ApplicationSelectionModel m_appModel;

		public CategoryTableModelBuilder(ApplicationSelectionModel appModel) {
			m_appModel = appModel;
		}

		public TableModel makeModel(Table t, PageState state) {
			Workspace workspace = (Workspace) m_appModel
					.getSelectedObject(state);

			CategorizedObject obj = new CategorizedObject(workspace);
			try {
				DomainCollection cats = obj.getParents();

				if (cats.isEmpty()) {
					return Table.EMPTY_MODEL;
				} else {
					return new CategoryTableModel(cats);
				}
			} catch (DataObjectNotFoundException ex) {
				s_log.debug("no parent categories");
				return Table.EMPTY_MODEL;
			}
		}
	}

	private class CategoryTableModel implements TableModel {
		private DomainCollection m_cats;

		private Category m_cat;

		public CategoryTableModel(DomainCollection cats) {
			m_cats = cats;
		}

		public boolean nextRow() {
			if (m_cats.next()) {
				m_cat = (Category) m_cats.getDomainObject();
				return true;
			} else {
				return false;
			}

		}

		public int getColumnCount() {
			return 2;
		}

		public Object getElementAt(int columnIndex) {
			return m_cat;
		}

		public Object getKeyAt(int columnIndex) {
			return m_cat.getID();
		}
	}

}
