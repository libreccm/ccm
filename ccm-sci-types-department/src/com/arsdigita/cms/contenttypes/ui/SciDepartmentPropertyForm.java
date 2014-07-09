/*
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentConfig;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Form for editing the the basic properties of a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public final static String ID = "SciDepartmentEdit";
    private final static SciDepartmentConfig config = SciDepartment.getConfig();

    public SciDepartmentPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciDepartmentPropertyForm(final ItemSelectionModel itemModel,
                                     final SciDepartmentPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {

        super.addWidgets();
        
        ParameterModel shortDescParam = new StringParameter(
                SciDepartment.DEPARTMENT_SHORT_DESCRIPTION);
        TextArea shortDesc = new TextArea(shortDescParam);
        shortDesc.addValidationListener(new StringInRangeValidationListener(
                                                0,
                                                config.getShortDescMaxLength()));
        shortDesc.setLabel(SciDepartmentGlobalizationUtil.globalize(
                           "scidepartment.ui.shortdesc"));
        shortDesc.setCols(75);
        shortDesc.setRows(5);
        add(shortDesc);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        final FormData data = fse.getFormData();
        final SciDepartment department = (SciDepartment) super.initBasicWidgets(
                fse);

        data.put(SciDepartment.DEPARTMENT_SHORT_DESCRIPTION,
                 department.getDepartmentShortDescription());
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final SciDepartment department = (SciDepartment) super.
                processBasicWidgets(fse);

        if ((department != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            department.setDepartmentShortDescription((String) data.get(
                    SciDepartment.DEPARTMENT_SHORT_DESCRIPTION));

            department.save();
        }

        init(fse);
    }

    @Override
    public GlobalizedMessage getTitleLabel() {
        return SciDepartmentGlobalizationUtil.globalize("scidepartment.ui.title");
    }
}
