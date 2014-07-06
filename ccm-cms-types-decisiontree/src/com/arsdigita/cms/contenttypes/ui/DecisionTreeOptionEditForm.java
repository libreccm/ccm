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
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionCollection;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionOption;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;

/**
 * Form to edit a SectionOption for a DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeOptionEditForm extends Form 
                                        implements FormInitListener, 
                                                   FormProcessListener, 
                                                   FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(
                                        DecisionTreeOptionEditForm.class);

    private ItemSelectionModel m_selTree;
    private ItemSelectionModel m_selOption;

    private DecisionTreeOptionStep m_container;

    private SaveCancelSection m_saveCancelSection;
    private SingleSelect m_sectionWidget;

    public static final String LABEL    = "label";
    public static final String VALUE    = "value";
    public static final String SECTION  = "section";

    /**
     * Constructor.
     *
     * @param selTree the current article
     * @param selOption the current section
     */
    public DecisionTreeOptionEditForm(ItemSelectionModel selTree,
                           ItemSelectionModel selOption) {
        this(selTree, selOption, null);
    }
    
    /**
     * Constructor.
     *
     * @param selTree   the current decision tree
     * @param selOption the current section
     * @param container container which this form is added to
     */
    public DecisionTreeOptionEditForm(ItemSelectionModel selTree,
                                      ItemSelectionModel selOption,
                                      DecisionTreeOptionStep container) {

        super("DecisionTreeOptionEditForm", new ColumnPanel(2));
        m_selTree = selTree;
        m_selOption = selOption;
        m_container = container;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    /**
     * Instantiate and add a save/cancel section to the form.
     *
     * @return the SaveCancelSection that was added
     */
    protected SaveCancelSection addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    /**
     * Returns the save/cancel section from this form.
     * @return 
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Set up the dynamic options for the section select widget.
     */
    private void initSectionOptions(PrintEvent e) {
    	PageState state = e.getPageState();
		SingleSelect target = (SingleSelect) e.getTarget();
        DecisionTree tree = (DecisionTree)m_selTree.getSelectedObject(state);

        if (tree != null) {
            DecisionTreeSectionCollection sections = tree.getSections();
            if (sections != null) {
                while (sections.next()) {
                    DecisionTreeSection section = sections.getSection();
                    Option option = new Option(section.getID().toString(), section.getTitle());
                    target.addOption(option, state);
                }
                sections.close();
            }
        }    	
    }
    
    /** 
     * Form initialisation hook.
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();
 
        if (m_selOption.getSelectedKey(state) != null) {
            BigDecimal id = new BigDecimal(m_selOption.getSelectedKey(state).toString());
            // retrieve the selected Option from the persistence layer
            DecisionTreeSectionOption sectionOption = new DecisionTreeSectionOption(id);

            data.put(SECTION, sectionOption.getSection().getID());
            data.put(LABEL, sectionOption.getLabel());
            data.put(VALUE, sectionOption.getValue());
        }
    }

    /**
     * Add form widgets for a Section.
     */
    protected void addWidgets() {
        Option pleaseSelect = new Option(
               "", 
               new Label(DecisionTreeGlobalizationUtil.globalize(
                         "cms.contenttypes.ui.decisiontree.options.form.please_select")
                         ));

        add(new Label(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.options.form.section")));
    	m_sectionWidget = new SingleSelect(SECTION);
    	m_sectionWidget.addValidationListener(new NotNullValidationListener());
        m_sectionWidget.addOption(pleaseSelect);

    	try {
    		m_sectionWidget.addPrintListener(new PrintListener() {
    			public void prepare(PrintEvent e) {
    				initSectionOptions(e);
    			}
    		});
    	} catch (TooManyListenersException e) {
            throw new RuntimeException(e);
        }
    	
    	add(m_sectionWidget);

    //  add(new Label(DecisionTreeGlobalizationUtil.globalize(
    //          "cms.contenttypes.ui.decisiontree.options.form.label")));
    	TextField labelWidget = new TextField(new TrimmedStringParameter(LABEL));
        labelWidget.setLabel(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.options.form.label"));
    	labelWidget.addValidationListener(new NotNullValidationListener());
    	labelWidget.setSize(60);
    	add(labelWidget);

    //  add(new Label(DecisionTreeGlobalizationUtil.globalize(
    //          "cms.contenttypes.ui.decisiontree.options.form.value")));
    	TextField valueWidget = new TextField(new TrimmedStringParameter(VALUE));
        valueWidget.setLabel(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.options.form.value"));
    	valueWidget.addValidationListener(new NotNullValidationListener());
    	valueWidget.setSize(60);
    	add(valueWidget);
    }

    /**
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     * Called on form submission.  Check to see if the user clicked the
     * cancel button.  If they did, don't continue with the form.
     */
    @Override
    public void submitted(FormSectionEvent event) 
           throws FormProcessException {
    	PageState state = event.getPageState();

    	if ( m_saveCancelSection.getCancelButton()
    			.isSelected(state) && m_container != null) {
    		m_container.onlyShowComponent(
    				state, DecisionTreeOptionStep.OPTION_TABLE +
    				m_container.getTypeIDStr());
    		throw new FormProcessException(
    				DecisionTreeGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.decisiontree.options.form.submission_cancelled")
    				);
    	}
    }

    /**
     * Called after form has been validated. Create the new SectionOption and
     * assign it to the current DecisionTree.
     * 
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent event) throws FormProcessException {
    	PageState state = event.getPageState();
    	FormData data = event.getFormData();

    	DecisionTreeSection section = new DecisionTreeSection(new BigDecimal((String)data.get(SECTION)));

    	DecisionTreeSectionOption option;
        if (m_selOption.getSelectedKey(state) != null) {
            BigDecimal id = new BigDecimal(m_selOption
                                           .getSelectedKey(state).toString());
            // retrieve the selected Option from the persistence layer
            option = new DecisionTreeSectionOption(id);
        } else {
    		option = new DecisionTreeSectionOption();
        	option.setName("DecisionTreeSectionOption " + option.getID());
        	int rank = section.getMaxOptionRank() + 1;
        	option.setRank(Integer.valueOf(rank));
    	}
        
        String label = (String)data.get(LABEL);
        String value = (String)data.get(VALUE);

    	option.setSection(section);
    	option.setLabel(label);
    	option.setValue(value);

    	if (m_container != null) {
    		m_container.onlyShowComponent(
    				state, 
    				DecisionTreeOptionStep.OPTION_TABLE +
    				m_container.getTypeIDStr());
    	}
    }

    @Override
    public void register(Page p) {
        super.register(p);
    }
}
