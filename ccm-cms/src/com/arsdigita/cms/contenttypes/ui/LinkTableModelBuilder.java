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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

/**
 * Reusable TableModel for displaying Links associated to a
 * ContentItem
 *
 * @version $Revision: #4 $ $Date: 2004/08/17 $
 * @author Nobuko Asakai (nasakai@redhat.com)
 */

public abstract class LinkTableModelBuilder 
    extends LockableImpl implements TableModelBuilder {
    private static final Logger s_log = 
        Logger.getLogger(LinkTableModelBuilder.class);


    /**
     * Creates the LinKTableModel based on the current table and pagestate
     *
     * @param t The <code>Table</code> for the current page.
     * @param s The <code>PageState</code> for the current request
     */
    public TableModel makeModel(Table t,
                                PageState s) {
        s_log.debug("LinkTableModelBuilder.makemodel");

        DataCollection links = getLinks(s);

        if ( links.isEmpty() ) {
            return Table.EMPTY_MODEL;
        } else {
            return new LinkTableModel(links);
        }
    }

    /**
     * Returns the DataCollection of Links for the current
     * TableModel. Not implemented for LinkTableModelBuilder  
     *
     * @param s The <code>PageState</code> for the current request
     * @return The DataCollection of Links
     */
    public abstract DataCollection getLinks(PageState s);

    /**
     * TableModel implementation for Links
     */
    public static class LinkTableModel implements TableModel {
    
        Link m_link;
        DataCollection m_links;
        public LinkTableModel(DataCollection links) {
            m_links = links;
            m_link = null;
        }

        public boolean nextRow() {
            if (m_links.next()) {
                DataObject object = m_links.getDataObject();
                m_link = (Link)DomainObjectFactory.newInstance(object);
                return true;
            } else {
                return false;
            }
        }

        public int getColumnCount() {
            return (int)m_links.size();

        }

        public Object getElementAt(int columnIndex) {
            return m_link;
        }

        public Object getKeyAt(int columnIndex) {
            return m_link.getID();
        }
        public long size() {
	    return m_links.size();
	}
    }
}
