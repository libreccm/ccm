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


import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.Assert;

/**
 * A form to confirm deletion of a single section of a DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeSectionDeleteForm extends Form
                                           implements FormInitListener, 
                                                      FormSubmissionListener, 
                                                      FormProcessListener  {
    private final static Logger log = Logger.getLogger(
                                      DecisionTreeSectionDeleteForm.class.getName());

    protected ItemSelectionModel m_selTree;
    protected ItemSelectionModel m_selSection;
    protected SaveCancelSection m_saveCancelSection;
    /** Label denoting the name of the section to delete.  */
    private Label m_sectionNameLabel;
    /** Value containin the name of the section to be deleted. Will be passed
     *  into the Label field above using string formatter                     */
    private String[] m_sectionNameValue = new String[1];


    /**
     * Constructor.
     * 
     * @param selArticle
     * @param selSection 
     */
    public DecisionTreeSectionDeleteForm( ItemSelectionModel selArticle,
                                          ItemSelectionModel selSection) {
        super("DecisionTreeSectionDeleteForm", new ColumnPanel(2));
        m_selTree = selArticle;
        m_selSection = selSection;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        m_sectionNameLabel = new Label (       // Section name
                 DecisionTreeGlobalizationUtil.globalize(
                 "cms.contenttypes.ui.decisiontree.sections.delete_section_msg",
                 m_sectionNameValue));  
        add(m_sectionNameLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        addSaveCancelSection();   // add a save/cancel section to the form

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    /**
     * Create and adjust the label(s) of the SaveCancel button bar.
     * @return 
     */
    protected SaveCancelSection addSaveCancelSection () {
        m_saveCancelSection = new SaveCancelSection();
        m_saveCancelSection.getSaveButton().setButtonLabel(
                    DecisionTreeGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.decisiontree.sections.delete_button"));
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    /**
     * 
     * @param event
     * @throws FormProcessException 
     */
    public void init ( FormSectionEvent event ) throws FormProcessException {

        String sectionTitle = " ";
        PageState state = event.getPageState();

        DecisionTreeSection section = (DecisionTreeSection)
                                      m_selSection.getSelectedObject(state);

        if ( section == null ) {
            log.error("No section selected");
        } else {
            // Just be be sure, null should never happen because section it
            // meant to have a title.
            if (section.getTitle() != null) sectionTitle = section.getTitle();
        }
        m_sectionNameValue[0] = sectionTitle;
    }

    public void submitted ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        if ( m_saveCancelSection.getCancelButton().isSelected(state) ) {
            throw new FormProcessException( (String) 
                DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.sections.form.submission_cancelled")
                .localize());
        }
    }

    public void process ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        DecisionTree article = (DecisionTree)m_selTree.getSelectedObject(state);
        DecisionTreeSection section = (DecisionTreeSection)m_selSection.getSelectedObject(state);

        Assert.exists(article, DecisionTree.class);
        Assert.exists(section, DecisionTreeSection.class);

        article.removeSection(section);

        log.debug("section " + m_selSection.getSelectedKey(state) + " delete");
    }
}
