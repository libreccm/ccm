/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;

/**
 * Form for editing the basic properties of a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public static final String ID = "SciOrganizationEdit";

    public SciOrganizationPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciOrganizationPropertyForm(ItemSelectionModel itemModel,
                                       SciOrganizationPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        Label descLabel = new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.organization.shortdescription"));
        add(descLabel);
        ParameterModel descParam = new StringParameter(
                SciOrganization.ORGANIZATION_SHORT_DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setCols(75);
        desc.setRows(5);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciOrganization orga = (SciOrganization) super.initBasicWidgets(fse);

        data.put(SciOrganization.ORGANIZATION_SHORT_DESCRIPTION,
                 orga.getOrganizationShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciOrganization organization = (SciOrganization) super.
                processBasicWidgets(fse);

        if ((organization != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {

            organization.setOrganizationShortDescription((String) data.get(
                    SciOrganization.ORGANIZATION_SHORT_DESCRIPTION));

            organization.save();
        }
    }
}
