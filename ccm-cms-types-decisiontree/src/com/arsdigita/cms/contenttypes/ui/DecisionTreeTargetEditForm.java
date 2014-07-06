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
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionOption;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionOptionCollection;
import com.arsdigita.cms.contenttypes.DecisionTreeOptionTarget;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionCollection;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Form to edit an DecisionTreeOptionTarget for a DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeTargetEditForm extends Form 
                                        implements FormInitListener, 
                                                   FormProcessListener, 
                                                   FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(
                                        DecisionTreeTargetEditForm.class);

    private ItemSelectionModel m_selTree;
    private ItemSelectionModel m_selTarget;

    private DecisionTreeTargetStep m_container;

    private SaveCancelSection m_saveCancelSection;
    // private SingleSelect m_sectionWidget;
    private SingleSelect m_matchValueWidget;
	private SingleSelect m_targetSectionWidget;
	private TextField m_targetURLWidget;

    public static final String MATCH_OPTION   = "matchOption";
    public static final String TARGET_URL     = "targetURL";
    public static final String TARGET_SECTION = "targetSection";
    
    /**
     * Constructor.
     *
     * @param selTree the current article
     * @param selTarget the current section
     */
    public DecisionTreeTargetEditForm(ItemSelectionModel selTree,
                                      ItemSelectionModel selTarget) {
        this(selTree, selTarget, null);
    }
    
    /**
     * Constructor.
     *
     * @param selTree
     * @param selTarget the current section
     * @param container container which this form is added to
     */
    public DecisionTreeTargetEditForm(ItemSelectionModel selTree,
                                      ItemSelectionModel selTarget,
                                      DecisionTreeTargetStep container) {

        super("DecisionTreeTargetEditForm", new ColumnPanel(2));
        m_selTree = selTree;
        m_selTarget = selTarget;
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
                    Option option = new Option(section.getID().toString(), 
                                               section.getTitle());
                    target.addOption(option, state);
                }
                sections.close();
            }
        }
    }
    
    /**
     * Set up the dynamic options for the target select widget.
     */
    private void initTargetOptions(PrintEvent e) {
    	PageState state = e.getPageState();
		SingleSelect target = (SingleSelect) e.getTarget();
        DecisionTree tree = (DecisionTree)m_selTree.getSelectedObject(state);
        
        if (tree != null) {
            DecisionTreeSectionCollection sections = tree.getSections();
            if (sections != null) {
                while (sections.next()) {
                    DecisionTreeSection section = sections.getSection();
                    Option option = new Option(section.getID().toString(), 
                                               section.getTitle());
                    target.addOption(option, state);
                }
                sections.close();
            }
        }
    }
    
    /**
     * Set up the dynamic options for the match value select widget.
     */
    private void initMatchOptions(PrintEvent e) {
    	PageState state = e.getPageState();
		SingleSelect target = (SingleSelect) e.getTarget();
        DecisionTree tree = (DecisionTree)m_selTree.getSelectedObject(state);
        
        if (tree != null) {
            DecisionTreeSectionOptionCollection sectionOptions = tree.getOptions(); 
            if (sectionOptions != null) {
            	while (sectionOptions.next()) {
            		DecisionTreeSectionOption sectionOption = sectionOptions
                                                              .getOption();
            		String label = sectionOption.getSection().getTitle() + 
                                   " : " + sectionOption.getLabel();
            		Option option = new Option(sectionOption.getID().toString(), 
                                               label);
            		target.addOption(option, state);
            	}
            	sectionOptions.close();
            }
        }
    }
    
    /** Form initialisation hook. Sets the options for select widgets.
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        if (m_selTarget.getSelectedKey(state) != null) {
            BigDecimal id = new BigDecimal(m_selTarget.getSelectedKey(state)
                                                      .toString());
            DecisionTreeOptionTarget target = new DecisionTreeOptionTarget(id);

            data.put(MATCH_OPTION, target.getMatchOption().getID());
            data.put(TARGET_URL, target.getTargetURL());

            DecisionTreeSection targetSection = target.getTargetSection();
            if (targetSection != null)
            	data.put(TARGET_SECTION, targetSection.getID());
        }
    }

    /**
     * Add form widgets for a Section.
     */
    protected void addWidgets() {
        Option pleaseSelect = new Option(
               "", 
               new Label(DecisionTreeGlobalizationUtil.globalize(
                   "cms.contenttypes.ui.decisiontree.targets.form.please_select") ));
        Option none = new Option(
               "", 
               new Label( DecisionTreeGlobalizationUtil.globalize(
                   "cms.contenttypes.ui.decisiontree.targets.form.none")));
        
    //  add(new Label(DecisionTreeGlobalizationUtil.globalize(
    //                "cms.contenttypes.ui.decisiontree.targets.form.match_value")));
    	m_matchValueWidget = new SingleSelect(MATCH_OPTION);
        m_matchValueWidget.setLabel(DecisionTreeGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.decisiontree.targets.form.match_value"));
    	m_matchValueWidget.addValidationListener(new NotNullValidationListener());
    	m_matchValueWidget.addOption(pleaseSelect);
        try {
            m_matchValueWidget.addPrintListener(new PrintListener() {
                    @Override
                    public void prepare(PrintEvent e) {
                        initMatchOptions(e);
                    }
            });
    	} catch (TooManyListenersException e) {
            throw new RuntimeException(e);
        }
        add(m_matchValueWidget);

     // add(new Label(DecisionTreeGlobalizationUtil.globalize(
     //         "cms.contenttypes.ui.decisiontree.targets.form.target_url_label")));
    	m_targetURLWidget = new TextField(TARGET_URL);
        m_targetURLWidget.setLabel(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.targets.form.target_url_label"));
    	m_targetURLWidget.setSize(60);
    	add(m_targetURLWidget);

     // add(new Label(DecisionTreeGlobalizationUtil.globalize(
     //         "cms.contenttypes.ui.decisiontree.targets.form.target_section_label")));
    	m_targetSectionWidget = new SingleSelect(TARGET_SECTION);
        m_targetSectionWidget.setLabel(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.targets.form.target_section_label"));
    	m_targetSectionWidget.addOption(none);
        try {
            m_targetSectionWidget.addPrintListener(new PrintListener() {
                @Override
                public void prepare(PrintEvent e) {
                    initTargetOptions(e);
                }
            });
    	} catch (TooManyListenersException e) {
            throw new RuntimeException(e);
        }
        add(m_targetSectionWidget);

        addValidationListener(new FormValidationListener() {
            @Override
            public final void validate(final FormSectionEvent event)
                    throws FormProcessException {
                final PageState state = event.getPageState();
                if ("".equals(m_targetURLWidget.getValue(state)) 
                        && "".equals(m_targetSectionWidget.getValue(state))) {
                	GlobalizedMessage msg = DecisionTreeGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.decisiontree.targets.form.target_required");
                	throw new FormProcessException(msg);
                }
                
                if (!"".equals(m_targetURLWidget.getValue(state)) 
                        && !"".equals(m_targetSectionWidget.getValue(state))) {
                	GlobalizedMessage msg = DecisionTreeGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.decisiontree.targets.form.duplicate_target");
                	throw new FormProcessException(msg);
                }
            }
        });
    }

    /**
     * Called on form submission.  Check to see if the user clicked the
     * cancel button.  If they did, don't continue with the form.
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(FormSectionEvent event) 
    throws FormProcessException {
    	PageState state = event.getPageState();

    	if ( m_saveCancelSection.getCancelButton().isSelected(state) 
             && m_container != null) {
            m_container.onlyShowComponent(state, 
                                          DecisionTreeTargetStep.TARGET_TABLE +
                                              m_container.getTypeIDStr());
                throw new FormProcessException(
                    DecisionTreeGlobalizationUtil.globalize(
                  "cms.contenttypes.ui.decisiontree.targets.form.submission_cancelled")
                    );
        }
    }

    /**
     * Called after form has been validated. Create the new 
     * DecisionTreeOptionTarget and assign it to the current DecisionTree.
     * 
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent event) throws FormProcessException {
    	PageState state = event.getPageState();
    	FormData data = event.getFormData();

    	DecisionTreeSectionOption matchOption = new 
                DecisionTreeSectionOption(new 
                        BigDecimal((String)data.get(MATCH_OPTION)));
    	
    	DecisionTreeSection targetSection = null;
    	String sectionID = (String)data.get(TARGET_SECTION);
    	if (!"".equals(sectionID)) {
    		targetSection = new DecisionTreeSection(new BigDecimal(sectionID));
    	}

    	DecisionTreeOptionTarget target;
        if (m_selTarget.getSelectedKey(state) != null) {
            BigDecimal id = new BigDecimal(m_selTarget
                                           .getSelectedKey(state).toString());
            target = new DecisionTreeOptionTarget(id);
        } else {
        	target = new DecisionTreeOptionTarget();
        	target.setName("OptionTarget " + target.getID());
    	}

        target.setMatchOption(matchOption);
        target.setTargetURL((String)data.get(TARGET_URL));
        target.setTargetSection(targetSection);

    	if (m_container != null) {
    		m_container.onlyShowComponent(
    				state, 
    				DecisionTreeTargetStep.TARGET_TABLE +
    				m_container.getTypeIDStr());
    	}
    }

    @Override
    public void register(Page p) {
        super.register(p);
    }
}
