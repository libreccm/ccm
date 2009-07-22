package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.authoring.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitChooseAssistentDirectorStep extends SimpleEditStep {

    private static String EDIT_SHEET_NAME = "edit";

    public OrganizationalUnitChooseAssistentDirectorStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        OrganizationalUnitChooseAssistentDirectorTable table = new OrganizationalUnitChooseAssistentDirectorTable(itemModel, this);

        addComponent(EDIT_SHEET_NAME, "Select assistent director", new WorkflowLockedComponentAccess(table, itemModel));

        setDisplayComponent(getOrganizationalUnitPropertiesSheet(itemModel));
    }

    private Component getOrganizationalUnitPropertiesSheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_title_pre"), "assistentDirection.titlepre");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_givenname"), "assistentDirection.givenname");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_surname"), "assistentDirection.surname");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.director_title_post"), "assistentDirection.titlepost");

        return sheet;
    }
}
