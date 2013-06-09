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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.contenttypes.LegalNotice;
import com.arsdigita.cms.contenttypes.util.LegalNoticeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of a legal notice. These are name, title,
 * item date and reference code. This form can be extended to create forms for
 * LegalNotice subclasses.
 **/
public class LegalNoticePropertyForm extends BasicPageForm
                                     implements FormProcessListener, 
                                                FormInitListener, 
                                                FormSubmissionListener {

    private LegalNoticePropertiesStep m_step;

    /**  government UID parameter name */
    public static final String GOVERNMENT_UID = "governmentUID";
    /** Name of this form */
    public static final String ID = "legal_notice_edit";

    /**
     * Creates a new form to edit the LegalNotice object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    LegalNotice to work on
     */
    public LegalNoticePropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the LegalNotice object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    LegalNotice to work on
     * @param step The LegalNoticePropertiesStep which controls this form.
     */
    public LegalNoticePropertyForm( ItemSelectionModel itemModel, 
                                    LegalNoticePropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(LegalNoticeGlobalizationUtil
                      .globalize("cms.contenttypes.ui.legal_notice.government_uid")));
        ParameterModel governmentUIDParam = new StringParameter(GOVERNMENT_UID);
        TextField governmentUID = new TextField(governmentUIDParam);
        governmentUID.setSize(30);
        add(governmentUID);

    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        LegalNotice notice = (LegalNotice) super.initBasicWidgets(fse);

        data.put(GOVERNMENT_UID, notice.getGovernmentUID());
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves LegalNotice object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        LegalNotice notice
            = (LegalNotice) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (notice != null
            && getSaveCancelSection()
            .getSaveButton().isSelected(fse.getPageState())) {

            notice.setGovernmentUID((String) data.get(GOVERNMENT_UID));
            notice.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
