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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.FAQItem;
import com.arsdigita.cms.contenttypes.util.FAQGlobalizationUtil;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;


/**
 * Form to edit the basic properties of an FAQItem. This form can be
 * extended to create forms for FAQItem subclasses.
 */
public class FAQItemPropertyForm
    extends BasicPageForm
    implements FormProcessListener, FormInitListener,FormSubmissionListener {

    private FAQItemPropertiesStep m_step;

    /** parameter names */
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String SECTION_NAME = "sectionName";

    /** Name of this form */
    public static final String ID = "FAQItem_edit";

    /**
     * Creates a new form to edit the FAQItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the FAQItem to
     * work on
     **/
    public FAQItemPropertyForm( ItemSelectionModel itemModel ) {
        this(itemModel,null);
    }
    /**
     * Creates a new form to edit the FAQItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the FAQItem to
     * work on
     * @param step The FAQItemPropertiesStep which controls this form.
     **/
    public FAQItemPropertyForm( ItemSelectionModel itemModel, FAQItemPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add( new Label(FAQGlobalizationUtil.globalize("cms.contenttypes.ui.faq.question")) );
        ParameterModel questionParam
            = new StringParameter( QUESTION );
        questionParam
            .addParameterListener( new NotNullValidationListener() );
        TextArea question = new TextArea( questionParam );
        question.setCols( 40 );
        question.setRows( 5 );
        add( question );

        add( new Label(FAQGlobalizationUtil.globalize("cms.contenttypes.ui.faq.answer")) );
        ParameterModel answerParam = new StringParameter( ANSWER );
        answerParam
            .addParameterListener( new NotNullValidationListener() );
        TextArea answer = new TextArea( answerParam );
        answer.setCols( 40 );
        answer.setRows( 5 );
        add( answer );
        
        add( new Label(FAQGlobalizationUtil.globalize("cms.contenttypes.ui.faq.sectionName")) );
        ParameterModel sectionNameParam = new StringParameter( SECTION_NAME );
        TextField sectionName = new TextField(sectionNameParam); 
        add(sectionName);

    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        FAQItem faqItem = (FAQItem) super.initBasicWidgets( fse );

        data.put( QUESTION, faqItem.getQuestion() );
        data.put( ANSWER,   faqItem.getAnswer() );
        data.put(SECTION_NAME , faqItem.getSectionName());
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
 
    /** Form processing hook. Saves FAQItem object. */
    public void process( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        
        FAQItem faqItem = (FAQItem) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( faqItem != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {
          faqItem.setQuestion( (String) data.get( QUESTION ) );
          faqItem.setAnswer( (String) data.get( ANSWER ) );
          faqItem.setSectionName( (String) data.get( SECTION_NAME) );
          faqItem.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
