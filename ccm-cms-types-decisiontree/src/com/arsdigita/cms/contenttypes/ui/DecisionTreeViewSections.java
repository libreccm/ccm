/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeUtil;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.util.Assert;


/**
 * Authoring kit step to manage the sections for a DecisionTree.
 * The editing process is implemented with three main visual components
 * that manipulate the currently selected DecisionTree and sections.
 * The visibility of these components is managed by this class.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeViewSections extends ResettableContainer
{
	/** id keys for each editing panel */
    public static final String SECTION_TABLE = "sec_tbl";
    public static final String SECTION_EDIT  = "sec_edt";
    public static final String SECTION_DEL   = "sec_del";

    /** class attributes */
    public static final String DATA_TABLE    = "dataTable";
    public static final String ACTION_LINK   = "actionLink";

    protected AuthoringKitWizard m_wizard;
    protected ItemSelectionModel m_selTree;
    protected ItemSelectionModel m_selSection;

    /** visual components that do the 'real work' */
    protected DecisionTreeSectionTable        m_sectionTable;
    protected DecisionTreeSectionEditForm     m_sectionEdit;
    protected DecisionTreeSectionDeleteForm   m_sectionDelete;

    private String m_typeIDStr;

    public DecisionTreeViewSections (ItemSelectionModel selTree, 
                                     AuthoringKitWizard wizard) {
        super();
        m_selTree = selTree;
        m_wizard = wizard;
        m_typeIDStr = wizard.getContentType().getID().toString();
        Assert.exists(m_selTree, ItemSelectionModel.class);

        // create the components and set default visibility
        add(buildSectionTable(), true);
        add(buildSectionEdit(), false);
        add(buildSectionDelete(), false);
    }

    /**
     * Builds a container to hold a SectionTable and a link
     * to add a new section.
     */
    protected Container buildSectionTable () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(SECTION_TABLE+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        m_sectionTable = new DecisionTreeSectionTable(m_selTree);
        m_sectionTable.setClassAttr(DATA_TABLE);

        // selected section is based on the selection in the SectionTable
        m_selSection = new ItemSelectionModel(DecisionTreeSection.class.getName(),
                                              DecisionTreeSection.BASE_DATA_OBJECT_TYPE,
                                              m_sectionTable.getRowSelectionModel());

        m_sectionTable.setSectionModel(m_selSection);

        Label emptyView = new Label(DecisionTreeUtil
                                    .globalize("tree_sections.no_sections_yet"));
        m_sectionTable.setEmptyView(emptyView);

        // handle clicks to delete a Section
        m_sectionTable.addTableActionListener (new TableActionListener () {
            public void cellSelected (TableActionEvent event) {
                PageState state = event.getPageState();

                TableColumn col = m_sectionTable.getColumnModel()
                        .get(event.getColumn().intValue());
                String colName = (String)col.getHeaderValue();

                if (DecisionTreeSectionTable.COL_DEL.equals(colName)) {
                    onlyShowComponent(state, SECTION_DEL+m_typeIDStr);
                } else if (DecisionTreeSectionTable.COL_EDIT.equals(colName)) {
                    onlyShowComponent(state, SECTION_EDIT+m_typeIDStr);
                } else if (DecisionTreeSectionTable.COL_FIRST.equals(colName)) {
                    DecisionTree tree = (DecisionTree)m_selTree
                                        .getSelectedObject(state);
                    DecisionTreeSection section = (DecisionTreeSection)m_selSection
                                                  .getSelectedItem(state);
                    tree.setFirstSection(section);
                    tree.save();
                }
            }

            public void headSelected(TableActionEvent e) {}
        });
        c.add(m_sectionTable);

        // link to add new section
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold a SectionEditForm and a link
     * to return to the section list.
     */
    protected Container buildSectionEdit () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(SECTION_EDIT+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        // display an appropriate title
        c.add( new Label( new PrintListener() {
                public void prepare ( PrintEvent event ) {
                    PageState state = event.getPageState();
                    Label label = (Label)event.getTarget();

                    if (m_selSection.getSelectedKey(state) == null) {
                        label.setLabel(DecisionTreeUtil
                                      .globalize("tree_sections.add_section"));
                    } else {
                        label.setLabel(DecisionTreeUtil
                                       .globalize("tree_sections.edit_section"));
                    }
                }
            }));

        // form to edit a Section
        m_sectionEdit = new DecisionTreeSectionEditForm(m_selTree, m_selSection, this);
        c.add(m_sectionEdit);

        c.add(buildViewAllLink());
        // link to add new section
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold the component to confirm
     * deletion of a section.
     */
    protected Container buildSectionDelete () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(SECTION_DEL+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        c.add(new Label(DecisionTreeUtil.globalize("tree_sections.delete_section")));
        m_sectionDelete = new DecisionTreeSectionDeleteForm(m_selTree, m_selSection);
        m_sectionDelete.addSubmissionListener ( new FormSubmissionListener () {
                public void submitted ( FormSectionEvent e ) {
                    PageState state = e.getPageState();
                    onlyShowComponent(state, SECTION_TABLE+m_typeIDStr);
                }
            });
        c.add(m_sectionDelete);

        c.add(buildViewAllLink());

        return c;
    }

    /**
     * Utility method to create a link to display the section list.
     */
    protected ActionLink buildViewAllLink () {
        ActionLink viewAllLink = new ActionLink(DecisionTreeUtil
                                     .globalize("tree_sections.view_all_sections"));
        viewAllLink.setClassAttr(ACTION_LINK);
        viewAllLink.addActionListener( new ActionListener() {
                public void actionPerformed ( ActionEvent event ) {
                    onlyShowComponent(event.getPageState(), SECTION_TABLE+m_typeIDStr);
                }
            });

        return viewAllLink;
    }

    /**
     * Utility method to create a link to display the section list.
     */
    protected ActionLink buildAddLink () {
        ActionLink addLink = new ActionLink(DecisionTreeUtil
                                 .globalize("tree_sections.add_new_section")) {
            @Override
            public boolean isVisible(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = (ContentItem)m_selTree.getSelectedObject(state);
                    
                return super.isVisible(state) &&
                       sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM,
                                    item);
                    
            }
        };
        addLink.setClassAttr(ACTION_LINK);
        addLink.addActionListener( new ActionListener () {
                public void actionPerformed ( ActionEvent event ) {
                    PageState state = event.getPageState();
                    m_selSection.clearSelection(state);
                    onlyShowComponent(state, SECTION_EDIT+m_typeIDStr);
                }
            });

        return addLink;
    }

    public String getTypeIDStr() {
        return m_typeIDStr;
    }
}
