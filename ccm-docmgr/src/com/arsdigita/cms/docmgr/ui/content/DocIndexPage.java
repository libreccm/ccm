/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui.content;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.cms.docmgr.ui.tree.DocFolderContentTreeRenderer;
import com.arsdigita.cms.docmgr.ui.tree.DocFoldersContentTree;
import com.arsdigita.cms.docmgr.ui.tree.DocFoldersContentTreeModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

/**
 * <p>A public page that displays all items in the current content
 * section, and allows to search or browse them.</p>
 *
 * @author Stanislav Freidin
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/content/DocIndexPage.java#1 $
 */
public class DocIndexPage extends CMSPage implements RequestListener {
    public static final String versionId =
        "$Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/content/DocIndexPage.java#1 $" +
        "$Author: cwolfe $" +
        "$DateTime: 2003/11/26 14:30:10 $";

    private static final Logger s_log = Logger.getLogger(DocIndexPage.class);

    private DocFoldersContentTree m_foldersTree;

    public DocIndexPage() {
        super(new Label(new TitlePrinter()), new SimpleContainer());

        add(new DocFolderNavbar());
        
        BoxPanel m_mainContainer = new BoxPanel(BoxPanel.HORIZONTAL);
        //m_mainContainer.setClassAttr("sidebarNavPanel");

        m_foldersTree = new DocFoldersContentTree(new DocFoldersContentTreeModel());
        m_foldersTree.setCellRenderer(new DocFolderContentTreeRenderer());
        BoxPanel leftSide = new BoxPanel();
        //leftSide.setClassAttr("navbar"); // need this?
        leftSide.add(m_foldersTree);
        m_mainContainer.add(leftSide);

        BoxPanel rightSide = new BoxPanel();
        rightSide.setClassAttr("main");  // need this?
        rightSide.add(new BundleList());
        m_mainContainer.add(rightSide);
        
        //add(new BundleList());

        add(m_mainContainer);

        addRequestListener(this);
    }

    public void pageRequested(RequestEvent e) {
        s_log.debug("PAGE IS REQUESTED");
        PageState state = e.getPageState();
        //m_foldersTree.setSelectedKey
        //    (state,
        //     CMS.getContext().getContentItem().getPath());
        m_foldersTree.expandPath(state);
    }

    private static class TitlePrinter implements PrintListener {
        public final void prepare(final PrintEvent e) {
            final Label label = (Label) e.getTarget();

            if (CMS.getContext().hasContentItem()) {
                DocFolder df = (DocFolder) 
                    CMS.getContext().getContentItem();
                if (df.isRoot()) {
                    df = (DocFolder) df.getWorkingVersion();
                    Repository rep = DocFolder.getRepository(df);
                    if (rep == null) {
                        s_log.error("repository is null");
                    } else {
                        label.setLabel(rep.getDisplayName());
                    }
                } else {
                    label.setLabel(df.getTitle());
                }
            }
        }
    }

    private class BundleList extends List {
        BundleList() {
            super(new BundleListModelBuilder());

            setCellRenderer(new CellRenderer());
        }

        public final boolean isVisible(final PageState state) {
            return CMS.getContext().hasContentItem() && super.isVisible(state);
        }
    }


    private static class BundleListModelBuilder
            extends AbstractListModelBuilder {
        public final ListModel makeModel(final List list,
                                         final PageState state) {
            final Folder parent = (Folder) CMS.getContext().getContentItem();

            final DataCollection bundles =
                SessionManager.getSession().retrieve(ContentBundle.BASE_DATA_OBJECT_TYPE);

            bundles.addEqualsFilter(ContentItem.PARENT, parent.getID());
            bundles.addOrder("lower(" + ACSObject.DISPLAY_NAME + ")");

            return new Model(new ItemCollection(bundles));
        }

        private class Model implements ListModel {
            private final ItemCollection m_items;

            Model(final ItemCollection items) {
                m_items = items;
            }

            public final boolean next() {
                if (m_items.next()) {
                    return true;
                } else {
                    m_items.close();

                    return false;
                }
            }

            public final Object getElement() {
                return m_items.getDisplayName();
            }

            public final String getKey() {
                return m_items.getName();
            }
        }
    }

    private class CellRenderer implements ListCellRenderer {
        public final Component getComponent(final List list,
                                            final PageState state,
                                            final Object value,
                                            final String key,
                                            final int index,
                                            final boolean isSelected) {
            Link l = new Link((String) value, key);
            l.setTargetFrame("docview");
            return l;
            
        }
    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }
}
