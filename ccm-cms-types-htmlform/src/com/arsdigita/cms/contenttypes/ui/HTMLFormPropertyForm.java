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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.HTMLForm;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of an HTMLForm. This form can be
 * extended to create forms for HTMLForm subclasses.
 */
public class HTMLFormPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(HTMLFormPropertyForm.class);
    private HTMLFormPropertiesStep m_step;
    public static final String LEAD = "lead";

    /**
     * Creates a new form to edit the HTMLForm object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    HTMLForm to work on
     */
    public HTMLFormPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the HTMLForm object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    HTMLForm to work on
     * @param step The HTMLFormPropertiesStep which controls this form.
     */
    public HTMLFormPropertyForm(ItemSelectionModel itemModel, HTMLFormPropertiesStep step) {
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

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.lead")));
        ParameterModel leadParam = new StringParameter(LEAD);

        if (ContentSection.getConfig().mandatoryDescriptions()) {
            leadParam.addParameterListener(new NotEmptyValidationListener(GlobalizationUtil.globalize("cms.contenttypes.ui.description_missing")));
        }
        //leadParam
        //    .addParameterListener( new NotNullValidationListener() );
        leadParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea lead = new TextArea(leadParam);
        lead.setCols(40);
        lead.setRows(5);
        add(lead);
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        super.validate(e);
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        HTMLForm htmlform = (HTMLForm) super.initBasicWidgets(fse);

        data.put(LEAD, htmlform.getLead());
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

        HTMLForm htmlform = (HTMLForm) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (htmlform != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            htmlform.setLead((String) data.get(LEAD));
            htmlform.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
