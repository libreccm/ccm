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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit the basic properties of an article. This form can be
 * extended to create forms for Article subclasses.
 */
public class GenericArticlePropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(GenericArticlePropertyForm.class);
    private GenericArticlePropertiesStep m_step;

    /**
     * Creates a new form to edit the Article object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Article to work on
     */
    public GenericArticlePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the GenericArticle object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    GenericArticle to work on
     * @param step The GenericArticlePropertiesStep which controls this form.
     */
    public GenericArticlePropertyForm(ItemSelectionModel itemModel, GenericArticlePropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        super.validate(e);        
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        GenericArticle article = (GenericArticle) super.initBasicWidgets(fse);
    }

    /** Cancels streamlined editing. */
    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Event object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        GenericArticle article = (GenericArticle) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (article != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            article.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
