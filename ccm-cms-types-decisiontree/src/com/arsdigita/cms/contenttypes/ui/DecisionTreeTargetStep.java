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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.util.Assert;


/**
 * Authoring kit step to manage the targets for a DecisionTree.
 * The editing process is implemented with three main visual components
 * that manipulate the currently selected DecisionTree and targets.
 * The visibility of these components is managed by this class.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeTargetStep extends ResettableContainer
{
	/** id keys for each editing panel */
    public static final String TARGET_TABLE = "tgt_tbl";
    public static final String TARGET_EDIT  = "tgt_edt";
    public static final String TARGET_DEL   = "tgt_del";

    /** class attributes */
    public static final String DATA_TABLE   = "dataTable";
    public static final String ACTION_LINK  = "actionLink";

    protected AuthoringKitWizard m_wizard;
    protected ItemSelectionModel m_selTree;
    protected ItemSelectionModel m_selTarget;

    /** visual components that do the 'real work' */
    protected DecisionTreeTargetTable       m_targetTable;
    protected DecisionTreeTargetEditForm    m_targetEdit;
    protected DecisionTreeTargetDeleteForm	m_targetDelete;

    private String m_typeIDStr;

    /**
     * 
     * @param selTree
     * @param wizard 
     */
    public DecisionTreeTargetStep (ItemSelectionModel selTree, 
                                    AuthoringKitWizard wizard) {
        super();
        m_selTree = selTree;
        m_wizard = wizard;
        m_typeIDStr = wizard.getContentType().getID().toString();
        Assert.exists(m_selTree, ItemSelectionModel.class);

        // create the components and set default visibility
        add(buildTargetTable(), true);
        add(buildTargetEdit(), false);
        add(buildTargetDelete(), false);
    }

    /**
     * Builds a container to hold a TargetTable and a link
     * to add a new target.
     */
    protected Container buildTargetTable () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(TARGET_TABLE+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        m_targetTable = new DecisionTreeTargetTable(m_selTree);
        m_targetTable.setClassAttr(DATA_TABLE);

        // selected section is based on the selection in the SectionTable
        m_selTarget = new ItemSelectionModel(DecisionTreeSection.class.getName(),
                                              DecisionTreeSection.BASE_DATA_OBJECT_TYPE,
                                              m_targetTable.getRowSelectionModel());

        m_targetTable.setSectionModel(m_selTarget);

        Label emptyView = new Label(DecisionTreeGlobalizationUtil.globalize(
                                    "cms.contenttypes.ui.decisiontree.targets.none_yet"));
        m_targetTable.setEmptyView(emptyView);

        // handle clicks to preview or delete a Target
        m_targetTable.addTableActionListener (new TableActionListener () {
                public void cellSelected (TableActionEvent event) {
                    PageState state = event.getPageState();
                    int col = event.getColumn();

                    if ( col == DecisionTreeTargetTable.COL_IDX_DEL ) {
                        onlyShowComponent(state, TARGET_DEL+m_typeIDStr);
                    } else if ( col == DecisionTreeTargetTable.COL_IDX_EDIT ) {
                        onlyShowComponent(state, TARGET_EDIT+m_typeIDStr);
                    }
                }

				public void headSelected(TableActionEvent e) {}
            });
        c.add(m_targetTable);

        // link to add new target
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold a SectionEditForm and a link
     * to return to the section list.
     */
    protected Container buildTargetEdit () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(TARGET_EDIT+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        // display an appropriate title
        c.add( new Label( new PrintListener() {
                public void prepare ( PrintEvent event ) {
                    PageState state = event.getPageState();
                    Label label = (Label)event.getTarget();

                    if (m_selTarget.getSelectedKey(state) == null) {
                        label.setLabel(DecisionTreeGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.decisiontree.targets.add"));
                    } else {
                        label.setLabel(DecisionTreeGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.decisiontree.targets.edit"));
                    }
                }
            }));

        // form to edit a Target
        m_targetEdit = new DecisionTreeTargetEditForm(m_selTree, m_selTarget, this);
        c.add(m_targetEdit);

        c.add(buildViewAllLink());
        // link to add new target
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold the component to confirm
     * deletion of a target.
     */
    protected Container buildTargetDelete () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(TARGET_DEL+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        c.add(new Label(DecisionTreeGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.decisiontree.targets.delete")));
        m_targetDelete = new DecisionTreeTargetDeleteForm(m_selTree, m_selTarget);
        m_targetDelete.addSubmissionListener ( new FormSubmissionListener () {
                public void submitted ( FormSectionEvent e ) {
                    PageState state = e.getPageState();
                    onlyShowComponent(state, TARGET_TABLE+m_typeIDStr);
                }
            });
        c.add(m_targetDelete);

        c.add(buildViewAllLink());

        return c;
    }

    /**
     * Utility method to create a link to display the targets list.
     */
    protected ActionLink buildViewAllLink () {
        ActionLink viewAllLink = new 
            ActionLink( DecisionTreeGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.decisiontree.targets.view_all"));
        viewAllLink.setClassAttr(ACTION_LINK);
        viewAllLink.addActionListener( new ActionListener() {
                public void actionPerformed ( ActionEvent event ) {
                    onlyShowComponent(event.getPageState(), TARGET_TABLE+m_typeIDStr);
                }
            });

        return viewAllLink;
    }

    /**
     * Utility method to create a link to display the section list.
     */
    protected ActionLink buildAddLink () {

        ActionLink addLink = new ActionLink( DecisionTreeGlobalizationUtil
                   .globalize("cms.contenttypes.ui.decisiontree.targets.add_new")) {
            @Override
            public boolean isVisible(PageState state) {
                SecurityManager sm = CMS.getSecurityManager(state);
                ContentItem item = (ContentItem)m_selTree.getSelectedObject(state);
                    
                return super.isVisible(state) && sm.canAccess(
                                                     state.getRequest(), 
                                                     SecurityManager.EDIT_ITEM,
                                                     item);
                    
                }
        };
        addLink.setClassAttr(ACTION_LINK);
        addLink.addActionListener( new ActionListener () {
                public void actionPerformed ( ActionEvent event ) {
                    PageState state = event.getPageState();
                    m_selTarget.clearSelection(state);
                    onlyShowComponent(state, TARGET_EDIT+m_typeIDStr);
                }
            });

        return addLink;
    }

    /**
     * 
     * @return 
     */
    public String getTypeIDStr() {
        return m_typeIDStr;
    }
}
