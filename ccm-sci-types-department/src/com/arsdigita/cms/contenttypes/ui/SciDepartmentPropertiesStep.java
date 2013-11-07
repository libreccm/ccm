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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step for editing the basic properties of a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciDepartmentPropertiesStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getSciDepartmentPropertySheet(
            final ItemSelectionModel itemModel) {

        final DomainObjectPropertySheet sheet =
                                        (DomainObjectPropertySheet) GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(itemModel);

        sheet.add(SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.shortdesc"),
                  SciDepartment.DEPARTMENT_SHORT_DESCRIPTION);

        return sheet;
    }

    @Override
    public void addBasicProperties(final ItemSelectionModel itemModel,
                                   final AuthoringKitWizard parent) {

        final SimpleEditStep basicProperties =
                             new SimpleEditStep(itemModel,
                                                parent,
                                                EDIT_SHEET_NAME);

        final BasicPageForm editBasicSheet = new SciDepartmentPropertyForm(
                itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(getSciDepartmentPropertySheet(
                itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.edit_basic_properties").localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(final ItemSelectionModel itemModel,
                            final AuthoringKitWizard parent) {
        addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                   parent),
                SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.contacts"));
    }
}
