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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form for editing the description of a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 * @see SciOrganization
 */
public class SciOrganizationDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public SciOrganizationDescriptionEditForm(ItemSelectionModel itemModel) {
        super("sciorganizationEditDescForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.description")));
        ParameterModel descParam = new StringParameter(
                SciOrganization.ORGANIZATION_DESCRIPTION);
        TextArea desc;
        if (SciOrganization.getConfig().getOrganizationDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciOrganization orga = (SciOrganization) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciOrganization.ORGANIZATION_DESCRIPTION,
                 orga.getOrganizationDescription());

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();

        SciOrganization orga = (SciOrganization) getItemSelectionModel().
                getSelectedObject(state);

        if ((orga != null) && this.getSaveCancelSection().getSaveButton().
                isSelected(state)) {
            orga.setOrganizationDescription((String) data.get(
                    SciOrganization.ORGANIZATION_DESCRIPTION));

            orga.save();

            init(fse);
        }
    }
}
