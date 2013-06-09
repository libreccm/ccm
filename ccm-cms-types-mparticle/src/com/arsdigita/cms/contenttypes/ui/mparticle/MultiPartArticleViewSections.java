/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui.mparticle;


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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;


/**
 * Authoring kit step to manage the section for a MultiPartArticle.
 * Proces is implemented with three main vidual components that manipulate
 * the currently selected MultiPartArticle and sections.  The visibility
 * of these components is managed by this class.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: MultiPartArticleViewSections.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class MultiPartArticleViewSections extends ResettableContainer
{
    /** id keys for each editing panel */
    public static final String SECTION_TABLE = "sec_tbl";
    public static final String SECTION_EDIT  = "sec_edt";
    public static final String SECTION_PRVW  = "sec_prv";
    public static final String SECTION_DEL   = "sec_del";

    /** class attributes */
    public static final String DATA_TABLE    = "dataTable";
    public static final String ACTION_LINK   = "actionLink";


    protected AuthoringKitWizard m_wizard;
    protected ItemSelectionModel m_selArticle;
    protected ItemSelectionModel m_selSection;
    protected ItemSelectionModel m_moveSection;
    protected BigDecimalParameter m_moveParameter;


    /** visual components that do the 'real work' */
    protected SectionTable        m_sectionTable;
    protected SectionEditForm     m_sectionEdit;
    protected SectionPreviewPanel m_sectionPreview;
    protected SectionDeleteForm   m_sectionDelete;

    protected ActionLink m_beginLink;
    private Label m_moveSectionLabel;

    private String m_typeIDStr;

    public MultiPartArticleViewSections ( ItemSelectionModel selArticle, 
                                          AuthoringKitWizard wizard) {
        super();
        m_selArticle = selArticle;
        m_wizard = wizard;
        m_typeIDStr = wizard.getContentType().getID().toString();
        Assert.exists(m_selArticle, ItemSelectionModel.class);

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

        m_moveParameter = new BigDecimalParameter("moveSection");
        m_moveSection   = new ItemSelectionModel(ArticleSection.class.getName(),
                                                 ArticleSection.BASE_DATA_OBJECT_TYPE,
                                                 m_moveParameter);

        m_sectionTable = new SectionTable(m_selArticle, m_moveSection);
        m_sectionTable.setClassAttr(DATA_TABLE);

        // selected section is based on the selection in the SectionTable
        m_selSection = new ItemSelectionModel(ArticleSection.class.getName(),
                                              ArticleSection.BASE_DATA_OBJECT_TYPE,
                                              m_sectionTable.getRowSelectionModel());

        m_sectionTable.setSectionModel(m_selSection);

        Label emptyView = new Label(MPArticleGlobalizationUtil
                  .globalize("cms.contenttypes.ui.mparticle.no_sections_yet"));
        m_sectionTable.setEmptyView(emptyView);

        m_moveSectionLabel = new Label (MPArticleGlobalizationUtil
                  .globalize("cms.contenttypes.ui.mparticle.section.title"));
        c.add(m_moveSectionLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        m_beginLink = new ActionLink( MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle.move_to_beginning"));
        c.add(m_beginLink);

        m_beginLink.addActionListener ( new ActionListener() {
                public void actionPerformed ( ActionEvent event ) {
                    PageState state = event.getPageState();
                    MultiPartArticle article = (MultiPartArticle)m_selArticle
                        .getSelectedObject(state);

                    article.changeSectionRank((BigDecimal)m_moveSection.getSelectedKey(state), 1);
                    m_moveSection.setSelectedKey(state, null);
                }
            });

        m_moveSection.addChangeListener ( new ChangeListener () {
                public void stateChanged ( ChangeEvent e ) {
                    PageState state = e.getPageState();
                    if ( m_moveSection.getSelectedKey(state) == null ) {
                        m_beginLink.setVisible(state, false);
                        m_moveSectionLabel.setVisible(state, false);
                    } else {
                        m_beginLink.setVisible(state, true);
                        m_moveSectionLabel.setVisible(state, true);
                        Object[] parmObj = {
                         ((ArticleSection)m_moveSection.getSelectedObject(state))
                         .getTitle()
                        };
  
                        m_moveSectionLabel
                        .setLabel(MPArticleGlobalizationUtil.globalize(
                                  "cms.contenttypes.ui.mparticle.move_section_name",
                                  parmObj  )
                                  , state
                                 );
                    }
                }
            });

        // handle clicks to preview or delete a Section
        m_sectionTable.addTableActionListener ( new 
            TableActionListener () {
                public void cellSelected ( TableActionEvent event ) {
                    PageState state = event.getPageState();

                    TableColumn col = m_sectionTable.getColumnModel()
                                                    .get(event.getColumn()
                                                    .intValue());

                    if (col.getModelIndex() == SectionTable.COL_INDEX_DELETE) {
                        onlyShowComponent(state, SECTION_DEL+m_typeIDStr);
                    } else if (col.getModelIndex() == SectionTable.COL_INDEX_EDIT) {
                        onlyShowComponent(state, SECTION_EDIT+m_typeIDStr);
                    }
                }

                public void headSelected ( TableActionEvent event ) {
                    // do nothing
                }
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

                    if ( m_selSection.getSelectedKey(state) == null ) {
                        label.setLabel(MPArticleGlobalizationUtil
                                       .globalize("cms.contenttypes.ui.mparticle.add_section"));
                    } else {
                        label.setLabel(MPArticleGlobalizationUtil
                                       .globalize("cms.contenttypes.ui.mparticle.edit_section"));
                    }
                }
            }));

        // form to edit a Section
        m_sectionEdit = new SectionEditForm(m_selArticle, m_selSection, this);
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

        c.add(new Label(MPArticleGlobalizationUtil
                        .globalize("cms.contenttypes.ui.mparticle.delete_section")));
        m_sectionDelete = new SectionDeleteForm(m_selArticle, m_selSection);
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
        ActionLink viewAllLink = new ActionLink(MPArticleGlobalizationUtil
                   .globalize("cms.contenttypes.ui.mparticle.view_all_sections"));
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
        ActionLink addLink = new ActionLink(MPArticleGlobalizationUtil
                   .globalize("cms.contenttypes.ui.mparticle.add_new_section")) {
                public boolean isVisible(PageState state) {
                    SecurityManager sm = Utilities.getSecurityManager(state);
                    ContentItem item = (ContentItem)m_selArticle.getSelectedObject(state);
                    
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


    public void register ( Page p ) {
        super.register(p);
        p.addGlobalStateParam(m_moveParameter);
        p.setVisibleDefault(m_beginLink, false);
        p.setVisibleDefault(m_moveSectionLabel, false);
    }

    public String getTypeIDStr() {
        return m_typeIDStr;
    }
}
