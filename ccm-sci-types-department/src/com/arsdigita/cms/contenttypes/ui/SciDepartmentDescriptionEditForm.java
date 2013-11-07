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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentConfig;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Edit form the description of an SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private final static SciDepartmentConfig config = SciDepartment.getConfig();

    public SciDepartmentDescriptionEditForm(final ItemSelectionModel itemModel) {
        super("SciDepartmentDescriptionEditForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.description")));
        final ParameterModel descParam = new StringParameter(
                SciDepartment.DEPARTMENT_DESCRIPTION);
        final TextArea desc;
        if (config.getEnableDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciDepartment department = (SciDepartment) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciDepartment.DEPARTMENT_DESCRIPTION, department.
                getDepartmentDescription());

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciDepartment department = (SciDepartment) getItemSelectionModel().
                getSelectedObject(state);

        if ((department != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            department.setDepartmentDescription((String) data.get(
                    SciDepartment.DEPARTMENT_DESCRIPTION));

            department.save();
        }

        init(fse);
    }
}
