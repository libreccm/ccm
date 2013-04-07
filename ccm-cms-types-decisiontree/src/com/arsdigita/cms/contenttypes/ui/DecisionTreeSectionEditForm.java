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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringIsLettersOrDigitsValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.DecisionTreeUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;


/**
 * Form to edit a TreeSection for a DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeSectionEditForm extends Form 
                                         implements FormInitListener, 
                                                    FormProcessListener, 
                                                    FormSubmissionListener {

    private final static Logger log = Logger.getLogger(
                                      DecisionTreeSectionEditForm.class);

    private ItemSelectionModel m_selTree;
    private ItemSelectionModel m_selSection;

    private BigDecimalParameter m_instructionsParam;
    private ItemSelectionModel m_selInstructions;
    private DecisionTreeViewSections m_container;

    private SaveCancelSection m_saveCancelSection;

    public static final String TITLE          = "title";
    public static final String PARAMETER_NAME = "parameterName";
    public static final String INSTRUCTIONS   = "instructions";

    private static final String INSTRUCTIONS_PARAM = "instructionsParam";

    /**
     * Constructor.
     *
     * @param selTree the current article
     * @param selSection the current section
     */
    public DecisionTreeSectionEditForm(ItemSelectionModel selTree,
                           ItemSelectionModel selSection) {
        this(selTree, selSection, null);
    }
    /**
     * Constructor.
     *
     * @param selArticle the current article
     * @param selSection the current section
     * @param container container which this form is added to
     */
    public DecisionTreeSectionEditForm(ItemSelectionModel selTree,
                           ItemSelectionModel selSection,
                           DecisionTreeViewSections container) {
        super("SectionEditForm", new ColumnPanel(2));
        m_selTree = selTree;
        m_selSection = selSection;
        m_container = container;

        m_instructionsParam = new BigDecimalParameter(INSTRUCTIONS_PARAM);
        m_selInstructions = new ItemSelectionModel(
                                           TextAsset.class.getName(),
                                           TextAsset.BASE_DATA_OBJECT_TYPE,
                                           m_instructionsParam);

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
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Add form widgets for a Section.
     */
    protected void addWidgets() {
        add(new Label(DecisionTreeUtil.globalize("form_label.title")));
        TextField titleWidget = new TextField(new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotEmptyValidationListener());
        add(titleWidget);

        add(new Label(DecisionTreeUtil.globalize("form_label.parameter_name")));
        TextField parameterWidget = new TextField(new TrimmedStringParameter(PARAMETER_NAME));
        parameterWidget.addValidationListener(new NotEmptyValidationListener());
        parameterWidget.addValidationListener(new DecisionTreeParameterNameValidationListener());
        add(parameterWidget);
        
        add(new Label(DecisionTreeUtil.globalize("form_label.instructions")),
            ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);
        CMSDHTMLEditor textWidget = 
            new CMSDHTMLEditor(new TrimmedStringParameter(INSTRUCTIONS));
        textWidget.setRows(40);
        textWidget.setCols(70);
        textWidget.setWrap(CMSDHTMLEditor.SOFT);
        add(textWidget,
            ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);
    }

    /**
     * Initialize the form.  If there is a selected section, ie. this
     * is an 'edit' step rather than a 'create new' step, load the data
     * into the form fields.
     */
    public void init( FormSectionEvent event ) 
    throws FormProcessException {
    	PageState state = event.getPageState();
    	FormData data = event.getFormData();
    	m_selInstructions.setSelectedObject(state,null);

    	if ( m_selSection.getSelectedKey(state) != null ) {
    		BigDecimal id = new BigDecimal(m_selSection
    				.getSelectedKey(state).toString());
    		try {
    			// retrieve the selected Section from the persistence layer
    			DecisionTreeSection section = new DecisionTreeSection(id);

    			data.put(TITLE, section.getTitle());
    			data.put(PARAMETER_NAME, section.getParameterName());

    			TextAsset t = section.getInstructions();
    			if ( t != null ) {
    				m_selInstructions.setSelectedObject(state, t);
    				data.put(INSTRUCTIONS, t.getText());
    			}

    		} catch ( DataObjectNotFoundException ex ) {
    			log.error("Section(" + id + ") could not be found");
    		}
    	}
        }


    /**
     * Called on form submission.  Check to see if the user clicked the
     * cancel button.  If they did, don't continue with the form.
     */
    public void submitted( FormSectionEvent event ) 
    throws FormProcessException {
    	PageState state = event.getPageState();

    	if ( m_saveCancelSection.getCancelButton()
    			.isSelected(state) && m_container != null) {
    		m_container.onlyShowComponent(
    				state, DecisionTreeViewSections.SECTION_TABLE +
    				m_container.getTypeIDStr());
    		throw new FormProcessException(
    				(String)DecisionTreeUtil
    				.globalize("tree_section.submission_cancelled")
    				.localize());
    	}
    }

    /**
     * Called after form has been validated. Create the new TreeSection and
     * assign it to the current DecisionTree.
     */
    public void process(FormSectionEvent event) 
    throws FormProcessException {
    	PageState state = event.getPageState();
    	FormData data = event.getFormData();

    	// retrieve the current DecisionTree
    	BigDecimal id = new BigDecimal(
    			m_selTree.getSelectedKey(state).toString());

    	DecisionTree tree;
    	try {
    		tree = new DecisionTree(id);
    	} catch ( DataObjectNotFoundException ex ) {
    		throw new UncheckedWrapperException(ex);
    	}            

    	// get the selected section to update or create a new one
        DecisionTreeSection section = (DecisionTreeSection)
                                      m_selSection.getSelectedObject(state);
    	if (section == null) {
    		section = createSection(event, tree);                
    		tree.addSection(section);
    	}

    	section.setTitle((String) data.get(TITLE));
    	section.setParameterName((String) data.get(PARAMETER_NAME));

    	// get the text asset
    	TextAsset textAsset = (TextAsset)m_selInstructions.getSelectedObject(state);
    	if ( textAsset == null ) {
    		textAsset = new TextAsset();
    		textAsset.setName(section.getName() + " text");
    		m_selInstructions.setSelectedObject(state, textAsset);
    		section.setInstructions(textAsset);
    	}

    	String text = (String)data.get(INSTRUCTIONS);
    	if ( text == null ) { text = ""; }

    	textAsset.setText(text);
    	if ( m_container != null) {
    		m_container.onlyShowComponent(
    				state, 
    				DecisionTreeViewSections.SECTION_TABLE +
    				m_container.getTypeIDStr());
    	}
    }
    
    /**
     * Utility method to create a Section from the form data supplied.
     */
    protected DecisionTreeSection createSection(FormSectionEvent event, 
                                                DecisionTree tree) {
        FormData data = event.getFormData();

        DecisionTreeSection section = new DecisionTreeSection();

        section.setTitle((String)data.get(TITLE));
        section.setName(tree.getName() + ": " + (String)data.get(TITLE));

        return section;
    }

    /**
     * 
     * @param p 
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_instructionsParam);
    }
}
