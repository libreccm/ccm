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
import com.arsdigita.london.search.Server;
import com.arsdigita.london.search.ServerCollection;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.ExternalLink;



public class ServersTable extends Table {

    private ServerSelectionModel m_server;

    public ServersTable(ServerSelectionModel server) {
	super(new ServersTableModelBuilder(),
	      new String [] { "Title", "URL", "", "" });
	
	m_server = server;
	setRowSelectionModel(m_server);

	setDefaultCellRenderer(new ServersTableRenderer());
	addTableActionListener(new ServersTableActionListener());
    }

    
    public static class ServersTableModelBuilder extends LockableImpl implements TableModelBuilder {
	public TableModel makeModel(Table t,
				    PageState s) {
	    ServerCollection servers = Server.retrieveAll();

	    return new ServersTableModel(servers);
	}
    }

    public static class ServersTableModel implements TableModel {

	private ServerCollection m_servers;
	
	public ServersTableModel(ServerCollection servers) {
	    m_servers = servers;
	}

	public boolean nextRow() {
	    return m_servers.next();
	}

	public int getColumnCount() {
	    return 3;
	}

	public Object getElementAt(int columnIndex) {
	    return m_servers.getServer();
	}
	public Object getKeyAt(int columnIndex) {
	    return m_servers.getServer().getID();
	}
    }

    private static class ServersTableRenderer implements TableCellRenderer {
	public Component getComponent(Table table,
				      PageState state,
				      Object value,
				      boolean isSelected,
				      Object key,
				      int row,
				      int column) {
	    Server server = (Server)value;
	    
	    if (column == 0) {
		return new ExternalLink(isSelected ? 
					new Label(server.getTitle(),
						  Label.BOLD) :
					new Label(server.getTitle()),
					server.getHostname());
	    } else if (column == 1) {
		return new Label(server.getHostname());
	    } else if (column == 2) {
		return new ControlLink(new Label("[edit]"));
	    } else if (column == 3) {
		return new ControlLink(new Label("[delete]"));
	    }
	    
	    throw new UncheckedWrapperException("Column out of bounds " + column);
	}
    }

    private class ServersTableActionListener implements TableActionListener {
	public void cellSelected(TableActionEvent e) {
	    if (e.getColumn().intValue() == 3) {
		Server server = m_server.getSelectedServer(e.getPageState());
		server.delete();

                // Clear the selected Server so the init listener in ServerForm
                // doesn't try to retrieve a deleted object.
                ((Table) e.getSource()).getRowSelectionModel().clearSelection(e.getPageState());
	    }
	}
	public void headSelected(TableActionEvent e) {}
    }
}
