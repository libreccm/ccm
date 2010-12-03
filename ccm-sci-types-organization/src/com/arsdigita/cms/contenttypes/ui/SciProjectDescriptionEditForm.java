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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form for editing the description of a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public SciProjectDescriptionEditForm(ItemSelectionModel itemModel) {
        super("sciprojectEditDescForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.description")));
        ParameterModel descParam = new StringParameter(
                SciProject.PROJECT_DESCRIPTION);
        TextArea desc;
        if (SciProject.getConfig().getProjectDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);

        if (!SciProject.getConfig().getProjectFundingHide()) {
            add(new Label(SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.funding")));
            ParameterModel fundingParam = new StringParameter(
                    SciProject.FUNDING);
            TextArea funding;
            if (SciProject.getConfig().getProjectFundingDhtml()) {
                funding = new CMSDHTMLEditor(fundingParam);
            } else {
                funding = new TextArea(fundingParam);
            }
            funding.setCols(75);
            funding.setRows(8);
            add(funding);
        }
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciProject.PROJECT_DESCRIPTION,
                 project.getProjectDescription());
        if (!SciProject.getConfig().getProjectFundingHide()) {
            data.put(SciProject.FUNDING, project.getFunding());
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if ((project != null) && getSaveCancelSection().getSaveButton().
                isSelected(state)) {
            project.setProjectDescription((String) data.get(
                    SciProject.PROJECT_DESCRIPTION));
            if (!SciProject.getConfig().getProjectFundingHide()) {
                project.setFunding((String) data.get(
                        SciProject.FUNDING));
            }

            project.save();

            init(fse);
        }
    }
}
