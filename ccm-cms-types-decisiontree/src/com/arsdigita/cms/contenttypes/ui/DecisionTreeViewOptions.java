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


import java.math.BigDecimal;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeUtil;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionOption;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.util.Assert;


/**
 * Authoring kit step to manage the options for a DecisionTree.
 * The editing process is implemented with three main visual components
 * that manipulate the currently selected DecisionTree and options.
 * The visibility of these components is managed by this class.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeViewOptions extends ResettableContainer
{
	/** id keys for each editing panel */
    public static final String OPTION_TABLE = "opt_tbl";
    public static final String OPTION_EDIT  = "opt_edt";
    public static final String OPTION_DEL   = "opt_del";

    /** class attributes */
    public static final String DATA_TABLE	= "dataTable";
    public static final String ACTION_LINK  = "actionLink";

    protected AuthoringKitWizard m_wizard;
    protected ItemSelectionModel m_selTree;
    protected ItemSelectionModel m_selOption;
    protected ItemSelectionModel m_moveOption;
    protected BigDecimalParameter m_moveParameter;

    /** visual components that do the 'real work' */
    protected DecisionTreeOptionTable       m_optionTable;
    protected DecisionTreeOptionEditForm    m_optionEdit;
    protected DecisionTreeOptionDeleteForm	m_optionDelete;

    protected ActionLink m_beginLink;
    private Label m_moveOptionLabel;

    private String m_typeIDStr;

    public DecisionTreeViewOptions (ItemSelectionModel selTree, AuthoringKitWizard wizard) {
        super();
        m_selTree = selTree;
        m_wizard = wizard;
        m_typeIDStr = wizard.getContentType().getID().toString();
        Assert.exists(m_selTree, ItemSelectionModel.class);

        // create the components and set default visibility
        add(buildOptionTable(), true);
        add(buildOptionEdit(), false);
        add(buildOptionDelete(), false);
    }

    /**
     * Builds a container to hold an OptionTable and a link
     * to add a new option.
     */
    protected Container buildOptionTable () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(OPTION_TABLE+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");
        
        m_moveParameter = new BigDecimalParameter("moveOption");
        m_moveOption = new ItemSelectionModel(DecisionTreeSectionOption.class.getName(),
        		DecisionTreeSectionOption.BASE_DATA_OBJECT_TYPE,
        		m_moveParameter);

        m_optionTable = new DecisionTreeOptionTable(m_selTree, m_moveOption);
        m_optionTable.setClassAttr(DATA_TABLE);

        // selected option is based on the selection in the OptionTable
        m_selOption = new ItemSelectionModel(DecisionTreeSectionOption.class.getName(),
                                              DecisionTreeSectionOption.BASE_DATA_OBJECT_TYPE,
                                              m_optionTable.getRowSelectionModel());
        m_optionTable.setOptionModel(m_selOption);

        Label emptyView = new Label(DecisionTreeUtil
                              .globalize("section_options.no_options_yet"));
        m_optionTable.setEmptyView(emptyView);

        m_moveOptionLabel = new Label ("Option Name");
        c.add(m_moveOptionLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        m_beginLink = new ActionLink(DecisionTreeUtil
                                    .globalize("section_options.move_to_beginning"));
        c.add(m_beginLink);

        m_beginLink.addActionListener ( new ActionListener() {
            public void actionPerformed ( ActionEvent event ) {
                PageState state = event.getPageState();
                DecisionTreeSectionOption option = new 
                        DecisionTreeSectionOption((BigDecimal) m_moveOption
                                                  .getSelectedKey(state));

                option.getSection().changeOptionRank(option, 1);
                option.save();
                m_moveOption.setSelectedKey(state, null);
            }
        });

        m_moveOption.addChangeListener ( new ChangeListener () {
                public void stateChanged ( ChangeEvent e ) {
                    PageState state = e.getPageState();
                    if ( m_moveOption.getSelectedKey(state) == null ) {
                        m_beginLink.setVisible(state, false);
                        m_moveOptionLabel.setVisible(state, false);
                    } else {
                        m_beginLink.setVisible(state, true);
                        m_moveOptionLabel.setVisible(state, true);

                        DecisionTreeSectionOption option = 
                                (DecisionTreeSectionOption) m_moveOption
                                .getSelectedObject(state);
                        String optionName = option.getSection().getTitle() + " - " 
                                            + option.getLabel();

                        m_moveOptionLabel.setLabel
                            ( (String)DecisionTreeUtil
                             .globalize("section_options.move_option_name")
                             .localize()
                             + " \"" + optionName + "\"", state);
                    }
                }
            });

        // handle clicks to preview or delete a Section
        m_optionTable.addTableActionListener (new TableActionListener () {
                public void cellSelected (TableActionEvent event) {
                    PageState state = event.getPageState();

                    TableColumn col = m_optionTable.getColumnModel()
                        .get(event.getColumn().intValue());
                    String colName = (String)col.getHeaderValue();

                    if (DecisionTreeOptionTable.COL_DEL.equals(colName)) {
                        onlyShowComponent(state, OPTION_DEL+m_typeIDStr);
                    } else if (DecisionTreeOptionTable.COL_EDIT.equals(colName)) {
                        onlyShowComponent(state, OPTION_EDIT+m_typeIDStr);
                    }
                }

				public void headSelected(TableActionEvent e) {}
            });
        c.add(m_optionTable);

        // link to add new section
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold a SectionEditForm and a link
     * to return to the section list.
     */
    protected Container buildOptionEdit () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(OPTION_EDIT+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        // display an appropriate title
        c.add( new Label( new PrintListener() {
                public void prepare ( PrintEvent event ) {
                    PageState state = event.getPageState();
                    Label label = (Label)event.getTarget();

                    if (m_selOption.getSelectedKey(state) == null) {
                        label.setLabel((String)DecisionTreeUtil.globalize("section_options.add_option").localize());
                    } else {
                        label.setLabel((String)DecisionTreeUtil.globalize("section_options.edit_option").localize());
                    }
                }
            }));

        // form to edit a Section
        m_optionEdit = new DecisionTreeOptionEditForm(m_selTree, m_selOption, this);
        c.add(m_optionEdit);

        c.add(buildViewAllLink());
        // link to add new section
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds a container to hold the component to confirm
     * deletion of a section.
     */
    protected Container buildOptionDelete () {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(OPTION_DEL+m_typeIDStr);
        c.setBorderColor("#FFFFFF");
        c.setPadColor("#FFFFFF");

        c.add(new Label(DecisionTreeUtil.globalize("section_options.delete_option")));
        m_optionDelete = new DecisionTreeOptionDeleteForm(m_selTree, m_selOption);
        m_optionDelete.addSubmissionListener ( new FormSubmissionListener () {
                public void submitted ( FormSectionEvent e ) {
                    PageState state = e.getPageState();
                    onlyShowComponent(state, OPTION_TABLE+m_typeIDStr);
                }
            });
        c.add(m_optionDelete);

        c.add(buildViewAllLink());

        return c;
    }

    /**
     * Utility method to create a link to display the section list.
     */
    protected ActionLink buildViewAllLink () {
        ActionLink viewAllLink = new ActionLink( (String) DecisionTreeUtil.globalize("section_options.view_all_options").localize());
        viewAllLink.setClassAttr(ACTION_LINK);
        viewAllLink.addActionListener( new ActionListener() {
                public void actionPerformed ( ActionEvent event ) {
                    onlyShowComponent(event.getPageState(), OPTION_TABLE+m_typeIDStr);
                }
            });

        return viewAllLink;
    }

    /**
     * Utility method to create a link to display the section list.
     */
    protected ActionLink buildAddLink () {
        ActionLink addLink = new ActionLink( (String) DecisionTreeUtil.globalize("section_options.add_new_option").localize()) {
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
                    m_selOption.clearSelection(state);
                    onlyShowComponent(state, OPTION_EDIT+m_typeIDStr);
                }
            });

        return addLink;
    }

    public void register ( Page p ) {
        super.register(p);
        p.addGlobalStateParam(m_moveParameter);
        p.setVisibleDefault(m_beginLink, false);
        p.setVisibleDefault(m_moveOptionLabel, false);
    }

    public String getTypeIDStr() {
        return m_typeIDStr;
    }
}
