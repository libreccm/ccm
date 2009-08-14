package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
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

        addComponent(EDIT_SHEET_NAME, OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.selectDirector").localize().toString(), new WorkflowLockedComponentAccess(table, itemModel));

        setDisplayComponent(getOrganizationalUnitPropertiesSheet(itemModel));
    }

    private Component getOrganizationalUnitPropertiesSheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

          sheet.add(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.director_title_pre"), "assistentDirection.titlepre");
        sheet.add(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.director_givenname"), "assistentDirection.givenname");
        sheet.add(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.director_surname"), "assistentDirection.surname");
        sheet.add(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.director_title_post"), "assistentDirection.titlepost");

        return sheet;
    }
}
