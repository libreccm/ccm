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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.ui.DataQueryTreeModel;
import com.arsdigita.kernel.ui.DataQueryTreeNode;
import com.arsdigita.persistence.GenericDataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * A {@link com.arsdigita.bebop.tree.TreeModelBuilder} that produces trees
 * containing the folder structure underneath a root folder. The root
 * folder can be changed by overriding {@link #getRoot getRoot}.
 *
 * @author <a href="mailto:tri@arsdigita.com">Tri Tran</a>
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 *
 * @version $Id: FolderTreeModelBuilder.java 2183 2011-06-20 00:57:18Z pboy $
 */
public class FolderTreeModelBuilder extends LockableImpl
        implements TreeModelBuilder {

    private static final Logger s_log = Logger.getLogger(FolderTreeModelBuilder.class);
    private PrivilegeDescriptor TREE_DESCRIPTOR =
            SecurityManager.CMS_PREVIEW_ITEM_DESCRIPTOR;

    /**
     * Make a tree model that lists the hierarchy of folders underneath the
     * folder returnedby {@link #getRoot getRoot}.
     *
     * @param t the tree in which the model is used
     * @param s represents the current request
     * @return a tree model that lists the hierarchy of folders underneath the
     * folder returnedby {@link #getRoot getRoot}.
     */
    @Override
    public com.arsdigita.bebop.tree.TreeModel makeModel(final Tree t, PageState s) {
        return new DataQueryTreeModel(getRoot(s).getID(),
                "com.arsdigita.cms.getRootFolder",
                "com.arsdigita.cms.getSubFolders") {

            @Override
            public Iterator getChildren(TreeNode node, PageState data) {
                String nodeKey = node.getKey().toString();

                // Always expand root node
                if (nodeKey.equals(getRoot(data).getKey().toString()) && t.isCollapsed(nodeKey, data)) {
                    t.expand(nodeKey, data);
                }

                if (t.isCollapsed(nodeKey, data)) {
                    return Collections.EMPTY_LIST.iterator();
                }
                Party party = Kernel.getContext().getParty();
                OID partyOID = null;
                if (party == null) {
                    partyOID = new OID(User.BASE_DATA_OBJECT_TYPE,
                            PermissionManager.VIRTUAL_PUBLIC_ID);
                } else {
                    partyOID = party.getOID();
                }
                UniversalPermissionDescriptor universalPermission =
                        new UniversalPermissionDescriptor(SecurityManager.CMS_PREVIEW_ITEM_DESCRIPTOR, partyOID);
                if (PermissionService.checkPermission(universalPermission)) {
                    // the person is an admin so we just pass in the
                    // standard, non filtered query
                    return getDataQueryTreeIterator(
                            (DataQueryTreeNode) node,
                            "com.arsdigita.cms.getSubFolders");
                } else {
                    // now we need to set the parameters
                    return new NewFolderBrowserIterator(
                            (DataQueryTreeNode) node,
                            partyOID);
                }
            }
        };
    }

    /**
     * Retrn the root folder for the tree model in the current
     * request.
     * @param s represents the current request
     * @return the root folder for the tree
     * @post return != null
     */
    protected Folder getRoot(PageState s)
            throws IllegalStateException {
        ContentSection sec = CMS.getContext().getContentSection();
        return sec.getRootFolder();
    }

    private class NewFolderBrowserIterator implements Iterator {

        private DataQuery m_nodes;

        public NewFolderBrowserIterator(DataQueryTreeNode node, OID partyOID) {

            BigDecimal userID = (BigDecimal) partyOID.get("id");

            String sql = ""
                    + "\n    select f.folder_id as id,"
                    + "\n           f.label as name,"
                    + "\n           count(sub.item_id) as nchild"
                    + "\n    from cms_folders f,"
                    + "\n         cms_items i"
                    + "\n         left join"
                    + "\n             (select i2.item_id, f2.label as name, i2.parent_id"
                    + "\n                from cms_folders f2,"
                    + "\n                     cms_items i2"
                    + "\n               where f2.folder_id = i2.item_id) sub"
                    + "\n           on (sub.parent_id = i.item_id"
                    + "\n             and"
                    + "\n             exists (select 1"
                    + "\n                          from dnm_object_1_granted_context dogc,"
                    + "\n                               dnm_granted_context dgc,"
                    + "\n                               dnm_permissions dp,"
                    + "\n                               dnm_group_membership dgm"
                    + "\n                           where dogc.pd_object_id = sub.item_id"
                    + "\n                             and dogc.pd_context_id = dgc.pd_object_id"
                    + "\n                             and dgc.pd_context_id = dp.pd_object_id"
                    + "\n                             and dp.pd_grantee_id = dgm.pd_group_id"
                    + "\n                             and dgm.pd_member_id in (-200," + userID + ",-202)"
                    + "\n                             and dp." + TREE_DESCRIPTOR.getColumnName() + " = '1'"
                    + "\n                             ) )"
                    + "\n    where i.parent_id = " + node.getID()
                    + "\n      and f.folder_id = i.item_id"
                    + "\n      and  exists ("
                    + "\n            select 1 as permission_p"
                    + "\n              from dnm_object_1_granted_context dogc,"
                    + "\n                   dnm_granted_context dgc,"
                    + "\n                   dnm_permissions dp,"
                    + "\n                   dnm_group_membership dgm"
                    + "\n              where dogc.pd_context_id = dgc.pd_object_id"
                    + "\n                and dgc.pd_context_id = dp.pd_object_id"
                    + "\n                and dgm.pd_member_id in (-200," + userID + ",-202)"
                    + "\n                and dp.pd_grantee_id = dgm.pd_group_id"
                    + "\n                and dogc.pd_object_id = f.folder_id"
                    + "\n                and dp." + TREE_DESCRIPTOR.getColumnName() + " = '1' )"
                    + "\n    group by f.label, f.folder_id"
                    + "\n    order by lower(f.label)";

            if (s_log.isDebugEnabled()) {
                s_log.debug("Custom SQL: \n" + sql);
            }

            m_nodes = new GenericDataQuery(
                    SessionManager.getSession(),
                    sql,
                    new String[]{"id", "name", "nchild"});
        }

        @Override
        public Object next() {
            BigDecimal id = new BigDecimal(0);
            try {
                // this appears to be the only portable way to dig numbers out
                // of the result set
                id = new BigDecimal(m_nodes.get("id").toString());
            } catch (NumberFormatException nfe) {
            }
            String name = m_nodes.get("name").toString();
            BigDecimal count = new BigDecimal(0);
            try {
                count = new BigDecimal(m_nodes.get("nchild").toString());
            } catch (NumberFormatException nfe) {
            }

            return new DataQueryTreeNode(id, name, count.intValue() > 0);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("cannot remove nodes via iterator");
        }

        @Override
        public boolean hasNext() {
            return m_nodes.next();
        }
    }
}
