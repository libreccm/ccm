/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

/**
 *
 * @author Jens Pelzetter
 */
public class OrganizationPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public OrganizationPropertiesStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                   parent),
                "cms.contenttypes.ui.orgaunit.contact");
        addStep(new OrganizationDepartmentPropertiesStep(itemModel,
                                                         parent),
                "cms.contenttypes.ui.orgaunit.departments");
        addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
                                                                  parent),
                "cms.contenttypes.ui.orgaunit.persons");
    }
}
