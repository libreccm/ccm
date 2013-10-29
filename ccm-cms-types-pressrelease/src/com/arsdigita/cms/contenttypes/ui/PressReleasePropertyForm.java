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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PressRelease;
import com.arsdigita.cms.contenttypes.util.PressReleaseGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of a press release. These are name, title,
 * release date and reference code. This form can be extended to create forms
 * for PressRelease subclasses.
 */
public class PressReleasePropertyForm extends BasicPageForm
                                      implements FormProcessListener, 
                                                 FormInitListener, 
                                                 FormSubmissionListener {

    private PressReleasePropertiesStep m_step;

    /**  summary parameter name */
    public static final String SUMMARY = "summary";
    /** contact info parameter name */
    public static final String CONTACT_INFO = "contactInfo";
    /** Reference code parameter name */
    public static final String REF_CODE = "ref_code";
    /** Release date parameter name */
    public static final String RELEASE_DATE = "rel_date";
    /** Name of this form */
    public static final String ID = "press_release_edit";

    /**
     * Creates a new form to edit the PressRelease object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    PressRelease to work on
     */
    public PressReleasePropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the PressRelease object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    PressRelease to work on
     * @param step The PressReleasePropertiesStep which controls this form.
     */
    public PressReleasePropertyForm( ItemSelectionModel itemModel, 
                                     PressReleasePropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.summary")));
        ParameterModel summaryParam = new StringParameter(SUMMARY);
        TextArea summary = new TextArea(summaryParam);
        summary.setCols(40);
        summary.setRows(7);
        summary.addValidationListener(new StringLengthValidationListener(4000));
        add(summary);

        add(new Label(PressReleaseGlobalizationUtil
                      .globalize("cms.contenttypes.ui.pressrelease.contact_info")));
        ParameterModel contactInfoParam = new StringParameter(CONTACT_INFO);
        CMSDHTMLEditor contactInfo = new CMSDHTMLEditor(contactInfoParam);
        contactInfo.setCols(40);
        contactInfo.setRows(10);
        contactInfo.addValidationListener(new StringLengthValidationListener(1000));
        add(contactInfo);

        add(new Label(PressReleaseGlobalizationUtil
                      .globalize("cms.contenttypes.ui.pressrelease.ref_code")));
        ParameterModel refCodeParam = new StringParameter(REF_CODE);
        TextField refCode = new TextField(refCodeParam);
        refCode.setSize(30);
        refCode.setMaxLength(30);
        refCode.addValidationListener(new StringLengthValidationListener(80));
        add(refCode);
    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PressRelease release
            = (PressRelease) super.initBasicWidgets(fse);

        data.put(SUMMARY,      release.getSummary());
        data.put(CONTACT_INFO, release.getContactInfo());
        data.put(REF_CODE,     release.getReferenceCode());
    }

    /** 
     * Cancels streamlined editing. 
     */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** 
     * Form processing hook. Saves PressRelease object. 
     */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        PressRelease release = (PressRelease) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (release != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(fse.getPageState())) {

            release.setSummary((String) data.get(SUMMARY));
            release.setContactInfo((String) data.get(CONTACT_INFO));
            release.setReferenceCode((String) data.get(REF_CODE));
            release.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
