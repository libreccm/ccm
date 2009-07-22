package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitChooseDirectorStep extends SimpleEditStep {

    private static String EDIT_SHEET_NAME = "edit";

    public OrganizationalUnitChooseDirectorStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        OrganizationalUnitChooseDirectorTable table = new OrganizationalUnitChooseDirectorTable(itemModel, this);

        addComponent(EDIT_SHEET_NAME, "Select Director", new WorkflowLockedComponentAccess(table, itemModel));

        setDisplayComponent(getOrganizationalUnitPropertiesSheet(itemModel));
    }

    private Component getOrganizationalUnitPropertiesSheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_title_pre"), "direction.titlepre");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_givenname"), "direction.givenname");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_surname"), "direction.surname");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_title_post"), "direction.titlepost");
        
        return sheet;
    }
}
