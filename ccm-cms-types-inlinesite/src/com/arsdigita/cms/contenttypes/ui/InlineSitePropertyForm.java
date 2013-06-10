/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.contenttypes.InlineSite;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of an InlineSite. This form can be extended to create forms for InlineSite
 * subclasses.
 */
public class InlineSitePropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener {

    private TextField m_url;
    private TextArea m_description;
    private InlineSitePropertiesStep m_step;

    /**
     * Creates a new form to edit the InlineSite object specified by the item selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the InlineSite to work on
     */
    public InlineSitePropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }
    
    public InlineSitePropertyForm(final ItemSelectionModel itemModel, final InlineSitePropertiesStep step) {
        super("inlineSiteEdit", itemModel);
        m_step = step;
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();
        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.description")));
        m_description = new TextArea(ContentPage.DESCRIPTION);
        m_description.setCols(30);
        m_description.setRows(5);
        if (ContentSection.getConfig().mandatoryDescriptions()) {
            m_description.addValidationListener(
                    new NotEmptyValidationListener(GlobalizationUtil.
                    globalize("cms.contenttypes.ui.description_missing")));
        }
        m_description.addValidationListener(
                new StringInRangeValidationListener(
                0,
                4000));
        add(m_description);

        add(new Label("URL:"));
        ParameterModel urlParam = new StringParameter("url");
        m_url = new TextField(urlParam);
        m_url.setSize(40);
        add(m_url);
    }

    /**
     * Form initialisation hook. Fills widgets with data.
     */
    public void init(FormSectionEvent fse) {
        InlineSite site = (InlineSite) super.initBasicWidgets(fse);

        m_url.setValue(fse.getPageState(),
                       site.getURL());
        m_description.setValue(fse.getPageState(), site.getDescription());
    }

    /**
     * Form processing hook. Saves InlineSite object.
     */
    public void process(FormSectionEvent fse) {
        InlineSite site = (InlineSite) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (site != null
            && getSaveCancelSection().getSaveButton()
                .isSelected(fse.getPageState())) {
            site.setURL((String) m_url.getValue(fse.getPageState()));
            site.setDescription((String) m_description.getValue(fse.getPageState()));
            site.save();
        }
        
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
