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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;

/**
 * Form for editing the basic properties of a {@link SciDepartment}.
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 */
public class SciDepartmentPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener {

    public static final String ID = "SciDepartmentEdit";

    public SciDepartmentPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciDepartmentPropertyForm(ItemSelectionModel itemModel,
                                     SciDepartmentPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        Label descLabel = new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.department.shortdescription"));
        add(descLabel);
        ParameterModel descParam = new StringParameter(
                SciDepartment.DEPARTMENT_SHORT_DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.addValidationListener(new StringInRangeValidationListener(0, 500));
        desc.setCols(75);
        desc.setRows(5);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciDepartment department = (SciDepartment) super.initBasicWidgets(fse);

        data.put(SciDepartment.DEPARTMENT_SHORT_DESCRIPTION,
                 department.getDepartmentShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciDepartment department =
                      (SciDepartment) super.processBasicWidgets(fse);

        if ((department != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            department.setDepartmentShortDescription(
                    (String) data.get(SciDepartment.DEPARTMENT_SHORT_DESCRIPTION));

            department.save();

            init(fse);
        }
    }

    @Override
    protected String getTitleLabel() {
        return (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.department.title").localize();
    }
}
