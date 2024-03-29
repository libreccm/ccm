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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.TreeExpansionEvent;
import com.arsdigita.bebop.event.TreeExpansionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * A pane that contains a folder tree on the left and a folder
 * manipulator on the right.
 *
 * @author David LutterKort &lt;dlutter@redhat.com&gt;
 * @version $Id: ItemSearchBrowsePane.java 1166 2006-06-14 11:45:15Z fabrice $
 */
public class ItemSearchBrowsePane extends SimpleContainer
        implements Resettable, TreeExpansionListener, ChangeListener,
                   FormProcessListener, FormSubmissionListener {

    private static final String CONTENT_TYPE_ID = "ct";
    private static final Logger s_log =
                                Logger.getLogger(ItemSearchBrowsePane.class);
    private FolderSelectionModel m_folderSel;
    private FolderRequestLocal m_folder;
    private Tree m_tree;
    private ItemSearchFolderBrowser m_browser;
    private SingleSelect m_section;
//    private NewItemForm m_newItem;
    private SingleSelectionModel m_typeSel;

    public ItemSearchBrowsePane() {

        final LayoutPanel mainPanel = new LayoutPanel();
        
        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title",
                     GlobalizationUtil.globalize("cms.ui.folder_browser").localize().toString());

        final BoxPanel left = new BoxPanel(BoxPanel.VERTICAL);
        
        Label label = new Label(GlobalizationUtil.globalize("cms.ui.folder_browser"));
        label.setClassAttr("heading");
        left.add(label);

        // As described in ticket 20540, some clients do not want the option to pick items from other 
        // subsites through the ItemSearchBrowsePane.  A new parameter has been added to allow the 
        // administrator to pick between the old and new versions.
        boolean linksOnlyInSameSubsite = ContentSection.getConfig().getLinksOnlyInSameSubsite();
        s_log.debug("linksOnlyInSameSubsite value is " + linksOnlyInSameSubsite);

        m_tree = new Tree(new FolderTreeModelBuilder() {
            @Override
            protected Folder getRoot(PageState ps) {
                Folder root = getRootFolder(ps);

                if (null == root) {
                    return super.getRoot(ps);
                }
                return root;
            }

        });
        m_folderSel = createFolderSelectionModel();
        m_folderSel.addChangeListener(this);
        m_folder = new FolderRequestLocal(m_folderSel);

        if (!linksOnlyInSameSubsite) {
            // The client should be able to pick between the subsites
            Form sectionForm = getSectionForm();
            add(sectionForm);
        }

        m_tree.setSelectionModel(m_folderSel);

        m_tree.setClassAttr("navbar");
        m_tree.addTreeExpansionListener(this);
        left.add(m_tree);

//        CMSContainer container = new CMSContainer();        
        left.setClassAttr("main");

        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);
        m_browser = new ItemSearchFolderBrowser(m_folderSel);
        body.add(m_browser);
        body.add(m_browser.getPaginator());

//        m_newItem = new SectionNewItemForm("newItem");
//        m_typeSel = new ParameterSingleSelectionModel(new BigDecimalParameter(CONTENT_TYPE_ID));
//        m_newItem.addProcessListener(this);
//
//        container.add(m_newItem);

        //add(container);
        mainPanel.setLeft(left);
        mainPanel.setBody(body);
        add(mainPanel);
    }

    @Override
    public boolean isVisible(PageState s) {
        // Always expand root node
        if (m_tree.isCollapsed(getRootFolder(s).getID().toString(), s)) {
            m_tree.expand(getRootFolder(s).getID().toString(), s);
        }

        return super.isVisible(s);
    }

    private Form getSectionForm() {
        Form sectionForm = new Form("isfbSectionForm",
                                    new BoxPanel(BoxPanel.HORIZONTAL));
        sectionForm.setClassAttr("navbar");

        m_section = new SingleSelect(new OIDParameter("isfbSection"));
        ContentSectionCollection sections = ContentSection.getAllSections();
        while (sections.next()) {
            ContentSection section = sections.getContentSection();
            m_section.addOption(new Option(section.getOID().toString(),
                                           section.getDisplayName()));
        }

        sectionForm.addInitListener(new FormInitListener() {
            @Override
            public void init(FormSectionEvent ev) {
                PageState ps = ev.getPageState();

                if (null == m_section.getValue(ps)) {
                    ContentSection section = CMS.getContext().getContentSection();
                    m_section.setValue(ps, section.getOID());
                }
            }

        });

        sectionForm.add(m_section);
        sectionForm.add(new Submit("Change Section"));

        return sectionForm;
    }

    private Folder getRootFolder(PageState ps) {
        s_log.debug("Getting the root folder.");
        if (m_section != null) {
            // We have more than one subsite to choose between
            OID sectionOID = (OID) m_section.getValue(ps);
            if (s_log.isDebugEnabled()) {
                if (null != sectionOID) {
                    s_log.debug("Using section " + sectionOID.toString());
                } else {
                    s_log.debug("Using default section");
                }
            }

            if (null == sectionOID) {
                return null;
            }

            ContentSection section = (ContentSection) DomainObjectFactory.newInstance(sectionOID);

            return section.getRootFolder();
        } else {
            return null;
        }
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_folderSel.getStateParameter());

        // Only add the SingleSelect item if it exists
        if (m_section != null) {
            p.addComponentStateParam(this, m_section.getParameterModel());
        }

        // Save the state of the new item component
