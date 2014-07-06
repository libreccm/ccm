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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * CMS authoring form for the Camden Decision Tree content type.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreePropertiesForm extends BasicPageForm implements
        FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static String ID = "FORM_PROPERTIES";
    public final static String DESCRIPTION = "description";
    public final static String CANCEL_URL = "cancelURL";

    private final DecisionTreePropertiesStep m_step;

    /**
     * Creates a new form to edit the Consultation object specified by the item
     * selection model passed in.
     *
     * @param itemModel
     *            The ItemSelectionModel to use to obtain the Article to work on
     * @param step
     *            The ArticlePropertiesStep which controls this form.
     */
    public DecisionTreePropertiesForm(ItemSelectionModel itemModel,
    		DecisionTreePropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
    	super.addWidgets();

    //  add(new Label(DecisionTreeGlobalizationUtil.globalize(
    //      "cms.contenttypes.ui.decisiontree.properties.form.description_label")));
    	
    	TextArea description = new TextArea(new TrimmedStringParameter(DESCRIPTION));
        description.setLabel(DecisionTreeGlobalizationUtil.globalize(
            "cms.contenttypes.ui.decisiontree.properties.form.description_label"));
    	description.setRows(5);
    	description.setCols(30);
    	add(description);
    	
    //  add(new Label(DecisionTreeGlobalizationUtil.globalize
    //      ("cms.contenttypes.ui.decisiontree.properties.cancel_url")));
    	TextField cancelURL = new TextField(new StringParameter(CANCEL_URL));
        cancelURL.setLabel(DecisionTreeGlobalizationUtil.globalize
            ("cms.contenttypes.ui.decisiontree.properties.cancel_url"));
    	cancelURL.setSize(60);
    	add(cancelURL);
	}

    /** 
     * Cancels streamlined editing. 
     * @param fse
     */
    @Override
    public void submitted(FormSectionEvent fse) {
        PageState state = fse.getPageState();

        if (m_step != null &&
            getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_step.cancelStreamlinedCreation(state);
        }
    }

    /** 
     * Form processing hook. Saves Event object.
     * @param fse 
     */
    @Override
    public void process(FormSectionEvent fse) {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        DecisionTree decisionTree = (DecisionTree)processBasicWidgets(fse);
        decisionTree.setDescription((String)data.get(DESCRIPTION));
        decisionTree.setCancelURL((String)data.get(CANCEL_URL));
        decisionTree.save();

        if (m_step != null)
            m_step.maybeForwardToNextStep(state);
    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
    	DecisionTree item = (DecisionTree)initBasicWidgets(fse);
    	
    	FormData data = fse.getFormData();
    	data.put(DESCRIPTION, item.getDescription());
    	data.put(CANCEL_URL, item.getCancelURL());
    }
    
}
