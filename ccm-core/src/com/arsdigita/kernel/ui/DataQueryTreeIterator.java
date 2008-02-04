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
package com.arsdigita.kernel.ui;



import java.util.Iterator;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

/**
 * 
 * This class iterates through the query, setting up the correct values
 * for the tree node
 *
 * @author Daniel Berrange 
 * @author Randy Graebner
 *
 * @version $Id: DataQueryTreeIterator.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DataQueryTreeIterator implements Iterator  {
    protected DataQuery m_nodes;
    
    public DataQueryTreeIterator(DataQueryTreeNode node, 
                                 String getSubCategories) {
        Session session = SessionManager.getSession();
        m_nodes = session.retrieveQuery(getSubCategories);
        m_nodes.setParameter("objectID", node.getID());
    }

    public void filterQuery(String propertyName,
                            PrivilegeDescriptor privilege,
                            OID userOID) {
        PermissionService.filterQuery(m_nodes, propertyName, privilege, userOID);
    }

    /**
     *  This allows the calling class to use data queries that
     *  need extra variables set.  This is used by FolderTreeModelBuilder
     *  in CMS but is generally useful so it has been added here.
     */
    public void setParameter(String parameterName, Object parameterValue) {
        m_nodes.setParameter(parameterName, parameterValue);
    }

    /**
     *  This allows the calling class to order the results of the data query
     */
    public void addOrder(String parameterName) {
        m_nodes.addOrder(parameterName);
    }

    public Object next() {
        BigDecimal id = (BigDecimal)m_nodes.get("id");
        String name = (String)m_nodes.get("name");
        BigDecimal count = (BigDecimal)m_nodes.get("nchild");
        
        return new DataQueryTreeNode(id, name, count.intValue() > 0);
    }
    
    public void remove() {
        throw new UnsupportedOperationException
            ("cannot remove categories via iterator");
    }
    
    public boolean hasNext() {
        return m_nodes.next();
    }
}