//        p.addComponentStateParam(this, m_typeSel.getStateParameter());

        p.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final PageState state = e.getPageState();

                if (state.isVisibleOnPage(ItemSearchBrowsePane.this)) {
                    showHideSegments(state);
                }
            }

        });
    }

    /**
     * Show/hide segments based on access checks.
     *
     * @param state The page state
     * @pre ( state != null )
     */
    private void showHideSegments(PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        Folder folder = m_folder.getFolder(state);
        Assert.exists(folder);

        // MP: This should be checked on the current folder instead of just
        //     the content section.
//        boolean newItem =
//                sm.canAccess(state.getRequest(), SecurityManager.NEW_ITEM, folder);
//
//        if (!newItem) {
//            browseMode(state);
//        }
//        m_newItem.setVisible(state, newItem);
    }

    @Override
    public void reset(PageState s) {
        //m_browser.reset(s);
    }

    public ItemSearchFolderBrowser getFolderBrowser() {
        return m_browser;
    }

    public final FolderSelectionModel getFolderSelectionModel() {
        return m_folderSel;
    }

    /**
     * sets the current level of expansion of the folder tree
     * and in the folder browser table
     */
    protected void setSelectedFolder(PageState s, String key) {

        //set the selected folder of the folder browser
        m_browser.getFolderSelectionModel().setSelectedKey(s, key);

        //set the selected folder of the folder tree
        m_folderSel.setSelectedKey(s, key);
        Folder current = (Folder) m_folderSel.getSelectedObject(s);
        Folder parent = (Folder) current.getParent();
        if (parent != null) {
            BigDecimal id = parent.getID();
            m_tree.expand(id.toString(), s);
        }
    }

    // Implement TreeExpansionListener
    @Override
    public void treeCollapsed(TreeExpansionEvent e) {
        PageState s = e.getPageState();
        m_folderSel.setSelectedKey(s, e.getNodeKey());
    }

    @Override
    public void treeExpanded(TreeExpansionEvent e) {
        return;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        PageState s = e.getPageState();
        Folder current = (Folder) m_folderSel.getSelectedObject(s);
        Folder parent = (Folder) current.getParent();
        m_browser.getPaginator().reset(s);
        if (parent != null) {
            BigDecimal id = parent.getID();
            m_tree.expand(id.toString(), s);
        }
        //m_browser.getPermissionsPane().reset(s);
        //m_browser.setPermissionLinkVis(s);
    }

    @Override
    public void process(FormSectionEvent e) {
        PageState s = e.getPageState();
        final Object source = e.getSource();
//        if (source == m_newItem) {
//            BigDecimal typeID = m_newItem.getTypeID(s);
//            m_typeSel.setSelectedKey(s, typeID);
//            newItemMode(s);
//        } else {
        browseMode(s);
//        }
    }

    @Override
    public void submitted(FormSectionEvent e) {
        PageState s = e.getPageState();
        final Object source = e.getSource();
//        if (source == m_newItem) {
//            BigDecimal typeID = m_newItem.getTypeID(s);
//            m_typeSel.setSelectedKey(s, typeID);
//            //newItemMode(s);
//        }
    }

    private void browseMode(PageState s) {
//        m_browseSeg.setVisible(s, true);
        m_typeSel.clearSelection(s);
    }

    private void newItemMode(PageState s) {
//        m_newItemSeg.setVisible(s, true);
    }

    private FolderSelectionModel createFolderSelectionModel() {
        return new FolderSelectionModel("folder") {
            @Override
            protected BigDecimal getRootFolderID(PageState ps) {
                Folder root = getRootFolder(ps);

                if (null == root) {
                    return super.getRootFolderID(ps);
                }
                return root.getID();
            }

        };
    }

    private static class SectionNewItemForm extends NewItemForm {

        public SectionNewItemForm(String name) {
            super(name);
        }

        @Override
        public ContentSection getContentSection(PageState s) {
            return CMS.getContext().getContentSection();
        }

    }
}
