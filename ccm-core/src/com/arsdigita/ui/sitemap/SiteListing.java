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
package com.arsdigita.ui.sitemap;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.ui.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

public class SiteListing extends BoxPanel
    implements ChangeListener, ActionListener {

    public static final String versionId = "$Id: SiteListing.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(SiteListing.class);

    private List m_list;

    private Tree m_tree;

    private ToggleLink m_addLink;

    public SiteListing () {
        super(BoxPanel.VERTICAL, false);
        SiteNode sn;
        try {
            sn = SiteNode.getSiteNode("/");
            m_tree = new Tree(new SitemapTreeModelBuilder());
            add(new Label(GlobalizationUtil.globalize("ui.sitemap.h4sitemap_treeh4"),  false));
            add(m_tree);
            m_tree.addChangeListener(this);
        } catch(com.arsdigita.domain.DataObjectNotFoundException e) {
            s_log.warn ("Problem Creating Site Map Tree");
            e.printStackTrace();
        }

        m_addLink = new ToggleLink(new Label(GlobalizationUtil.globalize("ui.sitemap.configure_sitemap_admin_page")));
        add(m_addLink);
        Label l = new Label(GlobalizationUtil.globalize("ui.sitemap.configure_sitemap_admin_page"));
        l.setFontWeight(Label.BOLD);
        m_addLink.setSelectedComponent(l);
        m_addLink.addActionListener(this);

    }

    public List getList() {
        return m_list;
    }

    public Tree getTree() {
        return m_tree;
    }

    public ToggleLink getCFGLink() {
        return m_addLink;
    }

    // List selection has changed
    public void stateChanged(ChangeEvent e) {
        PageState s = e.getPageState();
        if ( m_tree.isSelected(s) ) {
            m_addLink.setSelected(s, false);
        }
    }

    // Toggle link has been clicked
    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        if ( m_addLink.isSelected(s) ) {
            m_tree.clearSelection(s);
        }
    }

    private class SitemapTreeModelBuilder extends LockableImpl
        implements TreeModelBuilder {

        public com.arsdigita.bebop.tree.TreeModel makeModel(Tree t, PageState s) {
            return new SiteNodeTreeModel(SiteNode.getRootSiteNode());
        }
    }
}
