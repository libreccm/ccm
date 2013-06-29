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
import com.arsdigita.cms.contenttypes.DecisionTreeSectionOption;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;

/**
 * A form to confirm deletion of a single section of a DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeOptionDeleteForm extends Form
                                          implements FormInitListener, 
                                                     FormSubmissionListener, 
                                                     FormProcessListener   {
    private final static Logger s_log = Logger.getLogger(
                                        DecisionTreeOptionDeleteForm.class.getName());

    protected ItemSelectionModel m_selTree;
    protected ItemSelectionModel m_selOption;
    protected SaveCancelSection m_saveCancelSection;
    /** Label denoting the name of the option to delete.  */
    private Label optionLabel;
    /** Value containin the actual name of the option to be deleted. Will be 
     *  passed into the Label field above using string formatter              */
    private String[] optionValue = new String[1];

    /**
     * Constructor.
     * 
     * @param selTree
     * @param selOption 
     */
    public DecisionTreeOptionDeleteForm (ItemSelectionModel selTree, 
                                         ItemSelectionModel selOption) {

        super("DecisionTreeOptionDeleteForm", new ColumnPanel(2));
        m_selTree = selTree;
        m_selOption = selOption;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        optionLabel = new Label(    //   "Option Label");
              DecisionTreeGlobalizationUtil.globalize(
              "cms.contenttypes.ui.decisiontree.options.delete_option_msg",
              optionValue));  
        add(optionLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        addSaveCancelSection();  //  add a save/cancel section to the form

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    /**
     * Create and adjust the label(s) of the SaveCancel button bar.
     * @return a new save/cancel bar with customized button labels
     */
    protected SaveCancelSection addSaveCancelSection () {
        m_saveCancelSection = new SaveCancelSection();
        m_saveCancelSection.getSaveButton().setButtonLabel(  // "Delete");
                    DecisionTreeGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.decisiontree.options.delete_button"));
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    /**
     * 
     * @param event
     * @throws FormProcessException 
     */
    public void init ( FormSectionEvent event ) throws FormProcessException {

        PageState state = event.getPageState();

        BigDecimal id = new BigDecimal(m_selOption.getSelectedKey(state)
                                                  .toString());
        DecisionTreeSectionOption option = new DecisionTreeSectionOption(id);
        optionValue[0] = option.getLabel(); //insert the actual name to the label
    }

    public void submitted ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        if ( m_saveCancelSection.getCancelButton().isSelected(state) ) {
            throw new FormProcessException( (String) 
                DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.options.delete.submission_cancelled")
                .localize());
        }
    }

    public void process ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        BigDecimal id = new BigDecimal(m_selOption
        		.getSelectedKey(state).toString());
        DecisionTreeSectionOption option = new DecisionTreeSectionOption(id);

        option.delete();
    }
}
