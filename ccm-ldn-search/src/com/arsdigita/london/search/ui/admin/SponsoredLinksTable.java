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


package com.arsdigita.london.search.ui.admin;

import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.london.search.SponsoredLink;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.ACSObjectCollection;

public class SponsoredLinksTable extends Table {

    private ACSObjectSelectionModel m_link;

    public SponsoredLinksTable(ACSObjectSelectionModel link) {
	super(new SponsoredLinksTableModelBuilder(),
	      new String [] { "Title", "Term", "URL", "", "" });
	
	m_link = link;
	setRowSelectionModel(m_link);

	setDefaultCellRenderer(new SponsoredLinksTableRenderer());
	addTableActionListener(new SponsoredLinksTableActionListener());
    }

    
    public static class SponsoredLinksTableModelBuilder extends LockableImpl implements TableModelBuilder {
	public TableModel makeModel(Table t,
				    PageState s) {
	    ACSObjectCollection links = SponsoredLink.retrieveAll();
            links.addOrder(SponsoredLink.TERM);

	    return new SponsoredLinksTableModel(links);
	}
    }

    public static class SponsoredLinksTableModel implements TableModel {

	private ACSObjectCollection m_links;
	
	public SponsoredLinksTableModel(ACSObjectCollection links) {
	    m_links = links;
	}

	public boolean nextRow() {
	    return m_links.next();
	}

	public int getColumnCount() {
	    return 5;
	}

	public Object getElementAt(int columnIndex) {
	    return m_links.getACSObject();
	}
	public Object getKeyAt(int columnIndex) {
	    return m_links.getACSObject().getID();
	}
    }

    private static class SponsoredLinksTableRenderer implements TableCellRenderer {
        private Label getLabel(String text, PageState state, Table table, Object rowKey) {
            Label label = new Label(text);
            if (table.isSelectedRow(state, rowKey)) {
                label.setFontWeight(Label.BOLD);
            }
            return label;
        }

	public Component getComponent(Table table,
				      PageState state,
				      Object value,
				      boolean isSelected,
				      Object key,
				      int row,
				      int column) {
	    SponsoredLink link = (SponsoredLink) value;
	    
            if (column == 0) {
                return getLabel(link.getTitle(), state, table, key);
            } else if (column == 1) {
                return getLabel(link.getTerm(), state, table, key);
	    } else if (column == 2) {
		return new ExternalLink(getLabel(link.getURL(), state, table, key), link.getURL());
	    } else if (column == 3) {
		return new ControlLink(new Label("[edit]"));
	    } else if (column == 4) {
		return new ControlLink(new Label("[delete]"));
	    }
	    
	    throw new UncheckedWrapperException("Column out of bounds " + column);
	}
    }

    private class SponsoredLinksTableActionListener implements TableActionListener {
	public void cellSelected(TableActionEvent e) {
	    if (e.getColumn().intValue() == 4) {
		SponsoredLink link = (SponsoredLink) m_link.getSelectedObject(e.getPageState());
		link.delete();

                // Clear the selected SponsoredLink so the init listener in SponsoredLinkForm
                // doesn't try to retrieve a deleted object.
                ((Table) e.getSource()).getRowSelectionModel().clearSelection(e.getPageState());
	    }
	}
	public void headSelected(TableActionEvent e) {}
    }
}
